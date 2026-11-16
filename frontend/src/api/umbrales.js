// Rangos de cada tipo de métrica: los MISMOS valores que TipoMetrica de metricas-service.
// Se usan para dibujar la banda del rango normal en el gráfico y marcar en rojo los puntos fuera de rango.
export const UMBRALES = {
  PULSACIONES: { etiqueta: 'Pulsaciones', unidad: 'lpm', normalMin: 50, normalMax: 110, criticoMin: 40, criticoMax: 150 },
  PRESION: { etiqueta: 'Presión (sistólica)', unidad: 'mmHg', normalMin: 90, normalMax: 140, criticoMin: 80, criticoMax: 180 },
  SATURACION: { etiqueta: 'Saturación', unidad: '%', normalMin: 94, normalMax: 100, criticoMin: 90, criticoMax: 100 },
  TEMPERATURA: { etiqueta: 'Temperatura', unidad: '°C', normalMin: 35.5, normalMax: 37.8, criticoMin: 34, criticoMax: 39.5 },
};

/** ¿El valor está fuera del rango normal? (igual que superaUmbral() del backend) */
export function fueraDeRango(tipo, valor) {
  const u = UMBRALES[tipo];
  return valor < u.normalMin || valor > u.normalMax;
}
