package ec.mileniumtech.educafacil.web.security;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

/**
* @author christian Aug 13, 2026
*/
@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        httpResponse.setHeader(
            "X-Frame-Options",
            "DENY"
        );

        httpResponse.setHeader(
            "X-Content-Type-Options",
            "nosniff"
        );

        httpResponse.setHeader(
            "Referrer-Policy",
            "strict-origin-when-cross-origin"
        );

        httpResponse.setHeader(
            "Permissions-Policy",
            "camera=(), microphone=(), geolocation=(), payment=(), usb=()"
        );

        httpResponse.setHeader(
            "Content-Security-Policy-Report-Only",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
            "img-src 'self' data: blob:; " +
            "font-src 'self' data: https://fonts.gstatic.com; " +
            "connect-src 'self'; " +
            "frame-ancestors 'none'; " +
            "base-uri 'self'; " +
            "form-action 'self'"
        );
        httpResponse.setHeader("Cross-Origin-Embedder-Policy", "unsafe-none");

        chain.doFilter(request, response);
    }
}

