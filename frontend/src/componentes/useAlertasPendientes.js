// Cantidad de alertas sin atender, para el contador del menú ("Mis alertas (2)").
// ADMIN: todas las GENERADA; PROFESIONAL: las suyas. Se consulta cada 5 s (polling).
import { useState } from 'react';
import { listarAlertas } from '../api/alertas.js';
import usePolling from './usePolling.js';

export default function useAlertasPendientes(usuario) {
  const [cantidad, setCantidad] = useState(0);
  const vePanelDeAlertas = usuario.rol === 'ADMIN' || usuario.rol === 'PROFESIONAL';

  usePolling(async () => {
    if (!vePanelDeAlertas) return;
    try {
      const filtros = { estado: 'GENERADA', idProfesional: usuario.rol === 'PROFESIONAL' ? usuario.idUsuario : undefined };
      setCantidad((await listarAlertas(filtros)).length);
    } catch {
      // Si falla, el contador queda como estaba: no es crítico.
    }
  }, 5000, [usuario.idUsuario]);

  return cantidad;
}
