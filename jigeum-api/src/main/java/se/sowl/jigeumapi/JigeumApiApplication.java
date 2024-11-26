package se.sowl.jigeumapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EntityScan(basePackages = {"se.sowl.jigeumdomain"})
@EnableCaching
public class JigeumApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JigeumApiApplication.class, args);
    }

}
