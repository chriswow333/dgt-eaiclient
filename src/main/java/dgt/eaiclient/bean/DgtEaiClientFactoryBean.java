package dgt.eaiclient.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import dgt.eaiclient.exception.DgtClientInitException;
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


/**
 * Dgt eai client factory bean 
 * 
 * @author KY 89142
 */
@Slf4j
public class DgtEaiClientFactoryBean extends FeignClientFactoryBean{

  private String name;

	private String url;

  private String r4jType =  R4JType.DEFAULT;

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
    log.info("[dgt-client][init]:contextId: {}", name);

    FeignDecorators decorators = getFeignDecorator(context);

    Encoder encoder = get(context, Encoder.class);
    log.info("[dgt-client][init]:feign encoder : {}", encoder.getClass().getName());

    Decoder decoder = get(context, Decoder.class);
    log.info("[dgt-client][init]:feign decoder : {}", encoder.getClass().getName());

    Feign.Builder builder = Resilience4jFeign
    .builder(decorators)
    .contract(new SpringMvcContract())
    .encoder(encoder)
    .decoder(decoder);
    
		configureFeign(context, builder);

		return builder;
  }

  @Override
	protected void configureFeign(FeignContext context, Feign.Builder builder) {

    configureClient(context, builder);
    configureInterceptors(context, builder);

  }

  private void configureClient(FeignContext context, Feign.Builder builder){
    okhttp3.OkHttpClient client = getInheritedAwareOptional(context, okhttp3.OkHttpClient.class);
    if(client == null) {
      throw new DgtClientInitException("Not found client bean");
    }
    builder.client(new OkHttpClient(client));
  }

  private void configureInterceptors(FeignContext context, Feign.Builder builder){
    
    Map<String, RequestInterceptor> requestInterceptors = getInheritedAwareInstances(context, RequestInterceptor.class);
    log.info("[dgt-client][init]:interceptors: {}", requestInterceptors.keySet());
    if (requestInterceptors != null) {
			List<RequestInterceptor> interceptors = new ArrayList<>(requestInterceptors.values());
			AnnotationAwareOrderComparator.sort(interceptors);
			builder.requestInterceptors(interceptors);
		}
  }

  private FeignDecorators getFeignDecorator(FeignContext context){
    log.info("[dgt-client][init]:r4jType : {}", r4jType);

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
