package ec.mileniumtech.educafacil.resource;

import ec.mileniumtech.educafacil.modelo.persistencia.dto.MatriculaDto;
import ec.mileniumtech.educafacil.service.AdministracionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 * Recurso REST para la gestión de Matrículas.
 */
@Path("/matriculas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MatriculaResource {

    @Inject
    private AdministracionService administracionService;

    @GET
    @Path("/estudiante/{id}")
    public List<MatriculaDto> listarPorEstudiante(@PathParam("id") int idEstudiante) {
        return administracionService.listarMatriculasEstudianteDto(idEstudiante);
    }
}
