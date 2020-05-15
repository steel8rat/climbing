package com.mokhov.climbing.controllers;


import com.mokhov.climbing.config.AppConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ServiceController.PATH)
public class ServiceController {
    public final static String PATH = AppConfig.API_ROOT_PATH;

    @GetMapping
    public String healthCheck(){
        return "Ok";
    }
}
