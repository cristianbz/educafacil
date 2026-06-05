package ec.mileniumtech.educafacil.web.sesion;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class SessionTimeoutFilter
 */
@WebFilter(description = "Gestiona el estado de la sesion", urlPatterns = { "/*" })
public class SessionTimeoutFilter extends HttpFilter {
        
	private static final long serialVersionUID = 1L;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        
        if (debeIgnorar(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = req.getSession(false);
        boolean sesionValida = session != null && session.getAttribute("logeado") != null && (boolean) session.getAttribute("logeado");
        
        if (!sesionValida) {
            String timeoutUrl = req.getContextPath() + "/finsesion.xhtml";
            
            // DETECTAR SI ES UNA PETICIÓN AJAX
            boolean isAjax = "partial/ajax".equals(req.getHeader("Faces-Request")) 
                          || "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
            
            if (isAjax) {
                // Generar la respuesta XML que JSF/PrimeFaces entienden para redireccionar
                res.setContentType("text/xml");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().printf(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<partial-response><redirect url=\"%s\"/></partial-response>", 
                    timeoutUrl
                );
            } else {
                // Redirección normal para peticiones síncronas (F5, links normales)
                res.sendRedirect(timeoutUrl);
            }
            return;
        }
		chain.doFilter(request, response);
	}

	private boolean debeIgnorar(String path) {
	    return path.isEmpty()
	        || path.equals("/")
	        || path.equals("/login.cap")
	        || path.equals("/login.xhtml")
	        || path.equals("/finsesion.cap")
	        || path.equals("/finsesion.xhtml")
	        || path.equals("/loginFacebook.xhtml")
	        || path.startsWith("/jakarta.faces.resource/")
	        || path.startsWith("/resources/")
	        || path.startsWith("/paginas/cambioClave.xhtml")
	        || path.startsWith("/rest/auth/")
	        || path.startsWith("/rest/openapi")
	        || path.matches(".*\\.(css|js|png|jpg|jpeg|gif|ico|svg|woff2?|ttf|eot)$");
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
