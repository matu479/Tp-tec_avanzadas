// Formulario genérico en un panel.
// campos: [{ nombre, etiqueta, tipo ('text', 'email', 'date', 'datetime-local', 'textarea', 'select'), opciones, requerido,
//            autocompletar }]  (autocompletar = valor del atributo autocomplete, ej. 'off' o 'new-password')
export default function Formulario({ titulo, campos, valores, alCambiar, alEnviar, textoBoton, alCancelar }) {
  function enviar(evento) {
    evento.preventDefault();
    alEnviar();
  }
  return (
    <form className="panel" onSubmit={enviar}>
      {titulo && <h3>{titulo}</h3>}
      {campos.map((c) => (
        <label key={c.nombre}>
          {c.etiqueta}
          <Campo campo={c} valor={valores[c.nombre] ?? ''} alCambiar={(v) => alCambiar(c.nombre, v)} />
        </label>
      ))}
      <div className="botones">
        <button type="submit">{textoBoton}</button>
        {alCancelar && (
          <button type="button" className="secundario" onClick={alCancelar}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

function Campo({ campo, valor, alCambiar }) {
  const comunes = {
    name: campo.nombre,
    value: valor,
    required: campo.requerido,
    autoComplete: campo.autocompletar,
    onChange: (e) => alCambiar(e.target.value),
  };
  if (campo.tipo === 'textarea') return <textarea rows={3} {...comunes} />;
  if (campo.tipo === 'select') {
    return (
      <select {...comunes}>
        <option value="">Elegí una opción</option>
        {campo.opciones.map((o) => (
          <option key={o.valor} value={o.valor}>
            {o.texto}
          </option>
        ))}
      </select>
    );
  }
  return <input type={campo.tipo || 'text'} {...comunes} />;
}
