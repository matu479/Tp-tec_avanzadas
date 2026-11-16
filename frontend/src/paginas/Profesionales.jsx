// Profesionales (solo ADMIN): tabla de /api/profesionales + alta, edición y baja.
import { useState } from 'react';
import {
  actualizarProfesional, crearProfesional, eliminarProfesional, listarProfesionales,
} from '../api/profesionales.js';
import Formulario from '../componentes/Formulario.jsx';
import MensajeError from '../componentes/MensajeError.jsx';
import MensajeOk from '../componentes/MensajeOk.jsx';
import Tabla from '../componentes/Tabla.jsx';
import useCarga from '../componentes/useCarga.js';

const VACIO = { email: '', password: '', apellido: '', nombre: '', matricula: '', especialidad: '' };

export default function Profesionales() {
  const { datos: profesionales, error: errorCarga, recargar } = useCarga(listarProfesionales);
  const [valores, setValores] = useState(VACIO);
  const [editando, setEditando] = useState(null);
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  const campos = [
    // autocomplete: que Chrome no complete el alta con el email y la contraseña del usuario logueado
    { nombre: 'email', etiqueta: 'Email', tipo: 'email', requerido: true, autocompletar: 'off' },
    ...(editando ? [] : [{ nombre: 'password', etiqueta: 'Contraseña', tipo: 'password', requerido: true, autocompletar: 'new-password' }]),
    { nombre: 'apellido', etiqueta: 'Apellido', requerido: true },
    { nombre: 'nombre', etiqueta: 'Nombre', requerido: true },
    { nombre: 'matricula', etiqueta: 'Matrícula', requerido: true },
    { nombre: 'especialidad', etiqueta: 'Especialidad', requerido: true },
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
      ? ejecutar(() => actualizarProfesional(editando, valores), 'Profesional actualizado')
      : ejecutar(() => crearProfesional(valores), 'Profesional creado');

  function borrar(p) {
    if (window.confirm(`¿Dar de baja a ${p.nombre} ${p.apellido}?`)) {
      ejecutar(() => eliminarProfesional(p.id), 'Profesional eliminado');
    }
  }

  const columnas = [
    { titulo: 'Apellido', valor: (p) => p.apellido },
    { titulo: 'Nombre', valor: (p) => p.nombre },
    { titulo: 'Matrícula', valor: (p) => p.matricula },
    { titulo: 'Especialidad', valor: (p) => p.especialidad },
    { titulo: 'Email', valor: (p) => p.email },
    {
      titulo: 'Acciones',
      clase: 'acciones',
      valor: (p) => (
        <>
          <button className="secundario" onClick={() => { setEditando(p.id); setValores({ ...p, password: '' }); }}>
            Editar
          </button>
          <button className="peligro" onClick={() => borrar(p)}>Baja</button>
        </>
      ),
    },
  ];

  return (
    <section>
      <h2>Profesionales</h2>
      <MensajeError mensaje={errorCarga || error} />
      <MensajeOk mensaje={ok} />
      <Tabla columnas={columnas} filas={profesionales} vacio="No hay profesionales." />
      <Formulario
        titulo={editando ? 'Editar profesional' : 'Nuevo profesional'}
        campos={campos}
        valores={valores}
        alCambiar={(campo, valor) => setValores({ ...valores, [campo]: valor })}
        alEnviar={guardar}
        textoBoton={editando ? 'Guardar cambios' : 'Dar de alta'}
        alCancelar={editando ? () => { setEditando(null); setValores(VACIO); } : null}
      />
    </section>
  );
}
