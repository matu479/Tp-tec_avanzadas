// Alertas. ADMIN: todas. PROFESIONAL: "Mis alertas" (GET /api/alertas?idProfesional={id}).
// Filtro por estado, botón "Atender" (PATCH /api/alertas/{id}/atender) y polling cada 5 s:
// las alertas que llegan mientras la pantalla está abierta se resaltan unos segundos.
import { useRef, useState } from 'react';
import { atenderAlerta, listarAlertas } from '../api/alertas.js';
import { useAuth } from '../auth/AuthContext.jsx';
import BadgeEstado from '../componentes/BadgeEstado.jsx';
import MensajeError from '../componentes/MensajeError.jsx';
import Tabla from '../componentes/Tabla.jsx';
import usePolling from '../componentes/usePolling.js';
import { fechaHora } from '../formato.js';

export default function Alertas() {
  const { usuario } = useAuth();
  const esProfesional = usuario.rol === 'PROFESIONAL';
  const [estado, setEstado] = useState('');
  const [alertas, setAlertas] = useState(null);
  const [nuevas, setNuevas] = useState([]);
  const [error, setError] = useState('');
  const conocidas = useRef(null); // ids ya vistos (null = primera carga)

  async function cargar() {
    try {
      const lista = await listarAlertas({ estado, idProfesional: esProfesional ? usuario.idUsuario : undefined });
      marcarNuevas(lista);
      setAlertas(lista);
      setError('');
    } catch (e) {
      setError(e.message);
    }
  }

  /** Las alertas que no estaban en la carga anterior se resaltan durante 5 segundos. */
  function marcarNuevas(lista) {
    const ids = lista.map((a) => a.id);
    if (conocidas.current) {
      const recientes = ids.filter((id) => !conocidas.current.has(id));
      if (recientes.length > 0) {
        setNuevas(recientes);
        setTimeout(() => setNuevas([]), 5000);
      }
    }
    conocidas.current = new Set(ids);
  }

  usePolling(cargar, 5000, [estado]);

  async function atender(alerta) {
    try {
      await atenderAlerta(alerta.id);
      cargar();
    } catch (e) {
      setError(e.message);
    }
  }

  const columnas = [
    { titulo: 'Fecha y hora', valor: (a) => fechaHora(a.fechaHora) },
    { titulo: 'Paciente', valor: (a) => a.nombrePaciente },
    { titulo: 'Nivel', valor: (a) => <BadgeEstado valor={a.nivel} /> },
    { titulo: 'Mensaje', valor: (a) => a.mensaje },
    ...(esProfesional ? [] : [{ titulo: 'Profesional', valor: (a) => a.nombreProfesional || 'Guardia (sin profesional)' }]),
    { titulo: 'Estado', valor: (a) => <BadgeEstado valor={a.estado} /> },
    {
      titulo: 'Acciones',
      clase: 'acciones',
      valor: (a) => a.estado !== 'ATENDIDA' && <button onClick={() => atender(a)}>Atender</button>,
    },
  ];

  return (
    <section>
      <h2>{esProfesional ? 'Mis alertas' : 'Alertas'}</h2>
      <div className="filtros">
        <label>
          Estado
          <select value={estado} onChange={(e) => { conocidas.current = null; setEstado(e.target.value); }}>
            <option value="">Todas</option>
            <option value="GENERADA">Sin atender (GENERADA)</option>
            <option value="ATENDIDA">Atendidas</option>
          </select>
        </label>
      </div>
      <MensajeError mensaje={error} />
      {alertas === null ? <p>Cargando...</p> : (
        <Tabla columnas={columnas} filas={alertas} vacio="No hay alertas."
          claseFila={(a) => (nuevas.includes(a.id) ? 'nueva' : '')} />
      )}
      <p className="ayuda">Se actualiza sola cada 5 segundos.</p>
    </section>
  );
}
