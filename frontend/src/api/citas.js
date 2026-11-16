// Citas: /api/citas (reservar, cancelar, reprogramar, listar).
import { conParametros, pedir } from './cliente.js';

/** Todas las citas, o la agenda de un profesional si se pasa idProfesional. */
export const listarCitas = (idProfesional) => pedir(conParametros('/api/citas', { idProfesional }));
/** Método del UML reservar(): 201, 409 (turno ocupado) o 422 (fecha pasada) */
export const reservarCita = (datos) => pedir('/api/citas', { metodo: 'POST', cuerpo: datos });
/** Método del UML cancelar() */
export const cancelarCita = (id) => pedir(`/api/citas/${id}/cancelar`, { metodo: 'PATCH' });
/** Método del UML reprogramar(fechaHora) */
export const reprogramarCita = (id, fechaHora) =>
  pedir(`/api/citas/${id}/reprogramar`, { metodo: 'PATCH', cuerpo: { fechaHora } });
