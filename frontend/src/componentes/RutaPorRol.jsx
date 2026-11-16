// Protege una ruta en la INTERFAZ: sin sesión → /login; rol sin permiso → inicio.
// No es seguridad real: el backend no protege los endpoints (S11).
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import { puedeVer } from '../secciones.js';

export default function RutaPorRol({ children }) {
  const { usuario } = useAuth();
  const { pathname } = useLocation();
  if (!usuario) return <Navigate to="/login" replace />;
  if (pathname !== '/' && !puedeVer(usuario.rol, pathname)) return <Navigate to="/" replace />;
  return children;
}
