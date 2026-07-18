package com.cristinabeatriz.asistencia.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Valida que el rol elegido en el formulario de login (rolSeleccionado)
 * coincida con el rol real que tiene la cuenta en la base de datos.
 * Si no coincide, cierra la sesión recién creada y redirige a login con error.
 */
@Component
public class RolLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws ServletException, IOException {

        String rolSeleccionado = request.getParameter("rolSeleccionado");
        String rolReal = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse(null);

        if (rolSeleccionado != null && rolReal != null && !rolSeleccionado.equalsIgnoreCase(rolReal)) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login?rolIncorrecto");
            return;
        }

        setDefaultTargetUrl("/dashboard");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
