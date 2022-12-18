package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
public class Controller {

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

}
