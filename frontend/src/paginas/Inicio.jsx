// Inicio: saludo y accesos directos a las secciones del rol.
import { Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import { seccionesDelRol } from '../secciones.js';

export default function Inicio() {
  const { usuario } = useAuth();
  return (
    <section>
      <h2>Hola, {usuario.nombre}</h2>
      <p>Ingresaste como {usuario.rol}. Elegí una sección:</p>
      <div className="accesos">
        {seccionesDelRol(usuario.rol).map((s) => (
          <Link key={s.ruta} to={s.ruta} className="acceso">
            {s.titulo}
          </Link>
        ))}
      </div>
    </section>
  );
}
