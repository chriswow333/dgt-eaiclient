package dgt.eaiclient.zannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import dgt.eaiclient.zconfig.DgtClientRegistrar;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DgtClientRegistrar.class)
public @interface EnableDgtClients {


  /**
	 * List of classes annotated with @FeignClient. If not empty, disables classpath
	 * scanning.
	 * @return list of FeignClient classes
	 */
	Class<?>[] clients() default {};
}
