/**
Este software es propiedad de CEIMSCAP Cia.Ltda, el mismo que esta protegido por derechos de autor
*/

package ec.mileniumtech.educafacil.utilitario;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author [ Christian Baez ] cbaez Jan 13, 2020
 *
 */
public class ControlCachePhaseListener implements PhaseListener {


	private static final long serialVersionUID = 1L;

	public ControlCachePhaseListener() {
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
    @Override
    public void afterPhase(PhaseEvent arg0) {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    @Override
    public void beforePhase(PhaseEvent arg0) {
        // TODO Auto-generated method stub
        FacesContext facesContext = arg0.getFacesContext();
        HttpServletResponse response = (HttpServletResponse) facesContext
                .getExternalContext().getResponse();
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        // Stronger according to blog comment below that references HTTP spec
        response.addHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "must-revalidate");
        // some date in the past
        response.addHeader("Expires", "Mon, 8 Aug 2006 10:00:00 GMT");
    }
    /* (non-Javadoc)
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
    @Override
    public PhaseId getPhaseId() {

        return PhaseId.RENDER_RESPONSE;
    }

}
