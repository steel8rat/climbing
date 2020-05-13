package com.mokhov.climbing.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {

    @GetMapping
    public String healthCheck(){
        return "Ok";
    }
}
