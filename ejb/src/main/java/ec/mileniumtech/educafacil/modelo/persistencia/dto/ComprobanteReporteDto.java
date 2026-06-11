package ec.mileniumtech.educafacil.modelo.persistencia.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteReporteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate fechaEmision;
    private String tipoComprobante;
    private String numero;
    private String identificacion;
    private String razonSocial;
    private String claveAcceso;
    private String numeroAutorizacion;
    private BigDecimal total;
    private String estado;
    private Integer entityId;
    private String entityType;
    private String urlXml;
    private String urlPdf;
    private String correo;
}
