// Funciones chicas para mostrar fechas en formato argentino.

/** "2026-11-18T10:00:00" → "18/11/2026 10:00" */
export function fechaHora(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleString('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit', hour12: false });
}

/** "1983-04-12" → "12/04/1983" (sin pasar por Date para no correr el día por la zona horaria) */
export function fecha(iso) {
  if (!iso) return '';
  const [anio, mes, dia] = iso.slice(0, 10).split('-');
  return `${dia}/${mes}/${anio}`;
}

/** Fecha y hora local en el formato que espera el backend (LocalDateTime, sin zona): "2026-11-16T15:30:00" */
export function aIsoLocal(fechaJs) {
  const dos = (n) => String(n).padStart(2, '0');
  return `${fechaJs.getFullYear()}-${dos(fechaJs.getMonth() + 1)}-${dos(fechaJs.getDate())}T${dos(fechaJs.getHours())}:${dos(fechaJs.getMinutes())}:${dos(fechaJs.getSeconds())}`;
}
