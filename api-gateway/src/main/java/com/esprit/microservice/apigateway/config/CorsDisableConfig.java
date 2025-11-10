package com.esprit.microservice.apigateway.config;

//import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
//import org.springframework.context.ApplicationContextInitializer;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.ImportRuntimeHints;
//import org.springframework.context.support.GenericApplicationContext;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
//
//@Configuration
//public class CorsDisableConfig
//        implements ApplicationContextInitializer<ReactiveWebServerApplicationContext> {
//
//    @Override
//    public void initialize(ReactiveWebServerApplicationContext context) {
//        // Remove Spring Cloud Gateway’s default CorsWebFilter if it exists
//        if (context.containsBeanDefinition("corsWebFilter")) {
//            context.removeBeanDefinition("corsWebFilter");
//            System.out.println("🚫 Default CorsWebFilter removed — Symfony will handle CORS.");
//        }
//    }
//}
