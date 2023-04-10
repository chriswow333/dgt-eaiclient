package dgt.eaiclient.zconfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.ApplicationContext;

import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Target.HardCodedTarget;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.okhttp.OkHttpClient;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DgtClientFactoryBean extends FeignClientFactoryBean{
  
  private BeanFactory beanFactory;

	private ApplicationContext applicationContext;

	private Class<?> type;

	@Override
	public Object getObject() {
    
    return getTarget();
	}

  <T> T getTarget(){
    FeignContext context = beanFactory != null ? beanFactory.getBean(FeignContext.class)
    : applicationContext.getBean(FeignContext.class);
    Feign.Builder builder = feign(context);
    Targeter targeter = get(context, Targeter.class);
		return (T) targeter.target(this, builder, context, new HardCodedTarget<>(type, getName(), getUrl()));
  }


  @Override
  protected Feign.Builder feign(FeignContext context) {


    FeignLoggerFactory loggerFactory = get(context, FeignLoggerFactory.class);
		Logger logger = loggerFactory.create(type);

		// @formatter:off
		// Feign.Builder builder = get(context, Feign.Builder.class)
		// 		// required values
		// 		.logger(logger)
		// 		.encoder(get(context, Encoder.class))
		// 		.decoder(get(context, Decoder.class))
		// 		.contract(get(context, Contract.class));
		// @formatter:on

    FeignDecorators decorators = FeignDecorators.builder().build();
    Encoder encoder = get(context, Encoder.class);
    
    
    log.info(" context name.,.. {}", context.getContextNames());

    log.info("get encoder ... {} ", encoder );

    Feign.Builder builder = Resilience4jFeign
    .builder(decorators)
    .contract(new SpringMvcContract())
    .encoder(encoder)
    .decoder(get(context, Decoder.class));

		configureFeign(context, builder);




		return builder;
  }

  @Override
	protected void configureFeign(FeignContext context, Feign.Builder builder) {
    
    configureUsingConfiguration(context, builder);

  }


  @Override
	protected void configureUsingConfiguration(FeignContext context, Feign.Builder builder) {
    
    okhttp3.OkHttpClient client = getInheritedAwareOptional(context, okhttp3.OkHttpClient.class);

    builder.client(new OkHttpClient(client));

    RequestInterceptor requestInterceptor = getInheritedAwareOptional(context, RequestInterceptor.class);

    builder.requestInterceptor(requestInterceptor);

    // Encoder encoder = getInheritedAwareOptional(context, Encoder.class);

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

  public void setType(Class<?> type) {
		this.type = type;
	}


}
