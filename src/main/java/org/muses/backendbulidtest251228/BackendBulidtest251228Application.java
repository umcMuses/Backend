package org.muses.backendbulidtest251228;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BackendBulidtest251228Application {

    public static void main(String[] args) {
        SpringApplication.run(BackendBulidtest251228Application.class, args);
    }

}
