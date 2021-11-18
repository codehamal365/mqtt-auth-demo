package com.example.config;

import com.example.constant.AuthConstants;
import com.example.enums.ClientType;
import com.example.exception.ConfigMapException;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.*;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * configuration map for topics and scopes
 *
 * @author xie.wei
 * @date created at 2021-11-16 18:07
 */
@Validated
@ConfigurationProperties(prefix = "config-map")
@Component
@Data
public class ConfigMap implements InitializingBean {

    private List<String> scopes;
    private List<@Valid TopicProperties> topics;

    @Data
    public static class TopicProperties {
        @NotBlank(message = "topic must not be blank")
        private String topic;
        private List<@ValueCheck(type = "client", message = "client value is not valid") String> client;
        private List<@ValueCheck(type = "actions", message = "action value is not valid") String> actions;
        private List<String> permissions;
    }

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Constraint(validatedBy = TopicValidator.class)
    public @interface ValueCheck {

        boolean required() default true;

        String type() default "";

        String message() default "topic config value is not valid";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    public static class TopicValidator implements ConstraintValidator<ValueCheck, String> {
        private boolean required = false;
        private String type = "";

        @Override
        public void initialize(ValueCheck constraintAnnotation) {
            required = constraintAnnotation.required();
            type = constraintAnnotation.type();
        }

        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (required) {
                switch (type) {
                    case "actions":
                        return AuthConstants.ACTION_LIST.contains(value);
                    case "client":
                        return ClientType.getClientTypeList().contains(value);
                    default:
                        return true;
                }
            }
            return true;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<String> collect = topics.stream()
                .map(TopicProperties::getTopic).collect(Collectors.toSet());
        // topics should differ each other
        if (collect.size() != topics.size()) {
            throw new ConfigMapException("topics configuration have same topic");
        }
    }
}
