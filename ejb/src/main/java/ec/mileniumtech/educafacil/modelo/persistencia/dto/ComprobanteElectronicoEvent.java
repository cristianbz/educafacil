package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento CDI disparado cuando finaliza el procesamiento asíncrono
 * o reconciliación de un comprobante electrónico (Factura, Nota de Crédito, Retención).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteElectronicoEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tipoDocumento; // "FACTURA", "NOTA_CREDITO", "RETENCION"
    private Integer id;
    private String numero;
    private String estado; // "AUTORIZADO", "RECHAZADO", "ENVIADO", "ERROR"
    private String mensaje;
    private boolean exitoso;
    @Builder.Default
    private String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public ComprobanteElectronicoEvent(String tipoDocumento, Integer id, String numero, String estado, String mensaje, boolean exitoso) {
        this.tipoDocumento = tipoDocumento;
        this.id = id;
        this.numero = numero;
        this.estado = estado;
        this.mensaje = mensaje;
        this.exitoso = exitoso;
        this.fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
