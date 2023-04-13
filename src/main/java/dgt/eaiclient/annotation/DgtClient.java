package dgt.eaiclient.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

import dgt.eaiclient.type.R4JType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DgtClient {

	String name();

	String url();

	Class<?>[] configuration() default {};

	String r4jType() default R4JType.DEFAULT;

}
