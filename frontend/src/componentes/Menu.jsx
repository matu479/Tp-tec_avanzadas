// Barra superior (usuario + "Cerrar sesión") y menú lateral con las secciones del rol.
// En "Alertas" / "Mis alertas" muestra cuántas hay sin atender (polling cada 5 s).
import { NavLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import { seccionesDelRol } from '../secciones.js';
import useAlertasPendientes from './useAlertasPendientes.js';

export default function Menu() {
  const { usuario, cerrarSesion } = useAuth();
  const pendientes = useAlertasPendientes(usuario);
  return (
    <>
      <header className="barra">
        <span className="marca">Telemedicina</span>
        <span className="usuario">
          {usuario.nombre} {usuario.apellido} <small>({usuario.rol})</small>
          <button className="secundario" onClick={cerrarSesion}>
            Cerrar sesión
          </button>
        </span>
      </header>
      <nav className="menu">
        <NavLink to="/" end>
          Inicio
        </NavLink>
        {seccionesDelRol(usuario.rol).map((s) => (
          <NavLink key={s.ruta} to={s.ruta}>
            {s.titulo}
            {s.ruta === '/alertas' && pendientes > 0 && <span className="contador">({pendientes})</span>}
          </NavLink>
        ))}
      </nav>
    </>
  );
}
