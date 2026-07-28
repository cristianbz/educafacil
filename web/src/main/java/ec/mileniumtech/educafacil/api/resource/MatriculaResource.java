package ec.mileniumtech.educafacil.api.resource;

import ec.mileniumtech.educafacil.api.dto.ApiResponse;
import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaCreateRequest;
import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaFilterRequest;
import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaResponse;
import ec.mileniumtech.educafacil.api.security.annotations.Secured;
import ec.mileniumtech.educafacil.api.service.MatriculaRestService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

/**
 * Recurso REST para la gestión completa de matrículas.
 * <p>
 * Endpoints para CRUD de matrículas, incluyendo:
 * <ul>
 *   <li>Creación de matrícula (con persona, estudiante y curso)</li>
 *   <li>Consulta de matrículas con filtros</li>
 *   <li>Actualización de estado</li>
 *   <li>Anulación de matrículas</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Autenticación:</strong> Todos los endpoints requieren token JWT
 * excepto la creación de matrícula pública (sin anotación {@link Secured}).
 * </p>
 */
@Path("/v1/matriculas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MatriculaResource {

    @Inject
    private MatriculaRestService matriculaService;

    @Context
    private UriInfo uriInfo;

    /**
     * Crea una nueva matrícula/inscripción.
     * <p>
     * Endpoint público para registro de estudiantes.
     * </p>
     *
     * @param request datos completos de la matrícula
     * @return matrícula creada con HTTP 201
     */
    @POST
    public Response crearMatricula(@Valid MatriculaCreateRequest request) {
        MatriculaResponse response = matriculaService.crearMatricula(request);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(response.getId()))
                .build();
        return Response.created(location)
                .entity(ApiResponse.ok(response, "Matrícula creada exitosamente"))
                .build();
    }

    /**
     * Obtiene una matrícula por su ID.
     *
     * @param id ID de la matrícula
     * @return datos completos de la matrícula
     */
    @GET
    @Secured
    @Path("/{id}")
    public Response obtenerMatricula(@PathParam("id") Integer id) {
        MatriculaResponse response = matriculaService.obtenerMatricula(id);
        return Response.ok(ApiResponse.ok(response)).build();
    }

    /**
     * Lista matrículas con filtros opcionales.
     *
     * @param estado        filtro por estado (ej: INSMAT01, INSMAT02)
     * @param estudianteId  filtro por ID de estudiante
     * @param ofertaCursoId filtro por ID de oferta de curso
     * @param cursoId       filtro por ID de curso
     * @param cedula        filtro por cédula del estudiante
     * @param anio          filtro por año
     * @param page          número de página (0-based)
     * @param size          tamaño de página
     * @return lista de matrículas
     */
    @GET
    @Secured
    public Response listarMatriculas(
            @QueryParam("estado") String estado,
            @QueryParam("estudianteId") Integer estudianteId,
            @QueryParam("ofertaCursoId") Integer ofertaCursoId,
            @QueryParam("cursoId") Integer cursoId,
            @QueryParam("cedula") String cedula,
            @QueryParam("anio") Integer anio,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        MatriculaFilterRequest filtro = MatriculaFilterRequest.builder()
                .estado(estado)
                .estudianteId(estudianteId)
                .ofertaCursoId(ofertaCursoId)
                .cursoId(cursoId)
                .cedula(cedula)
                .anio(anio)
                .page(page)
                .size(size)
                .build();

        List<MatriculaResponse> lista = matriculaService.listarMatriculas(filtro);
        return Response.ok(ApiResponse.ok(lista)).build();
    }

    /**
     * Actualiza el estado de una matrícula.
     *
     * @param id         ID de la matrícula
     * @param nuevoEstado nuevo estado (INSMAT01, INSMAT02, INSMAT03, INSMAT05)
     * @return matrícula actualizada
     */
    @PUT
    @Secured
    @Path("/{id}/estado")
    public Response actualizarEstado(
            @PathParam("id") Integer id,
            @QueryParam("estado") String nuevoEstado) {
        MatriculaResponse response = matriculaService.actualizarEstadoMatricula(id, nuevoEstado);
        return Response.ok(ApiResponse.ok(response, "Estado actualizado")).build();
    }

    /**
     * Anula/cancela una matrícula.
     *
     * @param id     ID de la matrícula
     * @param motivo motivo de la anulación
     * @return matrícula anulada
     */
    @DELETE
    @Secured
    @Path("/{id}")
    public Response anularMatricula(
            @PathParam("id") Integer id,
            @QueryParam("motivo") String motivo) {
        MatriculaResponse response = matriculaService.anularMatricula(id, motivo);
        return Response.ok(ApiResponse.ok(response, "Matrícula anulada")).build();
    }
}
