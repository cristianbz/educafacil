package ec.mileniumtech.educafacil.backing.facelectronica;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
/**
* @author christian May 21, 2025
*/
@Path("/firma-electronica")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FirmaElectronicaResource {

    @Inject
    FirmaElectronicaService firmaService;

    @POST
    @Path("/validar")
    @Operation(summary = "Validar archivo P12")
    public Response validarP12(@RequestBody ValidacionRequest request) {
        try {
            boolean valido = firmaService.validarP12(request.getP12Data(), request.getPassword());
            return Response.ok(new ValidacionResponse(valido)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Error al validar P12: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/firmar")
    @Operation(summary = "Firmar documento")
    public Response firmarDocumento(@RequestBody FirmaRequest request) {
        try {
            byte[] firma = firmaService.firmarDocumento(
                request.getP12Data(), 
                request.getPassword(), 
                request.getDocumento());
            
            return Response.ok(new FirmaResponse(firma)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Error al firmar: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/verificar")
    @Operation(summary = "Verificar firma")
    public Response verificarFirma(@RequestBody VerificacionRequest request) {
        try {
            boolean valido = firmaService.verificarFirma(
                request.getP12Data(), 
                request.getPassword(), 
                request.getDocumento(), 
                request.getFirma());
            
            return Response.ok(new VerificacionResponse(valido)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Error al verificar: " + e.getMessage()))
                    .build();
        }
    }
}
