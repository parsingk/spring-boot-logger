package com.spring.boot.logger.annotations;

import com.spring.boot.logger.config.KinesisConfiguration;
import com.spring.boot.logger.config.LoggerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = { LoggerConfiguration.class, KinesisConfiguration.class })
public @interface EnableLogger {
}
