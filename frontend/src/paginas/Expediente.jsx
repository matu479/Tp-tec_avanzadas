// Expedientes clínicos. Se carga con GraphQL (ver api/expedientes.js): solo los campos que se muestran.
// PACIENTE: "Mi expediente", solo lectura. PROFESIONAL: elige un paciente y puede editar el diagnóstico
// (PUT /api/consultas/{id}/diagnostico) y agregar indicaciones u observaciones (PATCH).
import { useAuth } from '../auth/AuthContext.jsx';
import { obtenerExpediente } from '../api/expedientes.js';
import MensajeError from '../componentes/MensajeError.jsx';
import SelectorPaciente, { usePacienteDeLaUrl } from '../componentes/SelectorPaciente.jsx';
import useCarga from '../componentes/useCarga.js';
import ConsultaDelHistorial from './expediente/ConsultaDelHistorial.jsx';

export default function Expediente() {
  const { usuario } = useAuth();
  const esProfesional = usuario.rol === 'PROFESIONAL';
  const [pacienteUrl, setPacienteUrl] = usePacienteDeLaUrl();
  const idPaciente = esProfesional ? pacienteUrl : usuario.idUsuario;

  return (
    <section>
      <h2>{esProfesional ? 'Expedientes clínicos' : 'Mi expediente'}</h2>
      {esProfesional && (
        <div className="filtros">
          <SelectorPaciente valor={idPaciente} alCambiar={setPacienteUrl} />
        </div>
      )}
      {idPaciente ? <Historial idPaciente={idPaciente} editable={esProfesional} /> : <p>Elegí un paciente.</p>}
    </section>
  );
}

function Historial({ idPaciente, editable }) {
  const { datos: paciente, error, recargar } = useCarga(() => obtenerExpediente(idPaciente), [idPaciente]);
  if (error) return <MensajeError mensaje={error} />;
  if (!paciente) return <p>Cargando...</p>;
  const { expediente } = paciente;
  return (
    <>
      <div className="panel">
        <h3>
          {paciente.nombre} {paciente.apellido}
        </h3>
        <p><strong>Antecedentes:</strong> {expediente.antecedentes || '—'}</p>
        <p><strong>Observaciones generales:</strong> {expediente.observacionesGenerales || '—'}</p>
      </div>
      <h3>Historial de consultas ({expediente.consultas.length})</h3>
      {expediente.consultas.length === 0 && <p>Todavía no hay consultas registradas.</p>}
      {expediente.consultas.map((c) => (
        <ConsultaDelHistorial key={c.id} consulta={c} editable={editable} alCambiar={recargar} />
      ))}
    </>
  );
}
