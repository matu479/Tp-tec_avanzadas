// Gráfico de línea de una métrica en el tiempo, con la banda verde del rango normal
// y líneas punteadas en los límites. Los puntos fuera de rango se dibujan en rojo.
import { CartesianGrid, Line, LineChart, ReferenceArea, ReferenceLine, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { UMBRALES, fueraDeRango } from '../../api/umbrales.js';

const hora = (iso) => new Date(iso).toLocaleTimeString('es-AR', { hour12: false });

/** Punto del gráfico: rojo y más grande si está fuera de rango. */
function Punto({ cx, cy, payload, tipo }) {
  const fuera = fueraDeRango(tipo, payload.valor);
  return <circle cx={cx} cy={cy} r={fuera ? 5 : 3} fill={fuera ? '#c62828' : '#1f6feb'} stroke="none" />;
}

export default function GraficoMetricas({ metricas, tipo }) {
  const u = UMBRALES[tipo];
  if (metricas.length === 0) return <p>No hay mediciones de {u.etiqueta.toLowerCase()} en este rango.</p>;
  // El backend las devuelve de la más nueva a la más vieja; el gráfico va de izquierda (vieja) a derecha (nueva).
  const datos = [...metricas].reverse().map((m) => ({ ...m, hora: hora(m.fechaHora) }));
  const valores = datos.map((d) => d.valor);
  const minimo = Math.floor(Math.min(...valores, u.normalMin) - 2);
  const maximo = Math.ceil(Math.max(...valores, u.normalMax) + 2);
  return (
    <div className="grafico">
      <ResponsiveContainer width="100%" height={320}>
        <LineChart data={datos} margin={{ top: 10, right: 20, bottom: 10, left: 0 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="hora" minTickGap={30} />
          <YAxis domain={[minimo, maximo]} unit={` ${u.unidad}`} width={80} />
          <Tooltip formatter={(v) => [`${v} ${u.unidad}`, u.etiqueta]} />
          <ReferenceArea y1={u.normalMin} y2={u.normalMax} fill="#2e7d32" fillOpacity={0.08} />
          <ReferenceLine y={u.normalMin} stroke="#2e7d32" strokeDasharray="4 4" label={`mín ${u.normalMin}`} />
          <ReferenceLine y={u.normalMax} stroke="#2e7d32" strokeDasharray="4 4" label={`máx ${u.normalMax}`} />
          <Line type="monotone" dataKey="valor" stroke="#1f6feb" isAnimationActive={false}
            dot={(props) => <Punto key={props.index} {...props} tipo={tipo} />} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
