// Hook chico para cargar datos del backend: devuelve { datos, error, recargar }.
import { useCallback, useEffect, useState } from 'react';

export default function useCarga(funcion, dependencias = []) {
  const [datos, setDatos] = useState(null);
  const [error, setError] = useState('');

  const recargar = useCallback(async () => {
    try {
      setDatos(await funcion());
      setError('');
    } catch (e) {
      setError(e.message);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, dependencias);

  useEffect(() => {
    recargar();
  }, [recargar]);

  return { datos, error, recargar };
}
