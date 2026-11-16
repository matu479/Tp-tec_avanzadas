// Pacientes: tabla de /api/pacientes.
// ADMIN: alta, edición y baja (POST, PUT, DELETE). PROFESIONAL: "Ver expediente" y "Ver métricas".
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { actualizarPaciente, crearPaciente, eliminarPaciente, listarPacientes } from '../api/pacientes.js';
import { useAuth } from '../auth/AuthContext.jsx';
import Formulario from '../componentes/Formulario.jsx';
import MensajeError from '../componentes/MensajeError.jsx';
import MensajeOk from '../componentes/MensajeOk.jsx';
import Tabla from '../componentes/Tabla.jsx';
import useCarga from '../componentes/useCarga.js';
import { fecha } from '../formato.js';

const VACIO = { email: '', password: '', apellido: '', nombre: '', dni: '', fechaNacimiento: '' };

export default function Pacientes() {
  const { usuario } = useAuth();
  const esAdmin = usuario.rol === 'ADMIN';
  const navegar = useNavigate();
  const { datos: pacientes, error: errorCarga, recargar } = useCarga(listarPacientes);
  const [valores, setValores] = useState(VACIO);
  const [editando, setEditando] = useState(null); // id del paciente que se edita, o null (alta)
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  const campos = [
    // autocomplete: que Chrome no complete el alta con el email y la contraseña del usuario logueado
    { nombre: 'email', etiqueta: 'Email', tipo: 'email', requerido: true, autocompletar: 'off' },
    ...(editando ? [] : [{ nombre: 'password', etiqueta: 'Contraseña', tipo: 'password', requerido: true, autocompletar: 'new-password' }]),
    { nombre: 'apellido', etiqueta: 'Apellido', requerido: true },
    { nombre: 'nombre', etiqueta: 'Nombre', requerido: true },
    { nombre: 'dni', etiqueta: 'DNI', requerido: true },
    { nombre: 'fechaNacimiento', etiqueta: 'Fecha de nacimiento', tipo: 'date', requerido: true },
  ];

  async function ejecutar(accion, mensajeOk) {
    setError('');
    setOk('');
    try {
      await accion();
      setOk(mensajeOk);
      setValores(VACIO);
      setEditando(null);
      recargar();
    } catch (e) {
      setError(e.message);
    }
  }

  const guardar = () =>
    editando
      ? ejecutar(() => actualizarPaciente(editando, valores), 'Paciente actualizado')
      : ejecutar(() => crearPaciente(valores), 'Paciente creado (con su expediente clínico)');

  function editar(p) {
    setEditando(p.id);
    setValores({ ...p, password: '' });
  }

  function borrar(p) {
    if (window.confirm(`¿Dar de baja a ${p.nombre} ${p.apellido}?`)) {
      ejecutar(() => eliminarPaciente(p.id), 'Paciente eliminado');
    }
  }

  const columnas = [
    { titulo: 'Apellido', valor: (p) => p.apellido },
    { titulo: 'Nombre', valor: (p) => p.nombre },
    { titulo: 'DNI', valor: (p) => p.dni },
    { titulo: 'Nacimiento', valor: (p) => fecha(p.fechaNacimiento) },
    {
      titulo: 'Acciones',
      clase: 'acciones',
      valor: (p) =>
        esAdmin ? (
          <>
            <button className="secundario" onClick={() => editar(p)}>Editar</button>
            <button className="peligro" onClick={() => borrar(p)}>Baja</button>
          </>
        ) : (
          <>
            <button onClick={() => navegar(`/expedientes?paciente=${p.id}`)}>Ver expediente</button>
            <button onClick={() => navegar(`/metricas?paciente=${p.id}`)}>Ver métricas</button>
          </>
        ),
    },
  ];

  return (
    <section>
      <h2>Pacientes</h2>
      <MensajeError mensaje={errorCarga || error} />
      <MensajeOk mensaje={ok} />
      <Tabla columnas={columnas} filas={pacientes} vacio="No hay pacientes." />
      {esAdmin && (
        <Formulario
          titulo={editando ? 'Editar paciente' : 'Nuevo paciente'}
          campos={campos}
          valores={valores}
          alCambiar={(campo, valor) => setValores({ ...valores, [campo]: valor })}
          alEnviar={guardar}
          textoBoton={editando ? 'Guardar cambios' : 'Dar de alta'}
          alCancelar={editando ? () => { setEditando(null); setValores(VACIO); } : null}
        />
      )}
    </section>
  );
}
