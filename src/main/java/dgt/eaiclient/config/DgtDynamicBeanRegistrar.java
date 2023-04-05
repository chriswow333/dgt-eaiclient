package dgt.eaiclient.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

public class DgtDynamicBeanRegistrar implements BeanDefinitionRegistryPostProcessor, Ordered {


  public DgtDynamicBeanRegistrar(Environment environment) {
    // beanNames =
    //     Binder.get(environment)
    //         .bind(PROPERTIES_PREFIX, Bindable.listOf(String.class))
    //         .orElseThrow(IllegalStateException::new);
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int getOrder() {
    // TODO Auto-generated method stub
    return 2;
  }
  
}
