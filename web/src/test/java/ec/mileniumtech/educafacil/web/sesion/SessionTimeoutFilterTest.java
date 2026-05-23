package ec.mileniumtech.educafacil.web.sesion;

import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionTimeoutFilterTest {

    private final SessionTimeoutFilter filter = new SessionTimeoutFilter();

    @Test
    void redirigeCuandoNoExisteSesion() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/educafacil-web");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/educafacil-web/finsesion.xhtml");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void continuaCadenaCuandoExisteSesion() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(false)).thenReturn(session);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }
}
