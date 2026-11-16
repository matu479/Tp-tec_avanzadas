// Etiqueta de color para estados y niveles (RESERVADA, ATENDIDA, CANCELADA, GENERADA, ALTA, CRITICA).
export default function BadgeEstado({ valor }) {
  return <span className={`badge ${valor}`}>{valor}</span>;
}
