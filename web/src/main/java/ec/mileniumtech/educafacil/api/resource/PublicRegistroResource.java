package ec.mileniumtech.educafacil.api.resource;

import ec.mileniumtech.educafacil.api.dto.ApiResponse;
import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaCreateRequest;
import ec.mileniumtech.educafacil.api.dto.matricula.MatriculaResponse;
import ec.mileniumtech.educafacil.api.dto.matricula.OfertaCursoResponse;
import ec.mileniumtech.educafacil.api.dto.matricula.PersonaResponse;
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
 * Recurso REST público para el registro de nuevos estudiantes.
 * <p>
 * Este resource NO requiere autenticación JWT. Está diseñado
 * para ser consumido desde el formulario público de matrícula
 * en línea (React, Angular, etc.), emulando el flujo de la
 * página {@code registromatricula.xhtml}.
 * </p>
 *
 * <p>Endpoints públicos:</p>
 * <ul>
 *   <li>{@code GET /api/v1/public/ofertas} — cursos disponibles</li>
 *   <li>{@code GET /api/v1/public/personas/{cedula}} — consulta de persona</li>
 *   <li>{@code POST /api/v1/public/matriculas} — registro completo</li>
 * </ul>
 */
@Path("/v1/public")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicRegistroResource {

    @Inject
    private MatriculaRestService matriculaService;

    @Context
    private UriInfo uriInfo;

    // =========================================================
    // 1. CURSOS DISPONIBLES
    // =========================================================

    /**
     * Lista las ofertas de cursos activas para que el estudiante
     * seleccione en qué curso desea matricularse.
     *
     * @return lista de cursos disponibles
     */
    @GET
    @Path("/ofertas")
    public Response listarOfertas() {
        List<OfertaCursoResponse> ofertas = matriculaService.listarOfertasActivas();
        return Response.ok(ApiResponse.ok(ofertas)).build();
    }

    // =========================================================
    // 2. CONSULTAR PERSONA POR CÉDULA
    // =========================================================

    /**
     * Consulta si una persona ya existe en el sistema por su cédula.
     * <p>
     * Si existe, retorna sus datos (y los de su estudiante asociado)
     * para pre‑llenar el formulario. Si no existe, retorna {@code 404}
     * para que el frontend muestre el formulario vacío.
     * </p>
     *
     * @param cedula número de documento (cédula/RUC)
     * @return datos de la persona + estudiante, o 404 si no existe
     */
    @GET
    @Path("/personas/{cedula}")
    public Response buscarPersona(@PathParam("cedula") String cedula) {
        PersonaResponse response = matriculaService.buscarPersonaPorCedula(cedula);
        return Response.ok(ApiResponse.ok(response, "Persona encontrada")).build();
    }

    // =========================================================
    // 3. CREAR MATRÍCULA (REGISTRO COMPLETO)
    // =========================================================

    /**
     * Crea una nueva matrícula con todos los datos del formulario.
     * <p>
     * Este endpoint:
     * <ul>
     *   <li>Crea o actualiza la {@code Persona}</li>
     *   <li>Crea o actualiza el {@code Estudiante}</li>
     *   <li>Crea la {@code Matricula} asociada a la oferta seleccionada</li>
     *   <li>Opcionalmente registra datos de facturación a empresa</li>
     * </ul>
     * </p>
     *
     * @param request todos los campos del formulario de matrícula
     * @return matrícula creada con HTTP 201
     */
    @POST
    @Path("/matriculas")
    public Response crearMatricula(@Valid MatriculaCreateRequest request) {
        MatriculaResponse response = matriculaService.crearMatricula(request);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(response.getId()))
                .build();
        return Response.created(location)
                .entity(ApiResponse.ok(response, "Matrícula creada exitosamente"))
                .build();
    }
}