// Tabla con las últimas 10 mediciones (la más nueva arriba). Las fuera de rango se marcan en rojo.
import Tabla from '../../componentes/Tabla.jsx';
import { UMBRALES, fueraDeRango } from '../../api/umbrales.js';
import { fechaHora } from '../../formato.js';

export default function UltimasMediciones({ metricas, tipo }) {
  const u = UMBRALES[tipo];
  const columnas = [
    { titulo: 'Fecha y hora', valor: (m) => fechaHora(m.fechaHora) },
    { titulo: 'Valor', valor: (m) => `${m.valor} ${u.unidad}` },
    { titulo: 'Dispositivo', valor: (m) => m.idDispositivo },
    { titulo: 'Estado', valor: (m) => (fueraDeRango(tipo, m.valor) ? <span className="badge CRITICA">FUERA DE RANGO</span> : 'Normal') },
  ];
  return (
    <>
      <h3>Últimas 10 mediciones</h3>
      <Tabla columnas={columnas} filas={metricas.slice(0, 10)} claveFila={(m) => m.idMetrica} vacio="Sin mediciones." />
    </>
  );
}
