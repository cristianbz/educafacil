package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cuotas")
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = Cuota.LISTAR_POR_MATRICULA, query = "SELECT C FROM Cuota C WHERE C.matricula.matrId = :codigoMatricula ORDER BY C.cuoNumero"),
    @NamedQuery(name = Cuota.LISTAR_PENDIENTES_POR_MATRICULA, query = "SELECT C FROM Cuota C WHERE C.matricula.matrId = :codigoMatricula AND C.cuoEstado = 'CUOTA01' ORDER BY C.cuoNumero")
})
public class Cuota implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String LISTAR_POR_MATRICULA = "listarCuotasPorMatricula";
    public static final String LISTAR_PENDIENTES_POR_MATRICULA = "listarCuotasPendientesPorMatricula";

    @Id
    @SequenceGenerator(name = "cuotasSeq", sequenceName = "cuotas_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cuotasSeq")
    @Column(name = "cuo_id")
    private Integer cuoId;

    @Column(name = "cuo_numero")
    private Integer cuoNumero;

    @Column(name = "cuo_valor")
    private Double cuoValor;

    @Column(name = "cuo_fecha_limite")
    private Date cuoFechaLimite;

    @Column(name = "cuo_estado")
    private String cuoEstado;

    @Column(name = "cuo_fecha_pago")
    private Date cuoFechaPago;

    @ManyToOne
    @JoinColumn(name = "matr_id")
    private Matricula matricula;

    @ManyToOne
    @JoinColumn(name = "depa_id")
    private DetallePagos detallePagos;
}
