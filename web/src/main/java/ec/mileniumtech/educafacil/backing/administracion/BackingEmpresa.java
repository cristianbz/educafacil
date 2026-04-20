package ec.mileniumtech.educafacil.backing.administracion;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import ec.mileniumtech.educafacil.backing.MensajesBacking;
import ec.mileniumtech.educafacil.bean.administracion.BeanEmpresa;
import ec.mileniumtech.educafacil.modelo.persistencia.entity.EmpresaMatriz;
import ec.mileniumtech.educafacil.service.AdministracionService;
import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.CriptografiaUtil;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 *@author christian  Jul 13, 2024
 *
 */
@Named("backingEmpresa")
@ViewScoped
public class BackingEmpresa implements Serializable{
	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(BackingEmpresa.class);

    @Inject
    @Getter
    private MensajesBacking mensajesBacking;

    @Inject
    @Getter
    private BeanEmpresa beanEmpresa;

    @EJB
    private AdministracionService administracionService;

    @PostConstruct
    public void init() {
        cargarEmpresa();
    }

    /**
     * Carga los datos de la empresa desde la base de datos.
     */
    public void cargarEmpresa() {
        getBeanEmpresa().setListaEmpresas(administracionService.listarEmpresas());
        if (getBeanEmpresa().getListaEmpresas() != null && !getBeanEmpresa().getListaEmpresas().isEmpty()) {
            getBeanEmpresa().setEmpresa(getBeanEmpresa().getListaEmpresas().get(0));
        } else {
            getBeanEmpresa().setEmpresa(new EmpresaMatriz());
            getBeanEmpresa().getEmpresa().setEmpmAmbiente(1); // Por defecto Pruebas
            getBeanEmpresa().getEmpresa().setEmpmTipoEmision(1); // Por defecto Normal
            getBeanEmpresa().getEmpresa().setEmpmEstado(true);
        }
    }

    /**
     * Guarda o actualiza los datos de la empresa.
     */
    public void guardarEmpresa() {
        try {
            // Cifrar la clave del certificado antes de guardar
            String claveOriginal = getBeanEmpresa().getEmpresa().getEmpmPasswordCertificado();
            if (claveOriginal != null && !claveOriginal.isEmpty()) {
                getBeanEmpresa().getEmpresa().setEmpmPasswordCertificado(CriptografiaUtil.encriptar(claveOriginal));
            }

            administracionService.guardarEmpresa(getBeanEmpresa().getEmpresa());
            
            // Restauramos la clave original en el bean para que el usuario no la vea cifrada en la UI si sigue editando
            getBeanEmpresa().getEmpresa().setEmpmPasswordCertificado(claveOriginal);
            
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), getMensajesBacking().getPropiedad("info.grabar"));
        } catch (Exception e) {
            log.error("Error al guardar empresa", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), e.getMessage());
        }
    }

    /**
     * Maneja la carga del certificado digital .p12
     * @param event
     */
    public void handleFileUploadCertificado(FileUploadEvent event) {
        try {
            getBeanEmpresa().getEmpresa().setEmpmCertificado(event.getFile().getContent());
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), "Certificado digital cargado correctamente.");
        } catch (Exception e) {
            log.error("Error al cargar certificado", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error al cargar el certificado.");
        }
    }

    /**
     * Maneja la carga del logo de la empresa.
     * @param event
     */
    public void handleFileUploadLogo(FileUploadEvent event) {
        try {
            getBeanEmpresa().getEmpresa().setEmpmLogo(event.getFile().getContent());
            Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, getMensajesBacking().getPropiedad("info"), "Logo cargado correctamente.");
        } catch (Exception e) {
            log.error("Error al cargar logo", e);
            Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, getMensajesBacking().getPropiedad("error"), "Error al cargar el logo.");
        }
    }
    
}
