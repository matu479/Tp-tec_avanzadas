// "Mi agenda" del PROFESIONAL: GET /api/citas?idProfesional={id}.
// En las RESERVADAS, "Registrar consulta" abre el formulario → POST /api/consultas.
import { useState } from 'react';
import { listarCitas } from '../../api/citas.js';
import { registrarConsulta } from '../../api/consultas.js';
import BadgeEstado from '../../componentes/BadgeEstado.jsx';
import Formulario from '../../componentes/Formulario.jsx';
import MensajeError from '../../componentes/MensajeError.jsx';
import MensajeOk from '../../componentes/MensajeOk.jsx';
import Tabla from '../../componentes/Tabla.jsx';
import useCarga from '../../componentes/useCarga.js';
import { fechaHora } from '../../formato.js';

const VACIO = { observaciones: '', indicaciones: '', diagnostico: '' };

export default function AgendaProfesional({ idProfesional }) {
  const { datos: citas, error: errorCarga, recargar } = useCarga(() => listarCitas(idProfesional), [idProfesional]);
  const [citaElegida, setCitaElegida] = useState(null);
  const [valores, setValores] = useState(VACIO);
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  async function guardarConsulta() {
    setError('');
    setOk('');
    try {
      await registrarConsulta({ idCita: citaElegida.id, ...valores });
      setOk(`Consulta registrada para ${citaElegida.nombrePaciente}. La cita quedó ATENDIDA.`);
      setCitaElegida(null);
      setValores(VACIO);
      recargar();
    } catch (e) {
      setError(e.message);
    }
  }

  const columnas = [
    { titulo: 'Fecha y hora', valor: (c) => fechaHora(c.fechaHora) },
    { titulo: 'Paciente', valor: (c) => c.nombrePaciente },
    { titulo: 'Motivo', valor: (c) => c.motivo },
    { titulo: 'Estado', valor: (c) => <BadgeEstado valor={c.estado} /> },
    {
      titulo: 'Acciones',
      clase: 'acciones',
      valor: (c) =>
        c.estado === 'RESERVADA' && <button onClick={() => setCitaElegida(c)}>Registrar consulta</button>,
    },
  ];

  return (
    <section>
      <h2>Mi agenda</h2>
      <MensajeError mensaje={errorCarga || error} />
      <MensajeOk mensaje={ok} />
      {citaElegida && (
        <Formulario
          titulo={`Consulta de ${citaElegida.nombrePaciente} (${fechaHora(citaElegida.fechaHora)})`}
          campos={[
            { nombre: 'observaciones', etiqueta: 'Observaciones', tipo: 'textarea' },
            { nombre: 'indicaciones', etiqueta: 'Indicaciones', tipo: 'textarea' },
            { nombre: 'diagnostico', etiqueta: 'Diagnóstico', tipo: 'textarea' },
          ]}
          valores={valores}
          alCambiar={(campo, valor) => setValores({ ...valores, [campo]: valor })}
          alEnviar={guardarConsulta}
          textoBoton="Guardar consulta"
          alCancelar={() => setCitaElegida(null)}
        />
      )}
      <Tabla columnas={columnas} filas={citas} vacio="No tenés citas en tu agenda." />
    </section>
  );
}
