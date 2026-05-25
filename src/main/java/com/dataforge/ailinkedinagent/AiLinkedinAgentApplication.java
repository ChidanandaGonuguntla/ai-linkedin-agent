package com.dataforge.ailinkedinagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AiLinkedinAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiLinkedinAgentApplication.class, args);
    }
}
