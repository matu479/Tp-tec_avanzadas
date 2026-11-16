# =====================================================================
# SMOKE TEST (prueba de humo automática) - TP Telemedicina
# Requiere: los 8 contenedores levantados (docker compose up -d --build)
# Uso:      powershell -ExecutionPolicy Bypass -File scripts\smoke-test.ps1
# Recorre:  el front responde -> login -> reservar cita -> métrica crítica -> aparece la alerta -> hay un mail en MailHog
# Al terminar (bien o mal) deja limpio lo que creó: cancela la cita y marca la alerta como ATENDIDA.
# =====================================================================
param(
    [string]$CoreUrl = "",
    [string]$MetricasUrl = "",
    [string]$MailhogUrl = "",
    [string]$FrontUrl = ""
)
$ErrorActionPreference = "Stop"

# Lee un puerto del archivo .env (si existe) o usa el valor por defecto
function Get-Puerto([string]$nombre, [string]$porDefecto) {
    $archivoEnv = Join-Path $PSScriptRoot "..\.env"
    if (Test-Path $archivoEnv) {
        $linea = Get-Content $archivoEnv | Where-Object { $_ -match "^$nombre=(\d+)" } | Select-Object -First 1
        if ($linea) { return ($linea -split "=")[1].Trim() }
    }
    return $porDefecto
}
if (-not $CoreUrl)     { $CoreUrl = "http://localhost:" + (Get-Puerto "CORE_PUERTO" "8080") }
if (-not $MetricasUrl) { $MetricasUrl = "http://localhost:" + (Get-Puerto "METRICAS_PUERTO" "8081") }
if (-not $MailhogUrl)  { $MailhogUrl = "http://localhost:" + (Get-Puerto "MAILHOG_PUERTO" "8025") }
if (-not $FrontUrl)    { $FrontUrl = "http://localhost:" + (Get-Puerto "FRONT_PUERTO" "3000") }

function Paso([string]$texto) { Write-Host "-> $texto" -ForegroundColor Cyan }
function Ok([string]$texto)   { Write-Host "   OK: $texto" -ForegroundColor Green }
# Deshace lo que creó el test: cancela la cita y atiende la alerta (si llegaron a crearse).
# No hay DELETE de citas en la API: se cancela, que es la operación que ofrece el sistema.
$script:idCitaCreada = $null
$script:idAlertaCreada = $null
function Limpiar {
    if ($script:idCitaCreada) {
        try {
            Invoke-RestMethod -Method Patch "$CoreUrl/api/citas/$($script:idCitaCreada)/cancelar" | Out-Null
            Write-Host "   (limpieza) cita $($script:idCitaCreada) cancelada"
        } catch { Write-Host "   (limpieza) no se pudo cancelar la cita: $($_.Exception.Message)" }
        $script:idCitaCreada = $null
    }
    if ($script:idAlertaCreada) {
        try {
            Invoke-RestMethod -Method Patch "$CoreUrl/api/alertas/$($script:idAlertaCreada)/atender" | Out-Null
            Write-Host "   (limpieza) alerta $($script:idAlertaCreada) marcada como ATENDIDA"
        } catch { Write-Host "   (limpieza) no se pudo atender la alerta: $($_.Exception.Message)" }
        $script:idAlertaCreada = $null
    }
}
function Falla([string]$texto) {
    Write-Host "   FALLO: $texto" -ForegroundColor Red
    Limpiar
    Write-Host "SMOKE TEST: FALLO" -ForegroundColor Red
    exit 1
}
function Post-Json([string]$url, $cuerpo) {
    return Invoke-RestMethod -Method Post -Uri $url -ContentType "application/json" -Body ($cuerpo | ConvertTo-Json)
}
# En PowerShell 5.1, Invoke-RestMethod (1) devuelve un array JSON como UN solo objeto en el pipeline y
# (2) lee el texto como Latin-1 si la respuesta no dice "charset" (y Spring no lo dice en application/json),
# así que "Fernández" se vería "FernÃ¡ndez". Por eso se leen los bytes como UTF-8 y se guarda en una variable.
function Get-Alertas([string]$url) {
    $respuesta = Invoke-WebRequest -UseBasicParsing $url
    $lista = [Text.Encoding]::UTF8.GetString($respuesta.RawContentStream.ToArray()) | ConvertFrom-Json
    return ,@($lista)
}

$idPaciente = 6       # Lucía Díaz (datos de prueba)
$idProfesional = 2    # Clínica médica
$idDispositivo = 5    # Oxímetro de Lucía

try {
    Paso "0. El frontend responde ($FrontUrl)"
    $pagina = Invoke-WebRequest -UseBasicParsing "$FrontUrl/"
    if ($pagina.StatusCode -ne 200 -or $pagina.Content -notmatch 'id="root"') { Falla "el front no devolvió la página de React" }
    $ping = Invoke-RestMethod "$FrontUrl/api/ping"
    if ($ping.servicio -ne "core-service") { Falla "el proxy del front no llega al core" }
    Ok "200 con la app React, y el proxy /api llega al core"

    Paso "1. Login del admin ($CoreUrl)"
    $usuario = Post-Json "$CoreUrl/api/auth/login" @{ email = "admin@tp.com"; password = "Admin123!" }
    if ($usuario.rol -ne "ADMIN") { Falla "el login no devolvió el rol ADMIN" }
    Ok "login como $($usuario.nombre) ($($usuario.rol))"

    Paso "2. Reservar una cita"
    $fecha = (Get-Date).AddDays((Get-Random -Minimum 30 -Maximum 700)).Date.AddHours((Get-Random -Minimum 8 -Maximum 19)).AddMinutes((Get-Random -Minimum 0 -Maximum 59))
    $cita = Post-Json "$CoreUrl/api/citas" @{ idPaciente = $idPaciente; idProfesional = $idProfesional; fechaHora = $fecha.ToString("yyyy-MM-ddTHH:mm:00"); motivo = "Smoke test" }
    $script:idCitaCreada = $cita.id
    if ($cita.estado -ne "RESERVADA") { Falla "la cita no quedó RESERVADA" }
    Ok "cita $($cita.id) RESERVADA para el $($cita.fechaHora)"

    $alertasAntes = (Get-Alertas "$CoreUrl/api/alertas?idPaciente=$idPaciente").Count
    $mailsAntes = (Invoke-RestMethod "$MailhogUrl/api/v2/messages?limit=1").total

    Paso "3. Enviar una métrica crítica (saturación 86 %) a metricas-service ($MetricasUrl)"
    $metrica = Post-Json "$MetricasUrl/api/metricas" @{ idDispositivo = $idDispositivo; idPaciente = $idPaciente; tipo = "SATURACION"; valor = 86; unidad = "%" }
    if (-not $metrica.superaUmbral) { Falla "la métrica no superó el umbral" }
    Ok "métrica guardada en MongoDB ($($metrica.idMetrica)) y fuera de rango"

    Paso "4. Esperar a que aparezca la alerta en el core"
    $alertasDespues = $alertasAntes
    for ($i = 0; $i -lt 20 -and $alertasDespues -le $alertasAntes; $i++) {
        Start-Sleep -Seconds 1
        $alertasDespues = (Get-Alertas "$CoreUrl/api/alertas?idPaciente=$idPaciente").Count
    }
    if ($alertasDespues -le $alertasAntes) { Falla "no apareció la alerta en 20 segundos" }
    $alerta = (Get-Alertas "$CoreUrl/api/alertas?idPaciente=$idPaciente")[0]
    $script:idAlertaCreada = $alerta.id
    Ok "alerta $($alerta.id) $($alerta.nivel) registrada (profesional: $($alerta.nombreProfesional))"

    Paso "5. Verificar el mail en MailHog ($MailhogUrl)"
    $mailsDespues = $mailsAntes
    for ($i = 0; $i -lt 10 -and $mailsDespues -le $mailsAntes; $i++) {
        Start-Sleep -Seconds 1
        $mailsDespues = (Invoke-RestMethod "$MailhogUrl/api/v2/messages?limit=1").total
    }
    if ($mailsDespues -le $mailsAntes) { Falla "no llegó ningún mail nuevo a MailHog" }
    Ok "hay $($mailsDespues - $mailsAntes) mail(s) nuevo(s) en MailHog"

    Paso "6. Limpiar los datos de prueba"
    Limpiar

    Write-Host "SMOKE TEST: OK" -ForegroundColor Green
    exit 0
} catch {
    Falla $_.Exception.Message
}
