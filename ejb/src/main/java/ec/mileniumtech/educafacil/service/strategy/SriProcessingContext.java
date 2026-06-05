package ec.mileniumtech.educafacil.service.strategy;

import java.time.OffsetDateTime;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Configuraciones;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import lombok.Data;

@Data
public class SriProcessingContext {

    private Configuraciones configuraciones;
    private EmpresaMatriz empresa;
    private String claveAcceso;
    private String xmlString;
    private byte[] xmlFirmado;
    private byte[] pdfContent;
    private String estadoAutorizacion;
    private String numeroAutorizacion;
    private OffsetDateTime fechaAutorizacion;
    private String mensajeSri;
    private String urlPdf;
    private String urlXml;
    private Object entidad;
    private boolean autorizado;
}
