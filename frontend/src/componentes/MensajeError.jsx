// Cartel rojo con un mensaje de error (por ejemplo, el "mensaje" que devuelve el backend).
export default function MensajeError({ mensaje }) {
  if (!mensaje) return null;
  return <div className="mensaje-error">{mensaje}</div>;
}
