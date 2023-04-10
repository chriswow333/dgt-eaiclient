package dgt.eaiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import dgt.eaiclient.zannotation.EnableDgtClients;
import dgt.eaiclient.zdemo.DgtEaiClientDemo;

@SpringBootApplication
// @ComponentScan(excludeFilters={@ComponentScan.Filter(type = FilterType.REGEX, pattern = "dgt.eaiclient.zconfig.DgtClientConfiguration")})
@EnableDgtClients(clients={DgtEaiClientDemo.class})
public class Application {
  public static void main(String[] args){
    SpringApplication.run(Application.class, args);
  }
}