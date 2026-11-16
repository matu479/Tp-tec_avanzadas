// Ejecuta "funcion" ahora y después cada "milisegundos" (polling).
// El backend no tiene WebSocket: preguntar cada 5 s es lo más simple para ver datos "en vivo".
// El intervalo se limpia al salir de la pantalla (return del useEffect).
import { useEffect, useRef } from 'react';

export default function usePolling(funcion, milisegundos, dependencias) {
  const ultimaFuncion = useRef(funcion);
  ultimaFuncion.current = funcion;

  useEffect(() => {
    ultimaFuncion.current();
    const intervalo = setInterval(() => ultimaFuncion.current(), milisegundos);
    return () => clearInterval(intervalo);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, dependencias);
}
