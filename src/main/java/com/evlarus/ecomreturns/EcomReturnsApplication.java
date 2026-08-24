package com.evlarus.ecomreturns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcomReturnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomReturnsApplication.class, args);
        System.out.println("Приложение запущено"); // TODO: убрать, когда настрою нормальное логирование
    }
}
