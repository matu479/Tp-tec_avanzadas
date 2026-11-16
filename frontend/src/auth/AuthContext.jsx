// Usuario logueado (id, nombre, apellido, rol), disponible en toda la app con useAuth().
// Se guarda en localStorage para no perder la sesión al recargar la página.
// El id del usuario es el mismo que el id de paciente o profesional (herencia JOINED, S1).
import { createContext, useContext, useState } from 'react';
import { iniciarSesion as loginApi } from '../api/auth.js';

const AuthContext = createContext(null);
const CLAVE = 'telemedicina.usuario';

function leerUsuarioGuardado() {
  try {
    return JSON.parse(localStorage.getItem(CLAVE));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(leerUsuarioGuardado);

  async function iniciarSesion(email, password) {
    const datos = await loginApi(email, password);
    setUsuario(datos);
    localStorage.setItem(CLAVE, JSON.stringify(datos));
  }

  function cerrarSesion() {
    setUsuario(null);
    localStorage.removeItem(CLAVE);
  }

  return (
    <AuthContext.Provider value={{ usuario, iniciarSesion, cerrarSesion }}>{children}</AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
