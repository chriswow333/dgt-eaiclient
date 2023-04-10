package dgt.eaiclient.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.ApplicationContext;

import dgt.eaiclient.annotation.EnableDgtClients;
import dgt.eaiclient.type.R4JType;
import feign.Feign;
import feign.RequestInterceptor;
import feign.Target.HardCodedTarget;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.okhttp.OkHttpClient;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DgtEaiClientFactoryBean extends FeignClientFactoryBean{

  private String name;

	private String url;

  private String r4jType =  R4JType.NORMAL;

  private BeanFactory beanFactory;

	private ApplicationContext applicationContext;

	private Class<?> type;

	@Override
	public Object getObject() {
    

    
    return getTarget();
	}

  @SuppressWarnings("unchecked")
  private <T> T getTarget(){
    FeignContext context = beanFactory != null ? beanFactory.getBean(FeignContext.class) : applicationContext.getBean(FeignContext.class);
    Feign.Builder builder = feign(context);

    Targeter targeter = get(context, Targeter.class);
		return (T) targeter.target(this, builder, context, new HardCodedTarget<>(type, getName(), getUrl()));
  }


  @Override
  protected Feign.Builder feign(FeignContext context) {

    FeignDecorators decorators = getFeignDecorator(context);

    Feign.Builder builder = Resilience4jFeign
    .builder(decorators)
    .contract(new SpringMvcContract())
    .encoder(get(context, Encoder.class))
    .decoder(get(context, Decoder.class));
    

		configureFeign(context, builder);

		return builder;
  }

  @Override
	protected void configureFeign(FeignContext context, Feign.Builder builder) {
    
    configureUsingConfiguration(context, builder);

  }

  private FeignDecorators getFeignDecorator(FeignContext context){


    FeignDecorators.Builder decoratorBuilder = FeignDecorators.builder();

    CircuitBreakerRegistry circuitBreakerRegistry = getInheritedAwareOptional(context, CircuitBreakerRegistry.class);
    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(r4jType);
    decoratorBuilder.withCircuitBreaker(circuitBreaker);


    BulkheadRegistry bulkheadRegistry = getInheritedAwareOptional(context, BulkheadRegistry.class);
    Bulkhead bulkhead = bulkheadRegistry.bulkhead(r4jType);
    decoratorBuilder.withBulkhead(bulkhead);


    
    RateLimiterRegistry rateLimiterRegistry = getInheritedAwareOptional(context, RateLimiterRegistry.class);
    RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(r4jType);
    decoratorBuilder.withRateLimiter(rateLimiter);


    return decoratorBuilder.build();

  }


  @Override
	protected void configureUsingConfiguration(FeignContext context, Feign.Builder builder) {
    
    okhttp3.OkHttpClient client = getInheritedAwareOptional(context, okhttp3.OkHttpClient.class);
    log.info("hellloooo client to set {}", client );
    builder.client(new OkHttpClient(client));

    RequestInterceptor requestInterceptor = getInheritedAwareOptional(context, RequestInterceptor.class);

    builder.requestInterceptor(requestInterceptor);

  }


  @Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

  @Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
		beanFactory = context;
	}

  @Override
  public void setType(Class<?> type) {
    super.setType(type);
		this.type = type;
	}

  @Override
  public String getUrl() {
		return url;
	}
  @Override
	public void setUrl(String url) {
    super.setUrl(url);
    this.url = url;
	}
  

  @Override
	public String getName() {
		return name;
	}


  @Override
	public void setName(String name) {
    super.setContextId(name);
		this.name = name;
	}


  public void setR4JType(String r4jType) {
    this.r4jType = r4jType;
  }  

}
