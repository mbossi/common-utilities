package com.ia.common.utilities.configuration.validator;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfiguration {

    @Bean
    public Validator validator() {
        try (final var factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }
}
