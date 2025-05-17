package org.example.projectdevtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.projectdevtool.repo")
//@EnableR2dbcRepositories(basePackages = "org.example.projectdevtool.repo")
public class ProjectDevToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectDevToolApplication.class, args);
    }

}
