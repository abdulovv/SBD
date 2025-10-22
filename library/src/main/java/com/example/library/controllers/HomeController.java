package com.example.library.controllers;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Data
@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("home")
    public String method(){
        System.out.println("Method Called");
        return "home";
    }
}
