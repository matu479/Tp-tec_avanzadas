// Expediente clínico con GraphQL.
// Ejemplo de la consigna: "los profesionales solicitan SOLO los datos del historial que necesitan".
// Con REST (GET /api/pacientes/{id}/expediente) llegaría todo (email y matrícula del profesional, ids, etc.).
// Con GraphQL, la pantalla pide exactamente los campos que muestra, en un solo pedido.
import { consultarGraphQL } from './graphql.js';

const QUERY_EXPEDIENTE = `
  query Expediente($id: ID!) {
    paciente(id: $id) {
      nombre
      apellido
      expediente {
        antecedentes
        observacionesGenerales
        consultas {
          id
          fechaConsulta
          indicaciones
          observaciones
          profesional { nombre apellido especialidad }
          diagnostico { descripcion }
        }
      }
    }
  }`;

export async function obtenerExpediente(idPaciente) {
  const datos = await consultarGraphQL(QUERY_EXPEDIENTE, { id: idPaciente });
  return datos.paciente;
}
