package ec.mileniumtech.educafacil.api.service;

import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaCreateRequest;
import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaFilterRequest;
import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaResponse;
import ec.mileniumtech.educafacil.api.dto.matricula.PersonaResponse;
import ec.mileniumtech.educafacil.api.exception.BadRequestException;
import ec.mileniumtech.educafacil.api.exception.ConflictException;
import ec.mileniumtech.educafacil.api.exception.ResourceNotFoundException;
import ec.mileniumtech.educafacil.api.mapper.MatriculaMapper;
import ec.mileniumtech.educafacil.api.mapper.PersonaMapper;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.*;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumEstadosMatricula;
import ec.mileniumtech.educafacil.utilitarios.enumeraciones.EnumRol;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para la gestión REST de matrículas.
 * Orquesta las operaciones entre los recursos REST y los EJBs existentes.
 * <p>
 * Encapsula:
 * <ul>
 *   <li>Validaciones de negocio</li>
 *   <li>Transformación entre DTOs y entidades</li>
 *   <li>Transaccionalidad</li>
 *   <li>Manejo de errores con excepciones específicas de API</li>
 * </ul>
 * </p>
 */
@Stateless
public class MatriculaRestService {

    private static final Logger log = LogManager.getLogger(MatriculaRestService.class);

    @EJB
    private MatriculaFacade matriculaFacade;

    // =========================================================
    // Persona
    // =========================================================

    /**
     * Busca una persona por su número de cédula.
     *
     * @param cedula documento de identidad
     * @return datos de la persona y su estudiante asociado (si existe)
     * @throws ResourceNotFoundException si no se encuentra la persona
     */
    public PersonaResponse buscarPersonaPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new BadRequestException("La cédula es obligatoria");
        }

        Estudiante estudiante = matriculaFacade.estudiantesPorCedula(cedula);
        if (estudiante != null) {
            return PersonaMapper.toResponse(estudiante.getPersona(), estudiante);
        }

        Persona persona = matriculaFacade.buscarPersonaPorCedula(cedula);
        if (persona == null) {
            throw new ResourceNotFoundException("Persona con cédula " + cedula);
        }

        return PersonaMapper.toResponse(persona, null);
    }

    // =========================================================
    // Matrícula
    // =========================================================

    /**
     * Crea una nueva matrícula/inscripción con todos los datos del formulario.
     *
     * @param request datos completos de la matrícula
     * @return matrícula creada
     */
    public MatriculaResponse crearMatricula(MatriculaCreateRequest request) {
        validarRequest(request);

        // 1. Buscar o crear Persona
        Persona persona = matriculaFacade.buscarPersonaPorCedula(request.getDocumentoIdentidad());
        if (persona == null) {
            persona = crearPersonaDesdeRequest(request);
            matriculaFacade.guardarPersona(persona);
            persona = matriculaFacade.buscarPersonaPorCedula(request.getDocumentoIdentidad());
        } else {
            actualizarPersonaDesdeRequest(persona, request);
            matriculaFacade.actualizarPersona(persona);
        }

        // 2. Buscar o crear Estudiante
        Estudiante estudiante = matriculaFacade.estudiantesPorCedula(request.getDocumentoIdentidad());
        if (estudiante == null) {
            estudiante = new Estudiante();
            estudiante.setPersona(persona);
            estudiante.setEstuCargoOcupa(request.getCargoOcupa());
            estudiante.setEstuNivelEstudio(request.getNivelEstudio());
            estudiante.setEstuUltimoCurso(request.getUltimoCurso());
            estudiante.setEstuDireccionTrabajo(request.getDireccionTrabajo());
            estudiante.setEstuIngresosMensuales(request.getIngresosMensuales());
            estudiante.setEstuTelefonoTrabajo(request.getTelefonoTrabajo());
            matriculaFacade.guardarEstudiante(estudiante);
            estudiante = matriculaFacade.estudiantesPorCedula(request.getDocumentoIdentidad());
        } else {
            estudiante.setEstuCargoOcupa(request.getCargoOcupa());
            estudiante.setEstuNivelEstudio(request.getNivelEstudio());
            estudiante.setEstuUltimoCurso(request.getUltimoCurso());
            estudiante.setEstuDireccionTrabajo(request.getDireccionTrabajo());
            estudiante.setEstuIngresosMensuales(request.getIngresosMensuales());
            estudiante.setEstuTelefonoTrabajo(request.getTelefonoTrabajo());
            matriculaFacade.actualizaEstudiante(estudiante);
        }

        // 3. Obtener OfertaCursos
        OfertaCursos ofertaCurso = obtenerOfertaCurso(request.getOfertaCursoId());

        // 4. Validar que no exista matrícula duplicada
        Matricula existente = matriculaFacade.existeMatricula(
                request.getOfertaCursoId(), estudiante.getEstuId());
        if (existente != null) {
            throw new ConflictException(
                    "El estudiante ya tiene una matrícula activa en este curso");
        }

        // 5. Crear Matricula
        Matricula matricula = MatriculaMapper.toEntity(request);
        matricula.setEstudiante(estudiante);
        matricula.setOfertaCursos(ofertaCurso);

        // Empresa (facturación)
        if (Boolean.TRUE.equals(request.getFacturacionEmpresa())
                && request.getEmpresaRuc() != null) {
            Empresa empresa = new Empresa();
            empresa.setEmprRuc(request.getEmpresaRuc());
            empresa.setEmprNombre(request.getEmpresaRazonSocial());
            empresa.setEmprDireccion(request.getEmpresaDireccion());
            empresa.setEmprTelefono(request.getEmpresaTelefono());
            empresa.setEmprEstado(true);
            matricula.setEmpresa(empresa);
        }

        // 6. Persistir
        matriculaFacade.agregarMatriculaInscripcion(
                persona, matricula, null, null);

        matricula = matriculaFacade.existeMatricula(
                request.getOfertaCursoId(), estudiante.getEstuId());

        if (matricula == null) {
            throw new RuntimeException("Error al crear la matrícula");
        }

        log.info("Matrícula creada exitosamente: ID={}, Estudiante={}",
                matricula.getMatrId(), estudiante.getEstuId());

        return MatriculaMapper.toResponse(matricula);
    }

    /**
     * Obtiene una matrícula por su ID.
     */
    public MatriculaResponse obtenerMatricula(Integer id) {
        if (id == null) {
            throw new BadRequestException("El ID de matrícula es obligatorio");
        }

        // Buscar usando el facade (necesitamos un método que busque por ID)
        List<Matricula> todas = matriculaFacade.listaTodasMatriculas();
        Matricula matricula = todas.stream()
                .filter(m -> m.getMatrId().equals(id))
                .findFirst()
                .orElse(null);

        if (matricula == null) {
            throw new ResourceNotFoundException("Matrícula", id);
        }

        return MatriculaMapper.toResponse(matricula);
    }

    /**
     * Lista matrículas aplicando filtros.
     */
    public List<MatriculaResponse> listarMatriculas(MatriculaFilterRequest filtro) {
        List<Matricula> resultados;

        if (filtro.getEstudianteId() != null) {
            resultados = matriculaFacade.listaMatriculasEstudiante(filtro.getEstudianteId());
        } else if (filtro.getEstado() != null && filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
            resultados = matriculaFacade.listaMatriculasInscripcion(
                    filtro.getEstado(), filtro.getFechaInicio(), filtro.getFechaFin());
        } else if (filtro.getEstado() != null && filtro.getCursoId() != null) {
            resultados = matriculaFacade.listaMatriculasCurso(
                    filtro.getEstado(), filtro.getCursoId());
        } else if (filtro.getAnio() != null) {
            resultados = matriculaFacade.listaMatriculasPorAnio(filtro.getAnio());
        } else if (filtro.getCedula() != null) {
            Estudiante est = matriculaFacade.estudiantesPorCedula(filtro.getCedula());
            if (est != null) {
                resultados = matriculaFacade.listaMatriculasEstudiante(est.getEstuId());
            } else {
                return List.of();
            }
        } else {
            resultados = matriculaFacade.listaTodasMatriculas();
        }

        return MatriculaMapper.toResponseList(resultados);
    }

    /**
     * Actualiza el estado de una matrícula.
     */
    public MatriculaResponse actualizarEstadoMatricula(Integer id, String nuevoEstado) {
        Matricula matricula = buscarMatriculaPorId(id);

        if (!esEstadoValido(nuevoEstado)) {
            throw new BadRequestException(
                    "Estado inválido: " + nuevoEstado
                            + ". Valores permitidos: INSMAT01, INSMAT02, INSMAT03, INSMAT05");
        }

        matricula.setMatrEstado(nuevoEstado);
        matriculaFacade.actualizarMatricula(matricula);

        log.info("Matrícula {} actualizada a estado {}", id, nuevoEstado);
        return MatriculaMapper.toResponse(matricula);
    }

    /**
     * Anula/cancela una matrícula (cambia estado a DESERTADO).
     */
    public MatriculaResponse anularMatricula(Integer id, String motivo) {
        Matricula matricula = buscarMatriculaPorId(id);
        matricula.setMatrEstado(EnumEstadosMatricula.DESERTADO.getCodigo());
        matricula.setMatrMotivoAbandono(motivo);
        matriculaFacade.actualizarMatricula(matricula);

        log.info("Matrícula {} anulada. Motivo: {}", id, motivo);
        return MatriculaMapper.toResponse(matricula);
    }

    // =========================================================
    // OfertaCursos
    // =========================================================

    /**
     * Lista las ofertas de cursos activas disponibles.
     */
    public List<ec.mileniumtech.educafacil.api.dto.matricula.OfertaCursoResponse>
    listarOfertasActivas() {
        List<OfertaCursos> ofertas = matriculaFacade.listaOfertaCursosActivos();
        return MatriculaMapper.toOfertaResponseList(ofertas);
    }

    /**
     * Obtiene una oferta de curso por ID.
     */
    public ec.mileniumtech.educafacil.api.dto.matricula.OfertaCursoResponse
    obtenerOferta(Integer id) {
        List<OfertaCursos> activas = matriculaFacade.listaOfertaCursosActivos();
        OfertaCursos oc = activas.stream()
                .filter(o -> o.getOcurId() == id)
                .findFirst()
                .orElse(null);
        if (oc == null) {
            // Buscar en activas/cerradas
            List<OfertaCursos> activasCerradas = matriculaFacade.listaOfertaCursosActivosCerrados();
            oc = activasCerradas.stream()
                    .filter(o -> o.getOcurId() == id)
                    .findFirst()
                    .orElse(null);
        }
        if (oc == null) {
            throw new ResourceNotFoundException("Oferta de curso", id);
        }
        return MatriculaMapper.toOfertaResponse(oc);
    }

    // =========================================================
    // Métodos privados
    // =========================================================

    private void validarRequest(MatriculaCreateRequest request) {
        if (request.getDocumentoIdentidad() == null || request.getDocumentoIdentidad().isBlank()) {
            throw new BadRequestException("La cédula/RUC es obligatoria");
        }
        if (request.getNombres() == null || request.getNombres().isBlank()) {
            throw new BadRequestException("Los nombres son obligatorios");
        }
        if (request.getApellidos() == null || request.getApellidos().isBlank()) {
            throw new BadRequestException("Los apellidos son obligatorios");
        }
        if (request.getOfertaCursoId() == null) {
            throw new BadRequestException("La oferta de curso es obligatoria");
        }
    }

    private Persona crearPersonaDesdeRequest(MatriculaCreateRequest request) {
        Persona persona = new Persona();
        persona.setPersDocumentoIdentidad(request.getDocumentoIdentidad());
        persona.setPersNombres(request.getNombres());
        persona.setPersApellidos(request.getApellidos());
        persona.setPersTelefonoMobil(request.getTelefonoMobil());
        persona.setPersTelefonoCasa(request.getTelefonoCasa());
        persona.setPersCorreoElectronico(request.getCorreoElectronico());
        persona.setPersDomicilio(request.getDomicilio());
        persona.setPersNacionalidad(request.getNacionalidad());
        persona.setPersEstadoCivil(request.getEstadoCivil());
        persona.setPersCargasFamiliares(request.getCargasFamiliares() != null && request.getCargasFamiliares());
        persona.setPersFechaNacimiento(request.getFechaNacimiento());
        persona.setPersProvincia(request.getProvincia());
        persona.setPersCiudad(request.getCiudad());
        persona.setPersSector(request.getSector());
        return persona;
    }

    private void actualizarPersonaDesdeRequest(Persona persona, MatriculaCreateRequest request) {
        persona.setPersNombres(request.getNombres());
        persona.setPersApellidos(request.getApellidos());
        persona.setPersTelefonoMobil(request.getTelefonoMobil());
        persona.setPersTelefonoCasa(request.getTelefonoCasa());
        persona.setPersCorreoElectronico(request.getCorreoElectronico());
        persona.setPersDomicilio(request.getDomicilio());
        persona.setPersNacionalidad(request.getNacionalidad());
        persona.setPersEstadoCivil(request.getEstadoCivil());
        persona.setPersCargasFamiliares(request.getCargasFamiliares() != null && request.getCargasFamiliares());
        persona.setPersFechaNacimiento(request.getFechaNacimiento());
        persona.setPersProvincia(request.getProvincia());
        persona.setPersCiudad(request.getCiudad());
        persona.setPersSector(request.getSector());
    }

    private OfertaCursos obtenerOfertaCurso(Integer ofertaCursoId) {
        List<OfertaCursos> activas = matriculaFacade.listaOfertaCursosActivos();
        for (OfertaCursos oc : activas) {
            if (oc.getOcurId() == ofertaCursoId) {
                return oc;
            }
        }
        throw new ResourceNotFoundException("Oferta de curso activa", ofertaCursoId);
    }

    private Matricula buscarMatriculaPorId(Integer id) {
        List<Matricula> todas = matriculaFacade.listaTodasMatriculas();
        Matricula matricula = todas.stream()
                .filter(m -> m.getMatrId().equals(id))
                .findFirst()
                .orElse(null);
        if (matricula == null) {
            throw new ResourceNotFoundException("Matrícula", id);
        }
        return matricula;
    }

    private boolean esEstadoValido(String estado) {
        if (estado == null) return false;
        for (EnumEstadosMatricula e : EnumEstadosMatricula.values()) {
            if (e.getCodigo().equals(estado)) return true;
        }
        return false;
    }
}
