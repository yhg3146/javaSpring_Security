package com.sp.fc.web.controller;

import org.springframework.security.access.AccessDeniedException;
<<<<<<< HEAD
import org.springframework.security.web.access.AccessDeniedHandler;


public class YouCannotAccessUserPage extends AccessDeniedException {

    public YouCannotAccessUserPage() {
        super("유저페이지 접근 거부");
    }


=======

public class YouCannotAccessUserPage extends AccessDeniedException {
    public YouCannotAccessUserPage() {
        super("유저페이지 접근 거부");
    }
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
}
