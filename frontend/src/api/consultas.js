// Consultas médicas: /api/consultas.
import { pedir } from './cliente.js';

/** registrarConsulta(): la cita RESERVADA pasa a ATENDIDA. */
export const registrarConsulta = (datos) => pedir('/api/consultas', { metodo: 'POST', cuerpo: datos });
/** registrarDiagnostico(String) */
export const registrarDiagnostico = (id, texto) =>
  pedir(`/api/consultas/${id}/diagnostico`, { metodo: 'PUT', cuerpo: { texto } });
/** agregarIndicacion(String) */
export const agregarIndicacion = (id, texto) =>
  pedir(`/api/consultas/${id}/indicaciones`, { metodo: 'PATCH', cuerpo: { texto } });
/** agregarObservacion(String) */
export const agregarObservacion = (id, texto) =>
  pedir(`/api/consultas/${id}/observaciones`, { metodo: 'PATCH', cuerpo: { texto } });
