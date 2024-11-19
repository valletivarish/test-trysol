package com.monocept.myapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                              .directory("./") 
                              .ignoreIfMalformed()
                              .ignoreIfMissing()
                              .load();

        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );


        SpringApplication.run(Application.class, args);
    }
}
