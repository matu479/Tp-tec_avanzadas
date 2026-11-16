// Cliente HTTP base: todas las llamadas al backend pasan por acá.
// Usa fetch nativo, manda y recibe JSON, y convierte los errores del backend
// ({status, mensaje}) en una excepción ErrorApi con un mensaje legible.

export class ErrorApi extends Error {
  constructor(status, mensaje) {
    super(mensaje);
    this.status = status;
  }
}

/** Hace un pedido al backend. Ej.: pedir('/api/citas', { metodo: 'POST', cuerpo: {...} }) */
export async function pedir(ruta, { metodo = 'GET', cuerpo } = {}) {
  let respuesta;
  try {
    respuesta = await fetch(ruta, {
      method: metodo,
      headers: cuerpo !== undefined ? { 'Content-Type': 'application/json' } : {},
      body: cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined,
    });
  } catch {
    throw new ErrorApi(0, 'No se pudo conectar con el servidor');
  }
  const datos = await leerJson(respuesta);
  if (!respuesta.ok) {
    throw new ErrorApi(respuesta.status, datos?.mensaje || `El servidor respondió ${respuesta.status}`);
  }
  return datos;
}

/** Lee el cuerpo como JSON; si está vacío o no es JSON (ej. 502 de nginx), devuelve null. */
async function leerJson(respuesta) {
  const texto = await respuesta.text();
  if (!texto) return null;
  try {
    return JSON.parse(texto);
  } catch {
    return null;
  }
}

/** Arma un query string ignorando los valores vacíos. Ej.: conParametros('/api/citas', {idProfesional: 3}) */
export function conParametros(ruta, parametros) {
  const lista = Object.entries(parametros).filter(([, valor]) => valor !== undefined && valor !== null && valor !== '');
  if (lista.length === 0) return ruta;
  return `${ruta}?${new URLSearchParams(lista).toString()}`;
}
