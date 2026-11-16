// Alertas médicas: /api/alertas (listado con filtros y "atender").
import { conParametros, pedir } from './cliente.js';

/** Filtros opcionales: estado (GENERADA / ATENDIDA) e idProfesional ("Mis alertas"). */
export const listarAlertas = ({ estado, idProfesional } = {}) =>
  pedir(conParametros('/api/alertas', { estado, idProfesional }));
/** Método del UML marcarAtendida(): 200, o 409 si ya estaba atendida. */
export const atenderAlerta = (id) => pedir(`/api/alertas/${id}/atender`, { metodo: 'PATCH' });
