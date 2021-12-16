package com.example.config;

import com.example.constant.AuthConstants;
import com.example.enums.ClientType;
import com.example.exception.ConfigMapException;
import com.google.common.base.Splitter;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.constant.AuthConstants.*;

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
@org.springframework.context.annotation.PropertySource(value = "${config-map-file-path}",
        factory = ConfigMap.YamlPropertySourceFactory.class)
public class ConfigMap implements InitializingBean {

    private List<String> scopes;
    private List<@Valid TopicProperties> topics;

    @Data
    public static class TopicProperties {
        @NotBlank(message = "topic must not be blank")
        private String topic;
        @NotEmpty(message = "client should be declared")
        private List<@ValueCheck(type = "client", message = "client value is not valid") String> client;
        @NotEmpty(message = "actions should be declared")
        private List<@ValueCheck(type = "actions", message = "action value is not valid") String> actions;
        private List<String> permissions = new ArrayList<>();
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
        String exp = "topic %s is invalid %s";
        // check topic rules of # and +
        collect.forEach(topic -> {
            // check empty path //
            if (topic.contains("//")) {
                throw new ConfigMapException(String.format(exp, topic, ", it has empty path value"));
            }
            /*
             *   check #
             *   if path contains #, it can only be placed in the path tail
             */
            var index = topic.indexOf(POUND_KEY);
            if (index >= 0 && index < topic.length() - 1) {
                throw new ConfigMapException(String.format(exp, topic,
                        ", '#' can only be used in the path tail"));
            }
            // check +
            Splitter.on(SLASH).splitToList(topic)
                    .forEach(item -> {
                        /*
                         *  the following path are all forbidden in mqtt
                         *  /++/test
                         *  /+test/test
                         *  /test+/test
                         */
                        var indexOfPlus = item.indexOf(PLUS);
                        if (indexOfPlus >= 0 && item.length() > 1) {
                            throw new ConfigMapException(String.format(exp, topic,
                                    ", '+' can only be used itself in path"));
                        }
                    });
        });
    }

    static class YamlPropertySourceFactory implements PropertySourceFactory {

        @Override
        public PropertySource<?> createPropertySource( String name, EncodedResource encodedResource)
                throws IOException {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(encodedResource.getResource());

            Properties properties = factory.getObject();

            return new PropertiesPropertySource(encodedResource.getResource().getFilename(), properties);
        }
    }


}
