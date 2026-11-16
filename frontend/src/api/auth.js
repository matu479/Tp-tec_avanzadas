// Login: POST /api/auth/login (método iniciarSesion() del UML).
import { pedir } from './cliente.js';

export function iniciarSesion(email, password) {
  return pedir('/api/auth/login', { metodo: 'POST', cuerpo: { email, password } });
}
