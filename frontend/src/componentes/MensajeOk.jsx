// Cartel verde de confirmación ("Cita reservada", etc.).
export default function MensajeOk({ mensaje }) {
  if (!mensaje) return null;
  return <div className="mensaje-ok">{mensaje}</div>;
}
