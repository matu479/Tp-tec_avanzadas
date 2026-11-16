// Citas para el ADMIN: todas las citas (GET /api/citas), solo lectura.
import BadgeEstado from '../../componentes/BadgeEstado.jsx';
import MensajeError from '../../componentes/MensajeError.jsx';
import Tabla from '../../componentes/Tabla.jsx';
import useCarga from '../../componentes/useCarga.js';
import { listarCitas } from '../../api/citas.js';
import { fechaHora } from '../../formato.js';

export default function CitasAdmin() {
  const { datos: citas, error } = useCarga(() => listarCitas());
  const columnas = [
    { titulo: 'Fecha y hora', valor: (c) => fechaHora(c.fechaHora) },
    { titulo: 'Paciente', valor: (c) => c.nombrePaciente },
    { titulo: 'Profesional', valor: (c) => `${c.profesional.apellido} (${c.profesional.especialidad})` },
    { titulo: 'Motivo', valor: (c) => c.motivo },
    { titulo: 'Estado', valor: (c) => <BadgeEstado valor={c.estado} /> },
  ];
  return (
    <section>
      <h2>Citas</h2>
      <MensajeError mensaje={error} />
      <Tabla columnas={columnas} filas={citas} vacio="No hay citas." />
    </section>
  );
}
