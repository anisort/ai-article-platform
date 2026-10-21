package com.auth.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Defines login controller.
 */
@Controller
public class LoginController {
    @GetMapping("/login")
/**
 * Returns login.
 * @return the result of the operation
 */
    public String login() {
        return "login";
    }

    @GetMapping("/")
/**
 * Returns root.
 * @return the result of the operation
 */
    public String root() {
        return "redirect:http://localhost:81/";
    }
}
