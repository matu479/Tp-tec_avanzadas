// "Mis citas" del PACIENTE: GET /api/pacientes/{id}/citas.
// Reservar (POST /api/citas), cancelar y reprogramar (PATCH) las RESERVADAS.
// Los errores del backend (409 turno ocupado, 422 fecha pasada) se muestran tal cual.
import { useState } from 'react';
import { cancelarCita, reprogramarCita, reservarCita } from '../../api/citas.js';
import { citasDelPaciente } from '../../api/pacientes.js';
import { listarProfesionales } from '../../api/profesionales.js';
import BadgeEstado from '../../componentes/BadgeEstado.jsx';
import Formulario from '../../componentes/Formulario.jsx';
import MensajeError from '../../componentes/MensajeError.jsx';
import MensajeOk from '../../componentes/MensajeOk.jsx';
import Tabla from '../../componentes/Tabla.jsx';
import useCarga from '../../componentes/useCarga.js';
import { fechaHora } from '../../formato.js';

const VACIO = { idProfesional: '', fechaHora: '', motivo: '' };

export default function MisCitas({ idPaciente }) {
  const { datos: citas, error: errorCarga, recargar } = useCarga(() => citasDelPaciente(idPaciente), [idPaciente]);
  const { datos: profesionales } = useCarga(listarProfesionales);
  const [valores, setValores] = useState(VACIO);
  const [reprogramando, setReprogramando] = useState(null); // { id, fechaHora }
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  async function ejecutar(accion, mensajeOk) {
    setError('');
    setOk('');
    try {
      await accion();
      setOk(mensajeOk);
      recargar();
      return true;
    } catch (e) {
      setError(e.message);
      return false;
    }
  }

  async function reservar() {
    const pedido = { ...valores, idPaciente, idProfesional: Number(valores.idProfesional) };
    if (await ejecutar(() => reservarCita(pedido), 'Cita reservada')) setValores(VACIO);
  }

  async function confirmarReprogramacion() {
    const { id, fechaHora: nueva } = reprogramando;
    if (await ejecutar(() => reprogramarCita(id, nueva), 'Cita reprogramada')) setReprogramando(null);
  }

  const opcionesProfesionales = (profesionales || []).map((p) => ({
    valor: p.id,
    texto: `${p.apellido}, ${p.nombre} (${p.especialidad})`,
  }));
  const ordenadas = [...(citas || [])].sort((a, b) => a.fechaHora.localeCompare(b.fechaHora));

  const columnas = [
    { titulo: 'Fecha y hora', valor: (c) => fechaHora(c.fechaHora) },
    { titulo: 'Profesional', valor: (c) => `${c.profesional.apellido} (${c.profesional.especialidad})` },
    { titulo: 'Motivo', valor: (c) => c.motivo },
    { titulo: 'Estado', valor: (c) => <BadgeEstado valor={c.estado} /> },
    {
      titulo: 'Acciones',
      clase: 'acciones',
      valor: (c) =>
        c.estado === 'RESERVADA' && (
          <>
            <button className="secundario" onClick={() => setReprogramando({ id: c.id, fechaHora: c.fechaHora.slice(0, 16) })}>
              Reprogramar
            </button>
            <button className="peligro" onClick={() => ejecutar(() => cancelarCita(c.id), 'Cita cancelada')}>
              Cancelar
            </button>
          </>
        ),
    },
  ];

  return (
    <section>
      <h2>Mis citas</h2>
      <MensajeError mensaje={errorCarga || error} />
      <MensajeOk mensaje={ok} />
      <Tabla columnas={columnas} filas={ordenadas} vacio="Todavía no tenés citas." />
      {reprogramando && (
        <Formulario
          titulo="Reprogramar cita"
          campos={[{ nombre: 'fechaHora', etiqueta: 'Nueva fecha y hora', tipo: 'datetime-local', requerido: true }]}
          valores={reprogramando}
          alCambiar={(campo, valor) => setReprogramando({ ...reprogramando, [campo]: valor })}
          alEnviar={confirmarReprogramacion}
          textoBoton="Guardar nueva fecha"
          alCancelar={() => setReprogramando(null)}
        />
      )}
      <Formulario
        titulo="Reservar una cita"
        campos={[
          { nombre: 'idProfesional', etiqueta: 'Profesional', tipo: 'select', opciones: opcionesProfesionales, requerido: true },
          { nombre: 'fechaHora', etiqueta: 'Fecha y hora', tipo: 'datetime-local', requerido: true },
          { nombre: 'motivo', etiqueta: 'Motivo' },
        ]}
        valores={valores}
        alCambiar={(campo, valor) => setValores({ ...valores, [campo]: valor })}
        alEnviar={reservar}
        textoBoton="Reservar"
      />
    </section>
  );
}
