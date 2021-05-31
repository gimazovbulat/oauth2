package ru.itis.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class WebController {

    @RequestMapping("/secured")
    public String securedPage(Principal principal) {
        return "hi " + principal.getName();
    }

    @RequestMapping("/")
    public String index() {
        return "home!";
    }
}
