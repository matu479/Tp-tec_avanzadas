// Cliente GraphQL mínimo: POST /graphql con { query, variables }.
// Si la respuesta trae "errors", se lanza un ErrorApi con el primer mensaje.
import { ErrorApi, pedir } from './cliente.js';

export async function consultarGraphQL(query, variables = {}) {
  const respuesta = await pedir('/graphql', { metodo: 'POST', cuerpo: { query, variables } });
  if (respuesta?.errors?.length) {
    throw new ErrorApi(200, respuesta.errors[0].message);
  }
  return respuesta.data;
}
