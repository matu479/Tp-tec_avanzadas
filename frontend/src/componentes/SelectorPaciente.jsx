// Lista desplegable de pacientes (GET /api/pacientes), para que el PROFESIONAL elija a quién ver.
// El paciente elegido se guarda en la URL (?paciente=4), así funcionan los botones de la pantalla Pacientes.
import { useSearchParams } from 'react-router-dom';
import { listarPacientes } from '../api/pacientes.js';
import useCarga from './useCarga.js';

export function usePacienteDeLaUrl() {
  const [parametros, setParametros] = useSearchParams();
  const id = parametros.get('paciente');
  return [id ? Number(id) : null, (nuevo) => setParametros(nuevo ? { paciente: nuevo } : {})];
}

export default function SelectorPaciente({ valor, alCambiar }) {
  const { datos: pacientes } = useCarga(listarPacientes);
  return (
    <label>
      Paciente
      <select value={valor ?? ''} onChange={(e) => alCambiar(e.target.value ? Number(e.target.value) : null)}>
        <option value="">Elegí un paciente</option>
        {(pacientes || []).map((p) => (
          <option key={p.id} value={p.id}>
            {p.apellido}, {p.nombre} (DNI {p.dni})
          </option>
        ))}
      </select>
    </label>
  );
}
