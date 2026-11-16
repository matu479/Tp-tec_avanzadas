// Una consulta del historial. Si es editable (PROFESIONAL): editar diagnóstico y agregar indicación/observación.
import { useState } from 'react';
import { agregarIndicacion, agregarObservacion, registrarDiagnostico } from '../../api/consultas.js';
import MensajeError from '../../componentes/MensajeError.jsx';
import { fechaHora } from '../../formato.js';

const ACCIONES = {
  diagnostico: { titulo: 'Editar diagnóstico', funcion: registrarDiagnostico },
  indicacion: { titulo: 'Agregar indicación', funcion: agregarIndicacion },
  observacion: { titulo: 'Agregar observación', funcion: agregarObservacion },
};

export default function ConsultaDelHistorial({ consulta, editable, alCambiar }) {
  const [accion, setAccion] = useState(null); // 'diagnostico' | 'indicacion' | 'observacion'
  const [texto, setTexto] = useState('');
  const [error, setError] = useState('');

  function abrir(clave) {
    setAccion(clave);
    setTexto(clave === 'diagnostico' ? consulta.diagnostico?.descripcion || '' : '');
    setError('');
  }

  async function guardar(evento) {
    evento.preventDefault();
    try {
      await ACCIONES[accion].funcion(consulta.id, texto);
      setAccion(null);
      alCambiar();
    } catch (e) {
      setError(e.message);
    }
  }

  const { profesional } = consulta;
  return (
    <div className="consulta">
      <strong>{fechaHora(consulta.fechaConsulta)}</strong> — {profesional.nombre} {profesional.apellido} ({profesional.especialidad})
      <p><strong>Diagnóstico:</strong> {consulta.diagnostico?.descripcion || 'Sin diagnóstico'}</p>
      <p><strong>Indicaciones:</strong> {consulta.indicaciones || '—'}</p>
      <p><strong>Observaciones:</strong> {consulta.observaciones || '—'}</p>
      {editable && !accion && (
        <div className="botones">
          {Object.entries(ACCIONES).map(([clave, a]) => (
            <button key={clave} className="secundario" onClick={() => abrir(clave)}>
              {a.titulo}
            </button>
          ))}
        </div>
      )}
      {accion && (
        <form onSubmit={guardar}>
          <label>
            {ACCIONES[accion].titulo}
            <textarea rows={2} value={texto} onChange={(e) => setTexto(e.target.value)} required />
          </label>
          <MensajeError mensaje={error} />
          <div className="botones">
            <button type="submit">Guardar</button>
            <button type="button" className="secundario" onClick={() => setAccion(null)}>Cancelar</button>
          </div>
        </form>
      )}
    </div>
  );
}
