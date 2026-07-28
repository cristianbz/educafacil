package ec.mileniumtech.educafacil.api.mapper;

import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaCreateRequest;
import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaResponse;
import ec.mileniumtech.educafacil.api.dto.matricula.OfertaCursoResponse;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.*;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosMatricula;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades del dominio de matrícula
 * y DTOs de la API REST.
 * <p>
 * Sigue el patrón mapper estático sin dependencias de frameworks.
 * Las conversiones son explícitas para mantener claridad y testabilidad.
 * </p>
 */
public final class MatriculaMapper {

    private static final Logger log = LogManager.getLogger(MatriculaMapper.class);

    private MatriculaMapper() {
        // Utilidad — no instanciable
    }

    // =========================================================
    // Entity → Response
    // =========================================================

    /**
     * Convierte una entidad Matricula a su DTO de respuesta.
     */
    public static MatriculaResponse toResponse(Matricula entity) {
        if (entity == null) return null;

        MatriculaResponse response = MatriculaResponse.builder()
                .id(entity.getMatrId())
                .estado(entity.getMatrEstado())
                .estadoLabel(obtenerLabelEstado(entity.getMatrEstado()))
                .fechaRegistro(entity.getMatrFechaRegistro())
                .fechaMatricula(entity.getMatrFechaMatricula())
                .fechaInscripcion(entity.getMatrFechaInscripcion())
                .observacion(entity.getMatrObservacion())
                .medioInformacion(entity.getMatrMedioInformacion())
                .motivacionCurso(entity.getMatrMotivacionCurso())
                .trabajaEnArea(entity.isMatrTrabajaEnArea())
                .paraQueCurso(entity.getMatrParaQueCurso())
                .saldoPagoCurso(entity.getMatrSaldoPagoCurso())
                .fechaUltimoPago(entity.getMatrFechaUltimoPago())
                .facturacionEmpresa(entity.isMatrFacturacionEmpresa())
                .build();

        // Estudiante
        if (entity.getEstudiante() != null) {
            response.setEstudiante(toEstudianteResponse(entity.getEstudiante()));
        }

        // Oferta Curso
        if (entity.getOfertaCursos() != null) {
            response.setOfertaCurso(toOfertaCursoResponse(entity.getOfertaCursos()));
        }

        // Empresa
        if (entity.getEmpresa() != null) {
            response.setEmpresa(toEmpresaResponse(entity.getEmpresa()));
        }

        // Pagos
        if (entity.getPagos() != null && !entity.getPagos().isEmpty()) {
            response.setPagos(entity.getPagos().stream()
                    .map(MatriculaMapper::toPagoResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setPagos(Collections.emptyList());
        }

        return response;
    }

    /**
     * Convierte una lista de entidades a lista de DTOs.
     */
    public static List<MatriculaResponse> toResponseList(List<Matricula> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(MatriculaMapper::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Request → Entity (parcial, para creación/actualización)
    // =========================================================

    /**
     * Convierte un request de creación en una entidad Matricula.
     * No asigna relaciones (estudiante, ofertaCurso, empresa) — eso
     * debe hacerse en la capa de servicio con las entidades ya persistidas.
     */
    public static Matricula toEntity(MatriculaCreateRequest request) {
        if (request == null) return null;

        Matricula entity = new Matricula();
        entity.setMatrEstado(EnumEstadosMatricula.INSCRITO.getCodigo());
        entity.setMatrFechaRegistro(new java.util.Date());
        entity.setMatrFechaInscripcion(request.getFechaInscripcion() != null
                ? request.getFechaInscripcion() : new java.util.Date());
        entity.setMatrFechaMatricula(request.getFechaMatricula());
        entity.setMatrObservacion(request.getObservacion());
        entity.setMatrMedioInformacion(request.getMedioInformacion());
        entity.setMatrParaQueCurso(request.getParaQueCurso());
        entity.setMatrTrabajaEnArea(request.getTrabajaEnArea() != null && request.getTrabajaEnArea());
        entity.setMatrMotivacionCurso(request.getMotivacionCurso());
        entity.setMatrFacturacionEmpresa(request.getFacturacionEmpresa() != null && request.getFacturacionEmpresa());

        return entity;
    }

    // =========================================================
    // Métodos privados de conversión
    // =========================================================

    private static MatriculaResponse.EstudianteResponse toEstudianteResponse(Estudiante e) {
        MatriculaResponse.EstudianteResponse.EstudianteResponseBuilder builder =
                MatriculaResponse.EstudianteResponse.builder()
                        .id(e.getEstuId())
                        .cargoOcupa(e.getEstuCargoOcupa())
                        .nivelEstudio(e.getEstuNivelEstudio())
                        .ultimoCurso(e.getEstuUltimoCurso());

        if (e.getPersona() != null) {
            builder.persona(MatriculaResponse.PersonaResponse.builder()
                    .id(e.getPersona().getPersId())
                    .nombres(e.getPersona().getPersNombres())
                    .apellidos(e.getPersona().getPersApellidos())
                    .documentoIdentidad(e.getPersona().getPersDocumentoIdentidad())
                    .telefonoMobil(e.getPersona().getPersTelefonoMobil())
                    .telefonoCasa(e.getPersona().getPersTelefonoCasa())
                    .correoElectronico(e.getPersona().getPersCorreoElectronico())
                    .domicilio(e.getPersona().getPersDomicilio())
                    .estadoCivil(e.getPersona().getPersEstadoCivil())
                    .fechaNacimiento(e.getPersona().getPersFechaNacimiento())
                    .provincia(e.getPersona().getPersProvincia())
                    .ciudad(e.getPersona().getPersCiudad())
                    .build());
        }

        return builder.build();
    }

    private static MatriculaResponse.OfertaCursoResponse toOfertaCursoResponse(OfertaCursos oc) {
        MatriculaResponse.OfertaCursoResponse.OfertaCursoResponseBuilder builder =
                MatriculaResponse.OfertaCursoResponse.builder()
                        .id(oc.getOcurId())
                        .fechaInicio(oc.getOcurFechaInicio())
                        .fechaFin(oc.getOcurFechaFin())
                        .valor(oc.getOcurValor())
                        .descuento(oc.getOcurDescuento())
                        .tipo(oc.getOcurTipo())
                        .estado(oc.getOcurEstado())
                        .duracion(oc.getOcurDuracion())
                        .horario(oc.getOcurHorario())
                        .modalidad(oc.getOcurModalidad());

        if (oc.getOfertaCapacitacion() != null
                && oc.getOfertaCapacitacion().getCurso() != null) {
            builder.cursoNombre(oc.getOfertaCapacitacion().getCurso().getCursNombre());
        }

        if (oc.getInstructor() != null) {
            builder.instructorNombre(oc.getInstructor().getPersona() != null
                    ? oc.getInstructor().getPersona().getPersNombres() + " "
                    + oc.getInstructor().getPersona().getPersApellidos()
                    : null);
        }

        return builder.build();
    }

    private static MatriculaResponse.EmpresaResponse toEmpresaResponse(Empresa emp) {
        return MatriculaResponse.EmpresaResponse.builder()
                .id(emp.getEmprId())
                .ruc(emp.getEmprRuc())
                .razonSocial(emp.getEmprNombre())
                .direccion(emp.getEmprDireccion())
                .telefono(emp.getEmprTelefono())
                .build();
    }

    private static MatriculaResponse.PagoResponse toPagoResponse(Pagos pago) {
        String formaPago = null;
        BigDecimal valor = null;
        if (pago.getDetallePagos() != null && !pago.getDetallePagos().isEmpty()) {
            var detalle = pago.getDetallePagos().get(0);
            valor = detalle.getDepaValor();
            formaPago = detalle.getDepaFormaPago();
        }
        return MatriculaResponse.PagoResponse.builder()
                .id(pago.getPagoId())
                .valor(valor)
                .fechaPago(pago.getPagoFecha())
                .formaPago(formaPago)
                .estado(pago.getPagoObservacion())
                .build();
    }

    // =========================================================
    // OfertaCursos → OfertaCursoResponse (independiente)
    // =========================================================

    /**
     * Convierte una entidad OfertaCursos a su DTO de respuesta.
     */
    public static OfertaCursoResponse toOfertaResponse(OfertaCursos oc) {
        if (oc == null) return null;

        OfertaCursoResponse response = OfertaCursoResponse.builder()
                .id(oc.getOcurId())
                .fechaInicio(oc.getOcurFechaInicio())
                .fechaFin(oc.getOcurFechaFin())
                .valor(oc.getOcurValor())
                .descuento(oc.getOcurDescuento())
                .tipo(oc.getOcurTipo())
                .estado(oc.getOcurEstado())
                .duracion(oc.getOcurDuracion())
                .horario(oc.getOcurHorario())
                .modalidad(oc.getOcurModalidad())
                .grupoWhatsapp(oc.getOcurGrupoWhatsapp())
                .build();

        // Curso info
        if (oc.getOfertaCapacitacion() != null) {
            Curso curso = oc.getOfertaCapacitacion().getCurso();
            if (curso != null) {
                response.setCurso(OfertaCursoResponse.CursoInfo.builder()
                        .id(curso.getCursId())
                        .nombre(curso.getCursNombre())
                        .contenido(curso.getCursContenido())
                        .area(oc.getOfertaCapacitacion().getArea() != null
                                ? oc.getOfertaCapacitacion().getArea().getAreaNombre() : null)
                        .especialidad(oc.getOfertaCapacitacion().getEspecialidad() != null
                                ? oc.getOfertaCapacitacion().getEspecialidad().getEspeNombre() : null)
                        .build());
            }
        }

        // Instructor info
        if (oc.getInstructor() != null && oc.getInstructor().getPersona() != null) {
            Persona p = oc.getInstructor().getPersona();
            response.setInstructor(OfertaCursoResponse.InstructorInfo.builder()
                    .id(oc.getInstructor().getInstId())
                    .nombres(p.getPersNombres())
                    .apellidos(p.getPersApellidos())
                    .correo(p.getPersCorreoElectronico())
                    .build());
        }

        return response;
    }

    /**
     * Convierte una lista de OfertaCursos a lista de DTOs.
     */
    public static List<OfertaCursoResponse> toOfertaResponseList(List<OfertaCursos> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(MatriculaMapper::toOfertaResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Utilitarios
    // =========================================================

    private static String obtenerLabelEstado(String codigo) {
        if (codigo == null) return null;
        try {
            for (EnumEstadosMatricula e : EnumEstadosMatricula.values()) {
                if (e.getCodigo().equals(codigo)) {
                    return e.getLabel();
                }
            }
        } catch (Exception ex) {
            log.warn("Código de estado de matrícula no reconocido: {}", codigo);
        }
        return codigo;
    }
}
