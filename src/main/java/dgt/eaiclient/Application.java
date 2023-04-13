package dgt.eaiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@SpringBootApplication
// @FeignClient
// @EnableFeignClients
public class Application {
  public static void main(String[] args){
    SpringApplication.run(Application.class, args);
  }
}