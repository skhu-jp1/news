package se.sowl.jigeumapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = {"se.sowl.jigeumdomain"})
@EnableCaching
@EnableScheduling
public class JigeumApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(JigeumApiApplication.class, args);
    }

}
