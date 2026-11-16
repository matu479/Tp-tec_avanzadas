// Profesionales: /api/profesionales (ABM).
import { pedir } from './cliente.js';

export const listarProfesionales = () => pedir('/api/profesionales');
export const crearProfesional = (datos) => pedir('/api/profesionales', { metodo: 'POST', cuerpo: datos });
export const actualizarProfesional = (id, datos) => pedir(`/api/profesionales/${id}`, { metodo: 'PUT', cuerpo: datos });
export const eliminarProfesional = (id) => pedir(`/api/profesionales/${id}`, { metodo: 'DELETE' });
