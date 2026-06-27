/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.bean.administracion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PlanificacionCurso;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.ScheduleModel;

/**
 * Bean de datos para el módulo de Calendario de Cursos.
 *
 * @author christian
 */
@Named("beanCalendarioCursos")
@ViewScoped
public class BeanCalendarioCursos implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Planificaciones de la semana actualmente visualizada */
    @Getter @Setter
    private List<PlanificacionCurso> listaPlanificaciones;

    /** Planificación seleccionada para edición o visualización */
    @Getter @Setter
    private PlanificacionCurso planificacionSeleccionada;

    /** Lista de cursos disponibles para el selector del diálogo */
    @Getter @Setter
    private List<Curso> listaCursos;

    /** Lista de instructores disponibles para el selector del diálogo */
    @Getter @Setter
    private List<Instructor> listaInstructores;

    /** Fecha de inicio de la semana actualmente visible */
    @Getter @Setter
    private LocalDate inicioSemana;

    /** Fecha de fin de la semana actualmente visible */
    @Getter @Setter
    private LocalDate finSemana;

    /** ID del curso seleccionado en el diálogo */
    @Getter @Setter
    private Integer cursId;

    /** ID del instructor seleccionado en el diálogo */
    @Getter @Setter
    private Integer instId;

    /** Controla la visibilidad del panel de confirmación de eliminación */
    @Getter @Setter
    private boolean mostrarConfirmacion;

    /** Modelo del componente p:schedule */
    @Getter @Setter
    private ScheduleModel eventModel;
}
