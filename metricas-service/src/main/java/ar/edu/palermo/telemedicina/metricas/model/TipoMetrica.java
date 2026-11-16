package ar.edu.palermo.telemedicina.metricas.model;

/**
 * Tipos de métrica con sus rangos como constantes, para que superaUmbral() no necesite parámetros.
 * - Rango físico: lo que es posible medir (fuera de eso, la métrica se descarta).
 * - Rango normal: fuera de eso, la métrica supera el umbral (alerta ALTA).
 * - Rango crítico: fuera de eso, la alerta es CRITICA.
 * PRESION usa la sistólica (S10).
 */
public enum TipoMetrica {

    //            unidad   físico min/max   normal min/max    crítico min/max
    PULSACIONES("lpm",   20, 250,         50, 110,          40, 150),
    PRESION("mmHg",      50, 260,         90, 140,          80, 180),
    SATURACION("%",      50, 100,         94, 100,          90, 100),
    TEMPERATURA("°C",    30, 45,          35.5, 37.8,       34, 39.5);

    private final String unidad;
    private final double fisicoMin;
    private final double fisicoMax;
    private final double normalMin;
    private final double normalMax;
    private final double criticoMin;
    private final double criticoMax;

    TipoMetrica(String unidad, double fisicoMin, double fisicoMax, double normalMin, double normalMax,
                double criticoMin, double criticoMax) {
        this.unidad = unidad;
        this.fisicoMin = fisicoMin;
        this.fisicoMax = fisicoMax;
        this.normalMin = normalMin;
        this.normalMax = normalMax;
        this.criticoMin = criticoMin;
        this.criticoMax = criticoMax;
    }

    /** ¿El valor es físicamente posible para este tipo? */
    public boolean esPosible(double valor) {
        return valor >= fisicoMin && valor <= fisicoMax;
    }

    /** ¿El valor está fuera del rango normal? (los límites cuentan como normales) */
    public boolean estaFueraDeLoNormal(double valor) {
        return valor < normalMin || valor > normalMax;
    }

    /** ¿El valor está fuera del rango crítico? */
    public boolean esCritico(double valor) {
        return valor < criticoMin || valor > criticoMax;
    }

    public String getUnidad() {
        return unidad;
    }
}
