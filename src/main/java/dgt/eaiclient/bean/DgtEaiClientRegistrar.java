package dgt.eaiclient.bean;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import dgt.eaiclient.annotation.DgtClient;
import dgt.eaiclient.annotation.EnableDgtClients;
import dgt.eaiclient.exception.DgtEaiClientException;

/**
 * Dgt eai client Register 
 * 
 * @author KY 89142
 */
public class DgtEaiClientRegistrar  implements ImportBeanDefinitionRegistrar, EnvironmentAware {

	private Environment environment;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
    registerDgtClients(metadata, registry);
	}

  public void registerDgtClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry){

    LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
		Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableDgtClients.class.getName());
		final Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get("clients");
		if (clients == null || clients.length == 0) {
      throw new DgtEaiClientException("[dgt-eaiclient][init]: No dgt clients founded");
		}

    for (Class<?> clazz : clients) {
      candidateComponents.add(new AnnotatedGenericBeanDefinition(clazz));
    }

    for (BeanDefinition candidateComponent : candidateComponents) {
			if (candidateComponent instanceof AnnotatedBeanDefinition) {

				// verify annotated class is an interface
				AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
				AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
				Assert.isTrue(annotationMetadata.isInterface(), "@DgtClient can only be specified on an interface");

				Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(DgtClient.class.getCanonicalName());

				String name = getClientName(attributes);
				registerClientConfiguration(registry, name, attributes.get("configuration"));
				registerDgtClient(registry, name, annotationMetadata, attributes);

			}
		}
  }


	private void registerClientConfiguration(BeanDefinitionRegistry registry, Object name, Object configuration) {


		
		try {

			Class feignClientSpecification = Class.forName("org.springframework.cloud.openfeign.FeignClientSpecification");
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(feignClientSpecification);
			builder.addConstructorArgValue(name);
			builder.addConstructorArgValue(configuration);
		
			String beanDefinitionName = name + "." + feignClientSpecification.getSimpleName();
			registry.registerBeanDefinition(beanDefinitionName, builder.getBeanDefinition());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	private void registerDgtClient(BeanDefinitionRegistry registry, String name, AnnotationMetadata annotationMetadata, Map<String, Object> attributes){
		
		String className = annotationMetadata.getClassName();

		Class clazz = ClassUtils.resolveClassName(className, null);

		ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory ? (ConfigurableBeanFactory) registry : null;
		
		DgtEaiClientFactoryBean factoryBean = new DgtEaiClientFactoryBean();

		factoryBean.setBeanFactory(beanFactory);

		factoryBean.setType(clazz);
		factoryBean.setName(name);

		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> {
			factoryBean.setUrl(getUrl(beanFactory, attributes));
			factoryBean.setDecode404(false);
			return factoryBean.getObject();
		});

		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		definition.setLazyInit(true);

		AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
		beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
		beanDefinition.setAttribute("feignClientsRegistrarFactoryBean", factoryBean);

		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, null);

		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

	}

  private String getClientName(Map<String, Object> client) {
		if(client != null){
			String value = (String)client.get("name");
			if(StringUtils.hasText(value)){
				return value;
			}
		}
		throw new DgtEaiClientException("[dgt-eaiclient][init]: 'name' must be provided in @" + DgtClient.class.getSimpleName());
	}

	private String getUrl(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
		String url = resolve(beanFactory, (String) attributes.get("url"));
		validateUrl(url);
		return url;
	}

	private void validateUrl(String url) {
		try {
			new URL(url);
		}catch(MalformedURLException e){
			throw new DgtEaiClientException(url + "is malformed");
		}
	}

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }


	private String resolve(ConfigurableBeanFactory beanFactory, String value) {
		if (StringUtils.hasText(value)) {
			if (beanFactory == null) {
				return this.environment.resolvePlaceholders(value);
			}
			BeanExpressionResolver resolver = beanFactory.getBeanExpressionResolver();
			String resolved = beanFactory.resolveEmbeddedValue(value);
			if (resolver == null) {
				return resolved;
			}
			Object evaluateValue = resolver.evaluate(resolved, new BeanExpressionContext(beanFactory, null));
			if (evaluateValue != null) {
				return String.valueOf(evaluateValue);
			}
			return null;
		}
		return value;
	}

}
