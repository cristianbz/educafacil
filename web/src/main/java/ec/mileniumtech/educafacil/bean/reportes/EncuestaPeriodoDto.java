package ec.mileniumtech.educafacil.bean.reportes;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para almacenar los datos agregados por cada encuesta en el período.
 *
 * @author Christian Baez — Jul 2026
 */
@Getter
@Setter
public class EncuestaPeriodoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int evcuId;
    private String codigo;
    private String encuesta;
    private String curso;
    private String periodo;
    private int asignados;
    private int respondieron;
    private double tasa;
    private double promedio;
    private String estado;
}
