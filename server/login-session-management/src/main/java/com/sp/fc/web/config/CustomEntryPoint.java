package com.sp.fc.web.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
<<<<<<< HEAD

        request.getRequestDispatcher("/login-required")
                .forward(request, response);

=======
        request.getRequestDispatcher("/login-required").forward(request,response);
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
    }
}
