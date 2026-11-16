#!/usr/bin/env bash
# =====================================================================
# SMOKE TEST (prueba de humo automática) - TP Telemedicina
# Requiere: los 8 contenedores levantados (docker compose up -d --build) y curl
# Uso:      bash scripts/smoke-test.sh
# Recorre:  el front responde -> login -> reservar cita -> métrica crítica -> aparece la alerta -> hay un mail en MailHog
# Al terminar (bien o mal) deja limpio lo que creó: cancela la cita y marca la alerta como ATENDIDA.
# =====================================================================
set -u

# Puertos: los del archivo .env si existe, si no los de por defecto
DIR="$(cd "$(dirname "$0")/.." && pwd)"
puerto() { local v; v=$(grep -E "^$1=[0-9]+" "$DIR/.env" 2>/dev/null | head -1 | cut -d= -f2 | tr -d '\r'); echo "${v:-$2}"; }
CORE_URL="${CORE_URL:-http://localhost:$(puerto CORE_PUERTO 8080)}"
METRICAS_URL="${METRICAS_URL:-http://localhost:$(puerto METRICAS_PUERTO 8081)}"
MAILHOG_URL="${MAILHOG_URL:-http://localhost:$(puerto MAILHOG_PUERTO 8025)}"
FRONT_URL="${FRONT_URL:-http://localhost:$(puerto FRONT_PUERTO 3000)}"

ID_PACIENTE=6       # Lucía Díaz (datos de prueba)
ID_PROFESIONAL=2    # Clínica médica
ID_DISPOSITIVO=5    # Oxímetro de Lucía

paso()  { echo "-> $1"; }
ok()    { echo "   OK: $1"; }
falla() { echo "   FALLO: $1"; echo "SMOKE TEST: FALLO"; exit 1; }
# post URL JSON -> deja el cuerpo en $RESP y devuelve el código HTTP en $CODIGO
post() { RESP=$(curl -s -w '\n%{http_code}' -H 'Content-Type: application/json' -d "$2" "$1"); CODIGO=$(echo "$RESP" | tail -1); RESP=$(echo "$RESP" | sed '$d'); }
contar_alertas() { curl -s "$CORE_URL/api/alertas?idPaciente=$ID_PACIENTE" | grep -o '"id":' | wc -l; }
contar_mails()   { curl -s "$MAILHOG_URL/api/v2/messages?limit=1" | grep -o '"total":[0-9]*' | cut -d: -f2; }

# Limpieza: al salir (bien o mal) cancela la cita y atiende la alerta que creó el test.
# No hay DELETE de citas en la API: se cancela, que es la operación que ofrece el sistema.
ID_CITA=""; ID_ALERTA=""
limpiar() {
  if [ -n "$ID_CITA" ]; then
    curl -s -o /dev/null -X PATCH "$CORE_URL/api/citas/$ID_CITA/cancelar" && echo "   (limpieza) cita $ID_CITA cancelada"
  fi
  if [ -n "$ID_ALERTA" ]; then
    curl -s -o /dev/null -X PATCH "$CORE_URL/api/alertas/$ID_ALERTA/atender" && echo "   (limpieza) alerta $ID_ALERTA marcada como ATENDIDA"
  fi
}
trap limpiar EXIT

paso "0. El frontend responde ($FRONT_URL)"
CODIGO=$(curl -s -o /dev/null -w "%{http_code}" "$FRONT_URL/")
[ "$CODIGO" = "200" ] || falla "el front respondió $CODIGO"
curl -s "$FRONT_URL/" | grep -q 'id="root"' || falla "el front no devolvió la página de React"
curl -s "$FRONT_URL/api/ping" | grep -q core-service || falla "el proxy del front no llega al core"
ok "200 con la app React, y el proxy /api llega al core"

paso "1. Login del admin ($CORE_URL)"
post "$CORE_URL/api/auth/login" '{"email":"admin@tp.com","password":"Admin123!"}'
[ "$CODIGO" = "200" ] || falla "login respondió $CODIGO"
ok "login correcto"

paso "2. Reservar una cita"
DIAS=$(( (RANDOM % 600) + 30 )); HORA=$(( (RANDOM % 10) + 8 )); MIN=$(( RANDOM % 60 ))
FECHA=$(date -d "+$DIAS days" +%Y-%m-%d 2>/dev/null || date -v+"$DIAS"d +%Y-%m-%d)
FECHA=$(printf '%sT%02d:%02d:00' "$FECHA" "$HORA" "$MIN")
post "$CORE_URL/api/citas" "{\"idPaciente\":$ID_PACIENTE,\"idProfesional\":$ID_PROFESIONAL,\"fechaHora\":\"$FECHA\",\"motivo\":\"Smoke test\"}"
[ "$CODIGO" = "201" ] || falla "reservar respondió $CODIGO: $RESP"
ID_CITA=$(echo "$RESP" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)
ok "cita $ID_CITA reservada para el $FECHA"

ALERTAS_ANTES=$(contar_alertas); MAILS_ANTES=$(contar_mails)

paso "3. Enviar una métrica crítica (saturación 86 %) ($METRICAS_URL)"
post "$METRICAS_URL/api/metricas" "{\"idDispositivo\":$ID_DISPOSITIVO,\"idPaciente\":$ID_PACIENTE,\"tipo\":\"SATURACION\",\"valor\":86,\"unidad\":\"%\"}"
[ "$CODIGO" = "201" ] || falla "metricas respondió $CODIGO: $RESP"
echo "$RESP" | grep -q '"superaUmbral":true' || falla "la métrica no superó el umbral"
ok "métrica guardada y fuera de rango"

paso "4. Esperar a que aparezca la alerta en el core"
for _ in $(seq 1 20); do
  [ "$(contar_alertas)" -gt "$ALERTAS_ANTES" ] && break
  sleep 1
done
[ "$(contar_alertas)" -gt "$ALERTAS_ANTES" ] || falla "no apareció la alerta en 20 segundos"
# La lista viene de la más nueva a la más vieja: la primera es la que acaba de generarse
ID_ALERTA=$(curl -s "$CORE_URL/api/alertas?idPaciente=$ID_PACIENTE" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)
ok "alerta $ID_ALERTA registrada"

paso "5. Verificar el mail en MailHog ($MAILHOG_URL)"
for _ in $(seq 1 10); do
  [ "$(contar_mails)" -gt "$MAILS_ANTES" ] && break
  sleep 1
done
[ "$(contar_mails)" -gt "$MAILS_ANTES" ] || falla "no llegó ningún mail nuevo a MailHog"
ok "hay mail nuevo en MailHog"

paso "6. Limpiar los datos de prueba"
limpiar
ID_CITA=""; ID_ALERTA=""   # ya se limpió: que el trap de salida no lo repita

echo "SMOKE TEST: OK"
