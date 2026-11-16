// Pacientes: /api/pacientes (ABM, citas y expediente del paciente).
import { pedir } from './cliente.js';

export const listarPacientes = () => pedir('/api/pacientes');
export const crearPaciente = (datos) => pedir('/api/pacientes', { metodo: 'POST', cuerpo: datos });
export const actualizarPaciente = (id, datos) => pedir(`/api/pacientes/${id}`, { metodo: 'PUT', cuerpo: datos });
export const eliminarPaciente = (id) => pedir(`/api/pacientes/${id}`, { metodo: 'DELETE' });
/** Método del UML consultarCitas() */
export const citasDelPaciente = (id) => pedir(`/api/pacientes/${id}/citas`);
