package ec.mileniumtech.educafacil.api.resource;

import ec.mileniumtech.educafacil.api.dto.ApiResponse;
import ec.mileniumtech.educafacil.api.dto.matricula.OfertaCursoResponse;
import ec.mileniumtech.educafacil.api.service.MatriculaRestService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Recurso REST para consulta de ofertas de cursos disponibles.
 * <p>
 * Endpoints públicos — no requieren autenticación.
 * </p>
 */
@Path("/v1/ofertas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OfertaResource {

    @Inject
    private MatriculaRestService matriculaService;

    /**
     * Lista todas las ofertas de cursos activas.
     *
     * @return lista de ofertas disponibles
     */
    @GET
    public Response listarActivas() {
        List<OfertaCursoResponse> ofertas = matriculaService.listarOfertasActivas();
        return Response.ok(ApiResponse.ok(ofertas)).build();
    }

    /**
     * Obtiene una oferta de curso por su ID.
     *
     * @param id ID de la oferta
     * @return datos de la oferta
     */
    @GET
    @Path("/{id}")
    public Response obtenerPorId(@PathParam("id") Integer id) {
        OfertaCursoResponse oferta = matriculaService.obtenerOferta(id);
        return Response.ok(ApiResponse.ok(oferta)).build();
    }
}
