package com.kevinolarte.resibenissa.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/default")
@RestController
@AllArgsConstructor
public class DefaultController {

    @GetMapping("/v1")
    public String v1() {
        return "Hello World";
    }


}
