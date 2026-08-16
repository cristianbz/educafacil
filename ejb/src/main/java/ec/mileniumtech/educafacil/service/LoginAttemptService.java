package ec.mileniumtech.educafacil.service;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio Singleton para protección contra fuerza bruta en el login.
 * Registra intentos fallidos por usuario y bloquea la cuenta temporalmente
 * cuando se supera el número máximo de intentos permitidos.
 *
 * Configuración:
 *   - MAX_INTENTOS              : 5  intentos fallidos antes del bloqueo
 *   - TIEMPO_BLOQUEO_MINUTOS   : 15 minutos de bloqueo temporal
 *
 * @author EducaFácil Security Module
 */
@Singleton
@Startup
@LocalBean
public class LoginAttemptService {

    private static final Logger log = LogManager.getLogger(LoginAttemptService.class);

    /** Número máximo de intentos fallidos antes de bloquear la cuenta. */
    private static final int MAX_INTENTOS = 5;

    /** Duración del bloqueo temporal en minutos. */
    private static final long TIEMPO_BLOQUEO_MINUTOS = 15;

    /**
     * Mapa concurrente que guarda el estado de intentos por usuario.
     * Clave: documento de identidad del usuario (en minúsculas para normalización).
     */
    private final ConcurrentHashMap<String, AttemptInfo> intentosPorUsuario = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    /**
     * Registra un intento de autenticación fallido para el usuario dado.
     * Si se alcanza el límite, activa el bloqueo temporal.
     *
     * @param username documento de identidad del usuario
     */
    public void registrarIntentoFallido(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        String key = normalizar(username);
        AttemptInfo info = intentosPorUsuario.computeIfAbsent(key, k -> new AttemptInfo());

        synchronized (info) {
            // Si ya estaba bloqueado y el tiempo expiró, reiniciar contador
            if (info.bloqueadoHasta != null && Instant.now().isAfter(info.bloqueadoHasta)) {
                info.reiniciar();
            }

            info.intentosFallidos++;
            info.ultimoIntento = Instant.now();

            if (info.intentosFallidos >= MAX_INTENTOS) {
                info.bloqueadoHasta = Instant.now().plusSeconds(TIEMPO_BLOQUEO_MINUTOS * 60);
                log.warn("Cuenta bloqueada por fuerza bruta: usuario='{}', bloqueado hasta={}",
                        key, info.bloqueadoHasta);
            } else {
                log.warn("Intento fallido de login: usuario='{}', intentos={}/{}",
                        key, info.intentosFallidos, MAX_INTENTOS);
            }
        }
    }

    /**
     * Indica si la cuenta del usuario está actualmente bloqueada.
     *
     * @param username documento de identidad del usuario
     * @return {@code true} si la cuenta está bloqueada; {@code false} en caso contrario
     */
    public boolean estaBloqueado(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        AttemptInfo info = intentosPorUsuario.get(normalizar(username));
        if (info == null) {
            return false;
        }
        synchronized (info) {
            if (info.bloqueadoHasta == null) {
                return false;
            }
            if (Instant.now().isAfter(info.bloqueadoHasta)) {
                // Bloqueo expirado, limpiar
                info.reiniciar();
                return false;
            }
            return true;
        }
    }

    /**
     * Devuelve los minutos restantes del bloqueo activo.
     * Si la cuenta no está bloqueada retorna 0.
     *
     * @param username documento de identidad del usuario
     * @return minutos restantes (redondeados hacia arriba)
     */
    public long getMinutosRestantesBloqueo(String username) {
        if (username == null || username.isBlank()) {
            return 0;
        }
        AttemptInfo info = intentosPorUsuario.get(normalizar(username));
        if (info == null || info.bloqueadoHasta == null) {
            return 0;
        }
        synchronized (info) {
            if (info.bloqueadoHasta == null) {
                return 0;
            }
            long segundosRestantes = info.bloqueadoHasta.getEpochSecond() - Instant.now().getEpochSecond();
            if (segundosRestantes <= 0) {
                return 0;
            }
            // Redondear hacia arriba para no mostrar "0 minutos" cuando quedan segundos
            return (segundosRestantes / 60) + 1;
        }
    }

    /**
     * Retorna cuántos intentos le quedan al usuario antes de ser bloqueado.
     *
     * @param username documento de identidad del usuario
     * @return número de intentos restantes
     */
    public int getIntentosRestantes(String username) {
        if (username == null || username.isBlank()) {
            return MAX_INTENTOS;
        }
        AttemptInfo info = intentosPorUsuario.get(normalizar(username));
        if (info == null) {
            return MAX_INTENTOS;
        }
        synchronized (info) {
            int restantes = MAX_INTENTOS - info.intentosFallidos;
            return Math.max(0, restantes);
        }
    }

    /**
     * Limpia el contador de intentos fallidos de un usuario tras una autenticación exitosa.
     *
     * @param username documento de identidad del usuario
     */
    public void limpiarIntentos(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        intentosPorUsuario.remove(normalizar(username));
        log.debug("Intentos de login limpiados para usuario='{}'", normalizar(username));
    }

    /**
     * Retorna el número máximo de intentos permitidos (para uso en mensajes dinámicos).
     *
     * @return constante MAX_INTENTOS
     */
    public int getMaxIntentos() {
        return MAX_INTENTOS;
    }

    // -------------------------------------------------------------------------
    // Limpieza automática (cada hora) para evitar fugas de memoria
    // -------------------------------------------------------------------------

    /**
     * Tarea programada que elimina del mapa las entradas obsoletas
     * (bloqueos ya expirados o intentos sin actividad reciente).
     * Se ejecuta una vez por hora.
     */
    @Schedule(hour = "*", minute = "0", second = "0", persistent = false)
    public void limpiarEntradasObsoletas() {
        int eliminados = 0;
        Instant limite = Instant.now().minusSeconds(TIEMPO_BLOQUEO_MINUTOS * 60 * 2);

        Iterator<Map.Entry<String, AttemptInfo>> it = intentosPorUsuario.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, AttemptInfo> entry = it.next();
            AttemptInfo info = entry.getValue();
            synchronized (info) {
                boolean bloqueoCaducado = info.bloqueadoHasta != null
                        && Instant.now().isAfter(info.bloqueadoHasta);
                boolean inactivoMucho = info.ultimoIntento != null
                        && info.ultimoIntento.isBefore(limite);

                if (bloqueoCaducado || inactivoMucho) {
                    it.remove();
                    eliminados++;
                }
            }
        }

        if (eliminados > 0) {
            log.info("LoginAttemptService: se eliminaron {} entradas obsoletas del mapa de intentos.", eliminados);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String normalizar(String username) {
        return username.trim().toLowerCase();
    }

    // -------------------------------------------------------------------------
    // Inner class: estado de intentos por usuario
    // -------------------------------------------------------------------------

    /**
     * Clase interna que encapsula el estado de intentos de un usuario.
     */
    private static class AttemptInfo {

        /** Número de intentos fallidos consecutivos. */
        int intentosFallidos = 0;

        /** Momento del último intento fallido. */
        Instant ultimoIntento;

        /** Instante hasta el cual la cuenta está bloqueada (null = no bloqueada). */
        Instant bloqueadoHasta;

        /** Reinicia el estado al de una cuenta sin intentos fallidos. */
        void reiniciar() {
            intentosFallidos = 0;
            ultimoIntento = null;
            bloqueadoHasta = null;
        }
    }
}
