package dgt.eaiclient.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dgt.eaiclient.annotation.EnableDgtClients;
import dgt.eaiclient.client.InfoCloudDefaultClient;
import dgt.eaiclient.client.MidCloudDefaultClient;
import dgt.eaiclient.decoder.DgtEaiClientDecoder;
import dgt.eaiclient.encoder.DgtEaiClientEncoder;
import dgt.eaiclient.handler.R4JConfigHandler;
import dgt.eaiclient.props.DgtEaiClientProperty;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;


@Configuration
@EnableDgtClients(clients = {
  InfoCloudDefaultClient.class,
  MidCloudDefaultClient.class,
})
@EnableConfigurationProperties(value={DgtEaiClientProperty.class})
public class DgtEaiClientAutoConfiguration {

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry(DgtEaiClientProperty dgtEaiClientProperty){
    CircuitBreakerRegistry.Builder circuitBreakerRegistryBuilder = CircuitBreakerRegistry.custom();
    R4JConfigHandler.circuitBreaker(circuitBreakerRegistryBuilder, dgtEaiClientProperty);
    return circuitBreakerRegistryBuilder.build();
  }

  @Bean
  public BulkheadRegistry bulkheadRegistry(DgtEaiClientProperty dgtEaiClientProperty){
    BulkheadRegistry.Builder bulkheadRegisryBuilder = BulkheadRegistry.custom();
    R4JConfigHandler.bulkhead(bulkheadRegisryBuilder, dgtEaiClientProperty);
    return bulkheadRegisryBuilder.build();
  }

  @Bean
  public RateLimiterRegistry buildRateLimiter(DgtEaiClientProperty dgtEaiClientProperty){
    RateLimiterRegistry.Builder rateLimiterRegistryBuilder = RateLimiterRegistry.custom();
    R4JConfigHandler.rateLimiter(rateLimiterRegistryBuilder, dgtEaiClientProperty);
    return rateLimiterRegistryBuilder.build();
  }

  @Bean
  public Encoder encoder(){
    return new DgtEaiClientEncoder();
  }

  @Bean
  public Decoder decoder(){
    return new DgtEaiClientDecoder();
  }

}
