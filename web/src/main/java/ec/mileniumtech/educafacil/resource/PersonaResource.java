package ec.mileniumtech.educafacil.resource;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Persona;
import ec.mileniumtech.educafacil.service.PersonaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
* @author christian Aug 27, 2025
*/
/**
 * 
 */
@Path("/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonaResource {
	@Inject
	PersonaService personaService;
	
	@GET
	@Path("/buscacedulacorreo")
	public Response getPersonaPorCedulaCorreo(@QueryParam("cedula")String cedula,@QueryParam("correo")String correo) {
		Persona persona = personaService.buscarPersonaPorCedulaCorreo(cedula, correo);
        if (persona != null) {
            return Response.ok(persona).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
	}

}

