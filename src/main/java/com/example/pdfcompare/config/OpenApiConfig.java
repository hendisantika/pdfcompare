package com.example.pdfcompare.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Sercan Noyan Germiyanoğlu",
                        url = "https://github.com/Rapter1990/pdfcompare"
                ),
                description = "Case Study - Pdf Compare" +
                        " (Java 21, Spring Boot, JUnit, Jacoco, Sonarqube, Docker, Kubernetes, Prometheus, Grafana, Github Actions (CI/CD), Alert Manager) ",
                title = "pdfcompare",
                version = "1.0.0"
        )
)
public class OpenApiConfig {

}
