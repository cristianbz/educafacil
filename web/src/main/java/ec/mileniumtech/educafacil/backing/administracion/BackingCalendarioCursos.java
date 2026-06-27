/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.administracion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.time.temporal.TemporalAdjusters;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanCalendarioCursos;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Curso;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.Instructor;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.PlanificacionCurso;
import ec.mileniumtech.educafacil.service.AdministracionService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * Backing bean para el módulo de Calendario de Cursos (vista semanal).
 * Gestiona la navegación entre semanas y las operaciones CRUD sobre PlanificacionCurso.
 *
 * @author christian
 */
@Named("backingCalendarioCursos")
@ViewScoped
public class BackingCalendarioCursos implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(BackingCalendarioCursos.class);

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;

    @Inject
    @Getter
    private BeanCalendarioCursos beanCalendarioCursos;

    @EJB
    private AdministracionService administracionService;

    // =========================================================
    // Inicialización
    // =========================================================

    @PostConstruct
    public void init() {
        // Posicionarse en la semana actual (lunes → domingo)
        LocalDate hoy = LocalDate.now();
        getBeanCalendarioCursos().setInicioSemana(hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        getBeanCalendarioCursos().setFinSemana(hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));

        // Cargar datos de soporte
        getBeanCalendarioCursos().setListaCursos(administracionService.listarTodosCursosOrdenados());
        getBeanCalendarioCursos().setListaInstructores(administracionService.listarInstructoresOrdenados());

        // Cargar planificaciones de la semana actual
        cargarPlanificacionesSemana();

        // Inicializar planificación vacía
        getBeanCalendarioCursos().setPlanificacionSeleccionada(new PlanificacionCurso());
    }

    // =========================================================
    // Navegación de semanas
    // =========================================================

    /**
     * Avanza al siguiente período de 7 días.
     */
    public void semanaAnterior() {
        LocalDate nuevo = getBeanCalendarioCursos().getInicioSemana().minusDays(7);
        getBeanCalendarioCursos().setInicioSemana(nuevo);
        getBeanCalendarioCursos().setFinSemana(nuevo.plusDays(6));
        cargarPlanificacionesSemana();
    }

    /**
     * Retrocede al período de 7 días anterior.
     */
    public void semanaSiguiente() {
        LocalDate nuevo = getBeanCalendarioCursos().getInicioSemana().plusDays(7);
        getBeanCalendarioCursos().setInicioSemana(nuevo);
        getBeanCalendarioCursos().setFinSemana(nuevo.plusDays(6));
        cargarPlanificacionesSemana();
    }

    /**
     * Posiciona la vista en la semana que contiene la fecha actual.
     */
    public void irHoy() {
        LocalDate hoy = LocalDate.now();
        getBeanCalendarioCursos().setInicioSemana(hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        getBeanCalendarioCursos().setFinSemana(hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
        cargarPlanificacionesSemana();
    }

    // =========================================================
    // Carga de datos
    // =========================================================

    /**
     * Recarga la lista de planificaciones según el rango de la semana activa.
     */
    private void cargarPlanificacionesSemana() {
        DefaultScheduleModel model = new DefaultScheduleModel();
        try {
            List<PlanificacionCurso> planificaciones = administracionService.listarPlanificacionesPorSemana(
                    getBeanCalendarioCursos().getInicioSemana(),
                    getBeanCalendarioCursos().getFinSemana());
            getBeanCalendarioCursos().setListaPlanificaciones(planificaciones);

            for (PlanificacionCurso pc : planificaciones) {
                try {
                    if (pc.getPlcuFechaInicio() == null || pc.getPlcuHoraInicio() == null || pc.getPlcuTiempoDuracion() == null) {
                        log.warn("PlanificacionCurso id={} omitida: fecha, hora o duracion nula", pc.getPlcuId());
                        continue;
                    }
                    LocalDateTime start = pc.getPlcuFechaInicio().atTime(pc.getPlcuHoraInicio(), 0);
                    long minutosDuracion = (long) (pc.getPlcuTiempoDuracion().doubleValue() * 60);
                    LocalDateTime end = start.plusMinutes(minutosDuracion);

                    String title = pc.getCurso() != null && pc.getCurso().getCursNombre() != null
                            ? pc.getCurso().getCursNombre() : "Sin curso";
                    if (pc.getInstructor() != null && pc.getInstructor().getPersona() != null
                            && pc.getInstructor().getPersona().getPersApellidos() != null) {
                        title += " - " + pc.getInstructor().getPersona().getPersApellidos();
                    }

                    String descripcion = armarTooltip(pc, start, end, title);

                    DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder()
                            .title(title)
                            .startDate(start)
                            .endDate(end)
                            .description(descripcion)
                            .data(pc)
                            .styleClass(getClaseColorEvento(pc))
                            .build();

                    model.addEvent(event);
                } catch (Exception e) {
                    log.error("Error al procesar planificacion id={}", pc.getPlcuId(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error al cargar planificaciones de semana", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    "No se pudo cargar el calendario de cursos.");
        } finally {
            getBeanCalendarioCursos().setEventModel(model);
        }
    }

    // =========================================================
    // CRUD — Nuevo / Editar / Guardar / Eliminar
    // =========================================================

    /**
     * Prepara una nueva planificación vacía y abre el diálogo de creación.
     */
    public void nuevaPlanificacion() {
        PlanificacionCurso nueva = new PlanificacionCurso();
        nueva.setPlcuFechaInicio(LocalDate.now());
        nueva.setPlcuHoraInicio(8);
        nueva.setPlcuTiempoDuracion(new BigDecimal("1.00"));
        getBeanCalendarioCursos().setPlanificacionSeleccionada(nueva);
        getBeanCalendarioCursos().setCursId(null);
        getBeanCalendarioCursos().setInstId(null);
        Mensaje.verDialogo("wDlgPlanificacion");
    }

    /**
     * Carga la planificación seleccionada en el diálogo para su edición.
     *
     * @param planificacion planificación a editar
     */
    public void editarPlanificacion(PlanificacionCurso planificacion) {
        getBeanCalendarioCursos().setPlanificacionSeleccionada(planificacion);
        getBeanCalendarioCursos().setCursId(planificacion.getCurso() != null ? planificacion.getCurso().getCursId() : null);
        getBeanCalendarioCursos().setInstId(planificacion.getInstructor() != null ? planificacion.getInstructor().getInstId() : null);
        Mensaje.verDialogo("wDlgPlanificacion");
    }

    /**
     * Persiste o actualiza la planificación del diálogo y recarga la vista.
     */
    public void guardarPlanificacion() {
        try {
            PlanificacionCurso pc = getBeanCalendarioCursos().getPlanificacionSeleccionada();

            // Asignar curso seleccionado
            Integer cursId = getBeanCalendarioCursos().getCursId();
            if (cursId != null && cursId > 0) {
                Curso curso = getBeanCalendarioCursos().getListaCursos().stream()
                        .filter(c -> cursId.equals(c.getCursId()))
                        .findFirst().orElse(null);
                pc.setCurso(curso);
            }

            // Asignar instructor seleccionado
            Integer instId = getBeanCalendarioCursos().getInstId();
            if (instId != null && instId > 0) {
                Instructor instructor = getBeanCalendarioCursos().getListaInstructores().stream()
                        .filter(i -> instId.equals(i.getInstId()))
                        .findFirst().orElse(null);
                pc.setInstructor(instructor);
            }

            administracionService.guardarPlanificacionCurso(pc);
            cargarPlanificacionesSemana();
            Mensaje.ocultarDialogo("wDlgPlanificacion");
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO,
                    getMensajesBacking().getPropiedad("info"),
                    getMensajesBacking().getPropiedad("info.agregar"));

        } catch (Exception e) {
            log.error("Error al guardar planificación de curso", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    "No se pudo guardar la planificación del curso.");
        }
    }

    /**
     * Carga la planificación que se desea eliminar y abre el diálogo de confirmación.
     *
     * @param planificacion planificación a eliminar
     */
    public void confirmarEliminar(PlanificacionCurso planificacion) {
        getBeanCalendarioCursos().setPlanificacionSeleccionada(planificacion);
        Mensaje.verDialogo("wDlgConfirmarEliminar");
    }

    /**
     * Elimina definitivamente la planificación previamente seleccionada.
     */
    public void eliminarPlanificacion() {
        try {
            PlanificacionCurso pc = getBeanCalendarioCursos().getPlanificacionSeleccionada();
            if (pc != null && pc.getPlcuId() != null) {
                administracionService.eliminarPlanificacionCurso(pc.getPlcuId());
                cargarPlanificacionesSemana();
                Mensaje.ocultarDialogo("wDlgConfirmarEliminar");
                Mensaje.verMensaje(FacesMessage.SEVERITY_INFO,
                        getMensajesBacking().getPropiedad("info"),
                        getMensajesBacking().getPropiedad("info.eliminar"));
            }
        } catch (Exception e) {
            log.error("Error al eliminar planificación de curso", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR,
                    getMensajesBacking().getPropiedad("error"),
                    "No se pudo eliminar la planificación del curso.");
        }
    }

    // =========================================================
    // Helpers de vista
    // =========================================================

    /**
     * Devuelve el día de la semana en formato numérico ISO (1=Lunes … 7=Domingo)
     * para una planificación dada, necesario para posicionar el evento en la columna
     * correcta del calendario semanal.
     *
     * @param pc planificación a evaluar
     * @return valor ISO del día de la semana
     */
    public int getDiaSemana(PlanificacionCurso pc) {
        if (pc == null || pc.getPlcuFechaInicio() == null) return 1;
        return pc.getPlcuFechaInicio().getDayOfWeek().getValue();
    }

    /**
     * Calcula el porcentaje de top (posición vertical) para ubicar un evento
     * dentro de la franja horaria 07:00 – 21:00 (840 minutos de rango visible).
     *
     * @param pc planificación a evaluar
     * @return porcentaje de desplazamiento vertical (0 – 100)
     */
    public double getTopPorcentaje(PlanificacionCurso pc) {
        if (pc == null) return 0;
        int horaInicio = pc.getPlcuHoraInicio() != null ? pc.getPlcuHoraInicio() : 8;
        // Rango visible: 07:00 (420 min) – 21:00 (1260 min) → 840 min totales
        int minutos = horaInicio * 60;
        int minInicio = 420; // 07:00
        int rangoMin = 840;
        double pct = ((minutos - minInicio) * 100.0) / rangoMin;
        return Math.max(0, Math.min(pct, 95));
    }

    /**
     * Calcula el porcentaje de altura del bloque de un evento según su duración.
     *
     * @param pc planificación a evaluar
     * @return porcentaje de altura (mínimo 4 %)
     */
    public double getAlturaPorcentaje(PlanificacionCurso pc) {
        if (pc == null || pc.getPlcuTiempoDuracion() == null) return 4;
        double horas = pc.getPlcuTiempoDuracion().doubleValue();
        // Cada hora ocupa (60/840)*100 ≈ 7.14 % de la altura
        double pct = (horas * 60.0 * 100.0) / 840.0;
        return Math.max(4, Math.min(pct, 95));
    }

    /**
     * Retorna una clase CSS de color según el día de la semana para distinguir
     * visualmente los eventos del calendario.
     *
     * @param pc planificación a evaluar
     * @return nombre de clase CSS
     */
    public String getClaseColorEvento(PlanificacionCurso pc) {
        if (pc == null || pc.getPlcuFechaInicio() == null) return "cal-evento-blue";
        switch (pc.getPlcuFechaInicio().getDayOfWeek()) {
            case MONDAY:    return "cal-evento-blue";
            case TUESDAY:   return "cal-evento-purple";
            case WEDNESDAY: return "cal-evento-yellow";
            case THURSDAY:  return "cal-evento-red";
            case FRIDAY:    return "cal-evento-teal";
            case SATURDAY:  return "cal-evento-orange";
            case SUNDAY:    return "cal-evento-indigo";
            default:        return "cal-evento-blue";
        }
    }

    /**
     * Arma el contenido HTML del tooltip que se muestra al pasar el mouse
     * sobre un evento del calendario.
     */
    private String armarTooltip(PlanificacionCurso pc, LocalDateTime start, LocalDateTime end, String titulo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"cal-tt\">");
        sb.append("<div class=\"cal-tt-titulo\">").append(escHtml(titulo)).append("</div>");

        if (pc.getInstructor() != null && pc.getInstructor().getPersona() != null) {
            sb.append("<div class=\"cal-tt-row\">");
            sb.append("<span class=\"cal-tt-label\">Instructor:</span> ");
            sb.append(escHtml(pc.getInstructor().getPersona().getPersNombres()))
              .append(" ").append(escHtml(pc.getInstructor().getPersona().getPersApellidos()));
            sb.append("</div>");
        }

        sb.append("<div class=\"cal-tt-row\">");
        sb.append("<span class=\"cal-tt-label\">Horario:</span> ");
        sb.append(String.format("%02d", pc.getPlcuHoraInicio())).append(":00 &ndash; ")
          .append(String.format("%02d", end.getHour())).append(":")
          .append(String.format("%02d", end.getMinute()));
        sb.append("</div>");

        sb.append("<div class=\"cal-tt-row\">");
        sb.append("<span class=\"cal-tt-label\">Duraci&oacute;n:</span> ");
        sb.append(pc.getPlcuTiempoDuracion()).append(" h");
        sb.append("</div>");

        if (pc.getPlcuUbicacion() != null && !pc.getPlcuUbicacion().isBlank()) {
            sb.append("<div class=\"cal-tt-row\">");
            sb.append("<span class=\"cal-tt-label\">Ubicaci&oacute;n:</span> ");
            sb.append(escHtml(pc.getPlcuUbicacion()));
            sb.append("</div>");
        }

        if (pc.getPlcuWebinarCharla() != null && !pc.getPlcuWebinarCharla().isBlank()) {
            sb.append("<div class=\"cal-tt-row\">");
            sb.append("<span class=\"cal-tt-label\">Webinar:</span> ");
            sb.append(escHtml(pc.getPlcuWebinarCharla()));
            sb.append("</div>");
        }

        sb.append("</div>");
        return sb.toString();
    }

    private String escHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;").replace("<", "&lt;")
                    .replace(">", "&gt;").replace("\"", "&quot;");
    }

    // =========================================================
    // Helpers de formato de fechas (LocalDate → String)
    // =========================================================

    /**
     * Devuelve la etiqueta de rango de la semana visible.
     * Ejemplo: "22 – 28 de junio 2026"
     */
    public String getEtiquetaRangoSemana() {
        LocalDate inicio = getBeanCalendarioCursos().getInicioSemana();
        LocalDate fin    = getBeanCalendarioCursos().getFinSemana();
        if (inicio == null || fin == null) return "";
        DateTimeFormatter fmtDia  = DateTimeFormatter.ofPattern("d", new Locale("es"));
        DateTimeFormatter fmtFin  = DateTimeFormatter.ofPattern("d 'de' MMMM yyyy", new Locale("es"));
        return inicio.format(fmtDia) + " \u2013 " + fin.format(fmtFin);
    }

    /**
     * Devuelve el número de día (como String) de inicioSemana + offset días.
     * Offset 0 = Lunes, 1 = Martes, … 6 = Domingo.
     *
     * @param offset número de días a sumar al inicio de la semana
     * @return día del mes como String, p.ej. "22"
     */
    public String getDiaNumero(int offset) {
        LocalDate inicio = getBeanCalendarioCursos().getInicioSemana();
        if (inicio == null) return "";
        return String.valueOf(inicio.plusDays(offset).getDayOfMonth());
    }

    /**
     * Indica si el día en el offset dado es el día actual (para resaltarlo).
     *
     * @param offset número de días a sumar al inicio de la semana
     * @return true si ese día coincide con hoy
     */
    public boolean esDiaHoy(int offset) {
        LocalDate inicio = getBeanCalendarioCursos().getInicioSemana();
        if (inicio == null) return false;
        return inicio.plusDays(offset).equals(LocalDate.now());
    }

    // =========================================================
    // PrimeFaces Schedule Listeners
    // =========================================================

    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        ScheduleEvent<?> event = selectEvent.getObject();
        PlanificacionCurso pc = (PlanificacionCurso) event.getData();
        editarPlanificacion(pc);
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        LocalDateTime date = selectEvent.getObject();
        PlanificacionCurso nueva = new PlanificacionCurso();
        nueva.setPlcuFechaInicio(date.toLocalDate());
        nueva.setPlcuHoraInicio(date.getHour());
        nueva.setPlcuTiempoDuracion(new BigDecimal("1.00"));
        
        getBeanCalendarioCursos().setPlanificacionSeleccionada(nueva);
        getBeanCalendarioCursos().setCursId(null);
        getBeanCalendarioCursos().setInstId(null);
    }
}
