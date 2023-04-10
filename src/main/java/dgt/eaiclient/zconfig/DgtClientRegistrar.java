package dgt.eaiclient.zconfig;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.cloud.openfeign.FeignClientSpecification;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import dgt.eaiclient.zannotation.DgtClient;
import dgt.eaiclient.zannotation.EnableDgtClients;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DgtClientRegistrar  implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {  

	public DgtClientRegistrar(){
		log.info("create registrar..");
	}

	private ResourceLoader resourceLoader;

	private Environment environment;
  



	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

		// registerDefaultConfiguration(metadata, registry);
		// registerFeignClients(metadata, registry);
    registerDgtClients(metadata, registry);


	}


  public void registerDgtClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry){

    LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
		Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableDgtClients.class.getName());
		final Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get("clients");

		if (clients == null || clients.length == 0) {
      throw new RuntimeException("No dgt clients founded");
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

				Map<String, Object> attributes = annotationMetadata
						.getAnnotationAttributes(DgtClient.class.getCanonicalName());

						log.info("   {} ", DgtClient.class.getCanonicalName());
						log.info(" {}  ", attributes);
				String name = getClientName(attributes);

				log.info("init name {}", name);

				registerClientConfiguration(registry, name, attributes.get("configuration"));
				registerDgtClient(registry, annotationMetadata, attributes);
				// registerFeignClient(registry, annotationMetadata, attributes);

			}
		}

  }

	private void registerDgtClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata, Map<String, Object> attributes){
		
		String className = annotationMetadata.getClassName();
		System.out.println("class name "+ className);

		Class clazz = ClassUtils.resolveClassName(className, null);

		
		ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory ? (ConfigurableBeanFactory) registry : null;
		
		DgtClientFactoryBean factoryBean = new DgtClientFactoryBean();

		factoryBean.setBeanFactory(beanFactory);

		factoryBean.setName("name");

		factoryBean.setContextId("contextId");

		factoryBean.setType(clazz);

		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> {

			factoryBean.setUrl(getUrl(beanFactory, attributes));
			String contextId = getContextId(beanFactory, attributes);
			factoryBean.setContextId(contextId);
			factoryBean.setPath(getPath(beanFactory, attributes));

			factoryBean.setDecode404(false);
			
			Object fallback = attributes.get("fallback");
			
			if (fallback != null) {
				factoryBean.setFallback(fallback instanceof Class ? (Class<?>) fallback
						: ClassUtils.resolveClassName(fallback.toString(), null));
			}

			Object fallbackFactory = attributes.get("fallbackFactory");
			
			if (fallbackFactory != null) {
				factoryBean.setFallbackFactory(fallbackFactory instanceof Class ? (Class<?>) fallbackFactory
						: ClassUtils.resolveClassName(fallbackFactory.toString(), null));
			}

			return factoryBean.getObject();

		});


		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		definition.setLazyInit(true);
		// validate(attributes);


		AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
		beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
		beanDefinition.setAttribute("feignClientsRegistrarFactoryBean", factoryBean);

		// has a default, won't be null
		// boolean primary = (Boolean) attributes.get("primary");

		// beanDefinition.setPrimary(primary);

		String[] qualifiers = {};
		// getQualifiers(attributes);
		// if (ObjectUtils.isEmpty(qualifiers)) {
		// 	qualifiers = new String[] { contextId + "FeignClient" };
		// }

		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, qualifiers);

		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

	}



  private void registerClientConfiguration(BeanDefinitionRegistry registry, Object name, Object configuration) {


		log.info("hiiiiii config  {}", configuration);
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FeignClientSpecification.class);

		builder.addConstructorArgValue(name);

		builder.addConstructorArgValue(configuration);
		log.info("registerClientConfiguration {}", name + "." + FeignClientSpecification.class.getSimpleName());
		registry.registerBeanDefinition(name + "." + FeignClientSpecification.class.getSimpleName(), builder.getBeanDefinition());

	}


	private String getContextId(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
		String contextId = (String) attributes.get("contextId");
		if (!StringUtils.hasText(contextId)) {
			return getName(attributes);
		}

		contextId = resolve(beanFactory, contextId);
		return getName(contextId);
	}

		String getName(Map<String, Object> attributes) {
			return getName(null, attributes);
		}

		String getName(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
			String name = (String) attributes.get("serviceId");
			if (!StringUtils.hasText(name)) {
				name = (String) attributes.get("name");
			}
			if (!StringUtils.hasText(name)) {
				name = (String) attributes.get("value");
			}
			name = resolve(beanFactory, name);
			return getName(name);
		}

	static String getName(String name) {
		if (!StringUtils.hasText(name)) {
			return "";
		}

		String host = null;
		try {
			String url;
			if (!name.startsWith("http://") && !name.startsWith("https://")) {
				url = "http://" + name;
			}
			else {
				url = name;
			}
			host = new URI(url).getHost();

		}
		catch (URISyntaxException e) {
		}
		Assert.state(host != null, "Service id not legal hostname (" + name + ")");
		return name;
	}


  private String getClientName(Map<String, Object> client) {
		if (client == null) {
			return null;
		}
		String value = (String) client.get("contextId");
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("value");
		}
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("name");
		}
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("serviceId");
		}
		if (StringUtils.hasText(value)) {
			return value;
		}

		throw new IllegalStateException(
				"Either 'name' or 'value' must be provided in @" + DgtClient.class.getSimpleName());
	}



	private String getUrl(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
		String url = resolve(beanFactory, (String) attributes.get("url"));
		return getUrl(url);
	}

	private String getPath(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
		String path = resolve(beanFactory, (String) attributes.get("path"));
		return getPath(path);
	}

	static String getPath(String path) {
		if (StringUtils.hasText(path)) {
			path = path.trim();
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
		}
		return path;
	}

	static String getUrl(String url) {
		if (StringUtils.hasText(url) && !(url.startsWith("#{") && url.contains("}"))) {
			if (!url.contains("://")) {
				url = "http://" + url;
			}
			try {
				new URL(url);
			}
			catch (MalformedURLException e) {
				throw new IllegalArgumentException(url + " is malformed", e);
			}
		}
		return url;
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

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }
}