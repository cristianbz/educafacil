package ec.mileniumtech.educafacil.api.resource;

import ec.mileniumtech.educafacil.api.dto.ApiResponse;
import ec.mileniumtech.educafacil.api.dto.matricula.PersonaBuscarRequest;
import ec.mileniumtech.educafacil.api.dto.matricula.PersonaResponse;
import ec.mileniumtech.educafacil.api.security.annotations.Secured;
import ec.mileniumtech.educafacil.api.service.MatriculaRestService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST para la gestión de personas (estudiantes/clientes).
 * <p>
 * Permite buscar, crear y actualizar datos de personas.
 * </p>
 */
@Secured
@Path("/v1/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonaResource {

    @Inject
    private MatriculaRestService matriculaService;

    /**
     * Busca una persona por su número de cédula.
     *
     * @param request contiene la cédula a buscar
     * @return datos de la persona y su estudiante asociado (si existe)
     */
    @POST
    @Path("/buscar")
    public Response buscarPorCedula(@Valid PersonaBuscarRequest request) {
        PersonaResponse response = matriculaService.buscarPersonaPorCedula(request.getCedula());
        return Response.ok(ApiResponse.ok(response, "Persona encontrada")).build();
    }

    /**
     * Obtiene una persona por su ID.
     *
     * @param id ID de la persona
     * @return datos de la persona
     */
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") Integer id) {
        // TODO: Implementar cuando se necesite
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
