// Tabla genérica. columnas: [{ titulo, valor: (fila) => contenido }]. Si no hay filas, muestra "vacio".
export default function Tabla({ columnas, filas, claveFila = (f) => f.id, vacio = 'No hay datos.', claseFila }) {
  if (!filas || filas.length === 0) return <p>{vacio}</p>;
  return (
    <table>
      <thead>
        <tr>
          {columnas.map((c) => (
            <th key={c.titulo}>{c.titulo}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {filas.map((fila) => (
          <tr key={claveFila(fila)} className={claseFila ? claseFila(fila) : ''}>
            {columnas.map((c) => (
              <td key={c.titulo} className={c.clase || ''}>
                {c.valor(fila)}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
