package com.yas.storefront.authentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthenticationController {
    @GetMapping(path = "/logout")
    public RedirectView logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return new RedirectView("/");
    }

    @GetMapping(path = "/login")
    public RedirectView login() {
        return new RedirectView("/");
    }
}
