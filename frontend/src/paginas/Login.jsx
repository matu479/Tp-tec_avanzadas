// Pantalla de login: email + contraseña → POST /api/auth/login.
import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import MensajeError from '../componentes/MensajeError.jsx';

// Usuarios de prueba (se muestran SOLO en desarrollo: npm run dev)
const USUARIOS_PRUEBA = [
  ['Admin', 'admin@tp.com', 'Admin123!'],
  ['Profesional (cardiología)', 'cardio@tp.com', 'Medico123!'],
  ['Profesional (clínica)', 'clinica@tp.com', 'Medico123!'],
  ['Paciente (Ana Gómez)', 'ana.gomez@tp.com', 'Paciente123!'],
];

export default function Login() {
  const { usuario, iniciarSesion } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  if (usuario) return <Navigate to="/" replace />;

  async function enviar(evento) {
    evento.preventDefault();
    setError('');
    try {
      await iniciarSesion(email, password);
    } catch (e) {
      setError(e.status === 401 ? 'Email o contraseña incorrectos' : e.message);
    }
  }

  return (
    <div className="login">
      <form className="tarjeta" onSubmit={enviar}>
        <h1>Telemedicina</h1>
        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          Contraseña
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </label>
        <MensajeError mensaje={error} />
        <button type="submit">Ingresar</button>
        {import.meta.env.DEV && (
          <ul className="ayuda">
            {USUARIOS_PRUEBA.map(([rol, mail, clave]) => (
              <li key={mail}>
                {rol}: {mail} / {clave}
              </li>
            ))}
          </ul>
        )}
      </form>
    </div>
  );
}
