package com.sp.fc.web.config;

import com.sp.fc.web.controller.YouCannotAccessUserPage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomDeniedHandler implements AccessDeniedHandler {
<<<<<<< HEAD

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException
    {
        if(accessDeniedException instanceof YouCannotAccessUserPage){
            request.getRequestDispatcher("/access-denied").forward(request, response);
        }else{
            request.getRequestDispatcher("/access-denied2").forward(request, response);
=======
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        if(accessDeniedException instanceof YouCannotAccessUserPage){
            request.getRequestDispatcher("/access-denied").forward(request,response);
        }
        else {
            request.getRequestDispatcher("/access-denied2").forward(request,response);
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
        }
    }
}
