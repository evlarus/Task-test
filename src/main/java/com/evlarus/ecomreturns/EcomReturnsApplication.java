package com.evlarus.ecomreturns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@ConfigurationPropertiesScan
@SpringBootApplication
public class EcomReturnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomReturnsApplication.class, args);
        System.out.println("Приложение запущено"); // TODO: убрать, когда настрою нормальное логирование
    }
}
