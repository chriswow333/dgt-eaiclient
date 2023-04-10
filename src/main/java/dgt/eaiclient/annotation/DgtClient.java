package dgt.eaiclient.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DgtClient {

	String name() default "";

	String url() default "";

	Class<?>[] clientConfiguration() default {};

	String r4jType() default "";

}
