package ec.mileniumtech.educafacil.modelo.persistencia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "planificacioncurso")
@Getter
@Setter
public class PlanificacionCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plcu_seq")
    @SequenceGenerator(name = "plcu_seq", sequenceName = "cap.planificacioncurso_plcu_id_seq", allocationSize = 1)
    @Column(name = "plcu_id", nullable = false)
    private Integer plcuId;

    @Column(name = "plcu_webinar_charla", length = 200)
    private String plcuWebinarCharla;

    @Column(name = "plcu_fecha_inicio", nullable = false)
    private LocalDate plcuFechaInicio;

    @Column(name = "plcu_hora_inicio", nullable = false)
    private Integer plcuHoraInicio;

    // NUMERIC(4,2) se mapea idealmente con BigDecimal para evitar pérdida de precisión
    @Column(name = "plcu_tiempo_duracion", nullable = false, precision = 4, scale = 2)
    private BigDecimal plcuTiempoDuracion;

    @Column(name = "plcu_ubicacion", length = 200)
    private String plcuUbicacion;

    // --- CONSTRUCTORES ---
    public PlanificacionCurso() {
    }
    
	@ManyToOne
	@JoinColumn(name="curs_id")
	private Curso curso;
	
	@ManyToOne
	@JoinColumn(name="inst_id")
	private Instructor instructor;

}
