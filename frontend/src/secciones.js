// Secciones del menú (las del Diagrama de Sistema) y qué roles las ven.
// OJO: ocultar secciones según el rol es SOLO de interfaz, no es seguridad:
// el backend no protege los endpoints (decisión S11).
export const SECCIONES = [
  {
    ruta: '/metricas',
    titulos: { PROFESIONAL: 'Visualización de Métricas', PACIENTE: 'Mis métricas' },
  },
  { ruta: '/pacientes', titulos: { ADMIN: 'Pacientes', PROFESIONAL: 'Pacientes' } },
  { ruta: '/profesionales', titulos: { ADMIN: 'Profesionales' } },
  { ruta: '/citas', titulos: { ADMIN: 'Citas', PROFESIONAL: 'Mi agenda', PACIENTE: 'Mis citas' } },
  {
    ruta: '/expedientes',
    titulos: { PROFESIONAL: 'Expedientes clínicos', PACIENTE: 'Mi expediente' },
  },
  { ruta: '/alertas', titulos: { ADMIN: 'Alertas', PROFESIONAL: 'Mis alertas' } },
];

/** Secciones que ve un rol, con el título que corresponde a ese rol. */
export function seccionesDelRol(rol) {
  return SECCIONES.filter((s) => s.titulos[rol]).map((s) => ({ ruta: s.ruta, titulo: s.titulos[rol] }));
}

/** ¿El rol puede entrar a esta ruta? */
export function puedeVer(rol, ruta) {
  return seccionesDelRol(rol).some((s) => s.ruta === ruta);
}
