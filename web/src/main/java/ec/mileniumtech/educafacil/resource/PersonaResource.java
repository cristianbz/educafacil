package ec.mileniumtech.educafacil.resource;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.PersonaDto;
import ec.mileniumtech.educafacil.service.AdministracionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST para la gestión de Personas.
 */
@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonaResource {

    @Inject
    private AdministracionService administracionService;
    
    @GET
    @Path("/buscacedulacorreo")
    public Response getPersonaPorCedulaCorreo(@QueryParam("cedula") String cedula, @QueryParam("correo") String correo) {
        PersonaDto personaDto = administracionService.buscarPersonaDto(cedula, correo);
        if (personaDto != null) {
            return Response.ok(personaDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @POST
    public Response crearPersona(PersonaDto personaDto) {
        PersonaDto nuevaPersona = administracionService.guardarPersona(personaDto);
        return Response.status(Response.Status.CREATED).entity(nuevaPersona).build();
    }

    @PUT
    public Response actualizarPersona(PersonaDto personaDto) {
        PersonaDto personaActualizada = administracionService.actualizarPersona(personaDto);
        return Response.ok(personaActualizada).build();
    }
    @DELETE
    @Path("/{id}")
    public Response eliminarPersona(@PathParam("id") int id) {
        administracionService.eliminarPersona(id);
        return Response.noContent().build();
    }
}
