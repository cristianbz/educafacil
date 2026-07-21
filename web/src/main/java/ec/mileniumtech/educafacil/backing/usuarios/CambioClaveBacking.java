/**
 * Este software esta protegido por derechos de autor CEIMSCAP
 */
package ec.mileniumtech.educafacil.backing.usuarios;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ec.mileniumtech.educafacil.modelo.persistencia.entity.Usuario;
import ec.mileniumtech.educafacil.service.facade.MatriculaFacade;
import org.primefaces.PrimeFaces;

import ec.mileniumtech.educafacil.utilitario.Mensaje;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.CifradorBase;
import ec.mileniumtech.educafacil.utilitarios.encriptacion.Encriptar;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean para el cambio de contraseña desde el enlace enviado por correo.
 * Procesa los parámetros tk (token cifrado) y prm (documento de identidad cifrado)
 * de la URL para validar la solicitud y permitir el cambio de clave.
 * 
 * @author christian
 */
@Named("cambioClaveBacking")
@ViewScoped
public class CambioClaveBacking implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(CambioClaveBacking.class);

	@EJB
	private MatriculaFacade matriculaDataService;

	@Getter
	@Setter
	private String usuario;

	@Getter
	@Setter
	private String password;

	@Getter
	@Setter
	private String password2;

	@Getter
	@Setter
	private boolean usuarioValido;

	@Getter
	@Setter
	private boolean cambioExitoso;

	@Getter
	private List<String> items;

	@Getter
	@Setter
	private int numero;

	private Usuario usuarioEntity;

	@PostConstruct
	public void init() {
		items = Arrays.asList(
			"Mínimo 8 caracteres",
			"Al menos una letra mayúscula",
			"Al menos una letra minúscula",
			"Al menos un número",
			"Al menos un carácter especial (@, #, $, etc.)"
		);
		numero = 5;
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
			String tk = request.getParameter("tk");
			String prm = request.getParameter("prm");

			if (tk == null || prm == null || tk.isEmpty() || prm.isEmpty()) {
				log.warn("Parámetros insuficientes para cambio de clave");
				return;
			}

			// Decodificar documento de identidad (base64)
			String documentoIdentidad = CifradorBase.descifrarBase64(prm);
			if (documentoIdentidad == null || documentoIdentidad.isEmpty()) {
				log.warn("No se pudo decodificar el documento de identidad");
				return;
			}

			// Buscar usuario por documento
			usuarioEntity = matriculaDataService.consultarUsuarioPorDocumento(documentoIdentidad);
			if (usuarioEntity == null) {
				log.warn("Usuario no encontrado para documento: {}", documentoIdentidad);
				return;
			}

			// Validar token
			Integer tokenAlmacenado = usuarioEntity.getUsuaToken();
			if (tokenAlmacenado == null) {
				log.warn("El usuario no tiene un token de cambio de clave vigente");
				return;
			}

			// Decodificar token de la URL: base64 → sha256 → comparar con token almacenado
			String tkDescifrado = CifradorBase.descifrarBase64(tk);
			String tkHashEsperado = Encriptar.encriptarSHA256(String.valueOf(tokenAlmacenado));

			if (!tkHashEsperado.equals(tkDescifrado)) {
				log.warn("Token inválido para cambio de clave");
				return;
			}

			// Token válido
			usuarioValido = true;
			usuario = usuarioEntity.getUsuaUsuario();

		} catch (Exception e) {
			log.error("Error al validar token de cambio de clave", e);
		}
	}

	/**
	 * @return true si el usuario no es válido (token expirado o inválido)
	 */
	public boolean isUsuarioInvalido() {
		return !usuarioValido && !cambioExitoso;
	}

	/**
	 * Cambia la contraseña del usuario.
	 */
	public void cambiarClaveUsuario() {
		try {
			if (usuarioEntity == null) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Sesión de cambio de clave inválida.");
				return;
			}

			// Validar que las contraseñas coincidan
			if (password == null || password2 == null || !password.equals(password2)) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
				return;
			}

			// Validar requisitos de complejidad
			StringBuilder errores = new StringBuilder();
			if (password.length() < 8) {
				errores.append("• Mínimo 8 caracteres\n");
			}
			if (!password.matches(".*[A-Z].*")) {
				errores.append("• Al menos una letra mayúscula\n");
			}
			if (!password.matches(".*[a-z].*")) {
				errores.append("• Al menos una letra minúscula\n");
			}
			if (!password.matches(".*\\d.*")) {
				errores.append("• Al menos un número\n");
			}
			if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
				errores.append("• Al menos un carácter especial (@, #, $, etc.)\n");
			}
			if (errores.length() > 0) {
				Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Requisitos de contraseña",
					"La contraseña debe cumplir:\n" + errores.toString().trim());
				return;
			}

			// Encriptar nueva contraseña con BCrypt
			String hash = Encriptar.encriptarBCrypt(password);
			usuarioEntity.setUsuaClave(hash);

			// Limpiar token (ya no es válido)
			usuarioEntity.setUsuaToken(null);

			// Guardar
			matriculaDataService.actualizaUsuario(usuarioEntity);

			cambioExitoso = true;
			usuarioValido = false;

			Mensaje.verMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Contraseña cambiada correctamente.");
			PrimeFaces.current().executeScript("PF('poll').start();");

		} catch (Exception e) {
			log.error("Error al cambiar la clave", e);
			Mensaje.verMensaje(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al cambiar la contraseña.");
		}
	}

	/**
	 * Redirecciona al login después del cambio exitoso (llamado por el poll cada 1 segundo).
	 * Decrementa el contador y redirige cuando llega a 0.
	 */
	public void redireccionar() {
		if (cambioExitoso) {
			numero--;
			if (numero <= 0) {
				try {
					FacesContext.getCurrentInstance().getExternalContext()
						.redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/login.xhtml");
				} catch (Exception e) {
					log.error("Error al redireccionar al login", e);
				}
			}
		}
	}
}
