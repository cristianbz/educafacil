package ec.mileniumtech.educafacil.utilitarios.enumeraciones;

import lombok.Getter;

/**
 * Máquina de estados del documento electrónico frente al SRI.
 *
 * <p>Flujo normal de transición:</p>
 * <pre>
 * PENDIENTE → EN_PROCESO → ENVIADO → { AUTORIZADO | RECHAZADO | PENDIENTE }
 * </pre>
 *
 * <ul>
 *   <li>{@link #PENDIENTE}: creado, pendiente de envío o enviado sin autorización resuelta
 *       (candidato a reconciliación).</li>
 *   <li>{@link #EN_PROCESO}: XML firmado y clave de acceso generadas, a la espera de ser
 *       enviado al SRI (persistencia temprana).</li>
 *   <li>{@link #ENVIADO}: comprobante RECIBIDO por el SRI; la autorización aún no se resuelve.</li>
 *   <li>{@link #AUTORIZADO}: autorizado por el SRI.</li>
 *   <li>{@link #RECHAZADO}: rechazado por el SRI (recepción o autorización).</li>
 * </ul>
 */
public enum EnumEstadoDocumentoElectronico {
    PENDIENTE("DOC01", "PENDIENTE"),
    EN_PROCESO("DOC02", "EN_PROCESO"),
    ENVIADO("DOC03", "ENVIADO"),
    AUTORIZADO("DOC04", "AUTORIZADO"),
    RECHAZADO("DOC05", "RECHAZADO");

    @Getter
    private final String codigo;
    @Getter
    private final String label;

    private EnumEstadoDocumentoElectronico(String codigo, String label) {
        this.codigo = codigo;
        this.label = label;
    }

    public static EnumEstadoDocumentoElectronico[] listaValores() {
        return values();
    }

    /**
     * Determina si una transición de estado es válida dentro de la máquina de estados.
     *
     * @param desde estado de origen
     * @param hacia estado destino
     * @return {@code true} si la transición es válida
     */
    public static boolean esTransicionValida(EnumEstadoDocumentoElectronico desde,
                                             EnumEstadoDocumentoElectronico hacia) {
        if (desde == null || hacia == null) {
            return false;
        }
        switch (desde) {
            case PENDIENTE:
                // PENDIENTE puede avanzar a EN_PROCESO, o pasar directo a estados terminales
                // (por ejemplo al reconciliar un documento ya resuelto por el SRI).
                return hacia == EN_PROCESO || hacia == AUTORIZADO || hacia == RECHAZADO;
            case EN_PROCESO:
                return hacia == ENVIADO || hacia == AUTORIZADO || hacia == RECHAZADO;
            case ENVIADO:
                // ENVIADO es re-consultado por el scheduler de reconciliación; puede
                // permanecer ENVIADO o resolverse en los estados terminales.
                return hacia == AUTORIZADO || hacia == RECHAZADO || hacia == PENDIENTE;
            case AUTORIZADO:
            case RECHAZADO:
                // Estados terminales: no admiten transiciones.
                return false;
            default:
                return false;
        }
    }
}