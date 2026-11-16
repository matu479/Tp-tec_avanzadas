// Métricas de un paciente: el front se las pide al CORE (GET /api/pacientes/{id}/metricas),
// que a su vez consulta a metricas-service. Si métricas está caído, el core responde 503.
import { conParametros, pedir } from './cliente.js';

export const metricasDelPaciente = (idPaciente, tipo, desde) =>
  pedir(conParametros(`/api/pacientes/${idPaciente}/metricas`, { tipo, desde }));
