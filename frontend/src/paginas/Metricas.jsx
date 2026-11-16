// Visualización de Métricas: GET /api/pacientes/{id}/metricas?tipo&desde (a través del core).
// Gráfico de línea (recharts) con la banda del rango normal; los puntos fuera de rango van en rojo.
// Se actualiza cada 5 s (polling). Si el core responde 503, se avisa sin romper la página.
import { useEffect, useState } from 'react';
import { useAuth } from '../auth/AuthContext.jsx';
import { metricasDelPaciente } from '../api/metricas.js';
import { UMBRALES } from '../api/umbrales.js';
import MensajeError from '../componentes/MensajeError.jsx';
import SelectorPaciente, { usePacienteDeLaUrl } from '../componentes/SelectorPaciente.jsx';
import usePolling from '../componentes/usePolling.js';
import { aIsoLocal } from '../formato.js';
import GraficoMetricas from './metricas/GraficoMetricas.jsx';
import UltimasMediciones from './metricas/UltimasMediciones.jsx';

const RANGOS = { '1h': { texto: 'Última hora', horas: 1 }, '24h': { texto: 'Últimas 24 h', horas: 24 } };

export default function Metricas() {
  const { usuario } = useAuth();
  const esProfesional = usuario.rol === 'PROFESIONAL';
  const [pacienteUrl, setPacienteUrl] = usePacienteDeLaUrl();
  const idPaciente = esProfesional ? pacienteUrl : usuario.idUsuario;
  const [tipo, setTipo] = useState('SATURACION');
  const [rango, setRango] = useState('1h');
  const [metricas, setMetricas] = useState([]);
  const [error, setError] = useState('');

  // Al cambiar de paciente, tipo o rango se vacía el gráfico (para no mezclar datos con otros umbrales).
  useEffect(() => setMetricas([]), [idPaciente, tipo, rango]);

  usePolling(async () => {
    if (!idPaciente) return;
    const desde = aIsoLocal(new Date(Date.now() - RANGOS[rango].horas * 3600000));
    try {
      setMetricas(await metricasDelPaciente(idPaciente, tipo, desde));
      setError('');
    } catch (e) {
      setError(e.status === 503 ? 'El servicio de métricas no está disponible' : e.message);
    }
  }, 5000, [idPaciente, tipo, rango]);

  return (
    <section>
      <h2>{esProfesional ? 'Visualización de Métricas' : 'Mis métricas'}</h2>
      <div className="filtros">
        {esProfesional && <SelectorPaciente valor={idPaciente} alCambiar={setPacienteUrl} />}
        <label>
          Tipo
          <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
            {Object.entries(UMBRALES).map(([clave, u]) => <option key={clave} value={clave}>{u.etiqueta}</option>)}
          </select>
        </label>
        <label>
          Rango
          <select value={rango} onChange={(e) => setRango(e.target.value)}>
            {Object.entries(RANGOS).map(([clave, r]) => <option key={clave} value={clave}>{r.texto}</option>)}
          </select>
        </label>
      </div>
      <MensajeError mensaje={error} />
      {!idPaciente && <p>Elegí un paciente.</p>}
      {idPaciente && (
        <>
          <GraficoMetricas metricas={metricas} tipo={tipo} />
          <UltimasMediciones metricas={metricas} tipo={tipo} />
          <p className="ayuda">Se actualiza solo cada 5 segundos.</p>
        </>
      )}
    </section>
  );
}
