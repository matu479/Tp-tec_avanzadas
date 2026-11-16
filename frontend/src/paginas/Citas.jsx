// Citas: muestra una pantalla distinta según el rol.
// PACIENTE → "Mis citas" · PROFESIONAL → "Mi agenda" · ADMIN → todas (solo lectura).
import { useAuth } from '../auth/AuthContext.jsx';
import AgendaProfesional from './citas/AgendaProfesional.jsx';
import CitasAdmin from './citas/CitasAdmin.jsx';
import MisCitas from './citas/MisCitas.jsx';

export default function Citas() {
  const { usuario } = useAuth();
  if (usuario.rol === 'PACIENTE') return <MisCitas idPaciente={usuario.idUsuario} />;
  if (usuario.rol === 'PROFESIONAL') return <AgendaProfesional idProfesional={usuario.idUsuario} />;
  return <CitasAdmin />;
}
