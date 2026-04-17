package ec.mileniumtech.educafacil.resource;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.CursoDto;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import java.util.List;

/**
 * Recurso REST para la gestión de Cursos.
 */
@Path("/cursos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CursoResource {

    @Inject
    private AdministracionService administracionService;

    @GET
    @Operation(summary = "Lista todos los cursos disponibles", description = "Retorna una lista de cursos en formato DTO")
    @APIResponse(responseCode = "200", description = "Lista de cursos encontrada")
    public List<CursoDto> listarTodos() {
        return administracionService.listarCursosDto();
    }
    @POST
    @Operation(summary = "Crea un nuevo curso", description = "Persiste un nuevo curso en la base de datos")
    @APIResponse(responseCode = "201", description = "Curso creado exitosamente")
    public Response crearCurso(CursoDto cursoDto) {
        CursoDto nuevoCurso = administracionService.guardarCurso(cursoDto);
        return Response.status(Response.Status.CREATED).entity(nuevoCurso).build();
    }

    @PUT
    @Operation(summary = "Actualiza un curso existente", description = "Modifica los datos de un curso según su ID")
    @APIResponse(responseCode = "200", description = "Curso actualizado")
    public Response actualizarCurso(CursoDto cursoDto) {
        CursoDto cursoActualizado = administracionService.actualizarCursoDto(cursoDto);
        return Response.ok(cursoActualizado).build();
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Elimina un curso", description = "Borra un curso de la base de datos según su ID")
    @APIResponse(responseCode = "204", description = "Curso eliminado")
    public Response eliminarCurso(@PathParam("id") int id) {
        administracionService.eliminarCurso(id);
        return Response.noContent().build();
    }
}
