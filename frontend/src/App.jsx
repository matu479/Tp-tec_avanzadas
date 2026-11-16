// Rutas de la app y estructura de la página (menú + contenido).
import { Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext.jsx';
import Menu from './componentes/Menu.jsx';
import RutaPorRol from './componentes/RutaPorRol.jsx';
import Alertas from './paginas/Alertas.jsx';
import Citas from './paginas/Citas.jsx';
import Expediente from './paginas/Expediente.jsx';
import Inicio from './paginas/Inicio.jsx';
import Login from './paginas/Login.jsx';
import Metricas from './paginas/Metricas.jsx';
import Pacientes from './paginas/Pacientes.jsx';
import Profesionales from './paginas/Profesionales.jsx';

const PAGINAS = {
  '/': Inicio,
  '/pacientes': Pacientes,
  '/profesionales': Profesionales,
  '/citas': Citas,
  '/expedientes': Expediente,
  '/metricas': Metricas,
  '/alertas': Alertas,
};

export default function App() {
  const { usuario } = useAuth();
  return (
    <div className={usuario ? 'app' : ''}>
      {usuario && <Menu />}
      <main className={usuario ? 'contenido' : ''}>
        <Routes>
          <Route path="/login" element={<Login />} />
          {Object.entries(PAGINAS).map(([ruta, Pagina]) => (
            <Route key={ruta} path={ruta} element={<RutaPorRol><Pagina /></RutaPorRol>} />
          ))}
          <Route path="*" element={<RutaPorRol><Inicio /></RutaPorRol>} />
        </Routes>
      </main>
    </div>
  );
}
