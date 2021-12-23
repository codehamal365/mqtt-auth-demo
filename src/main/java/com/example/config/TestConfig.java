package com.example.config;


import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;

import java.io.File;
import java.util.Properties;

/**
 * test bean init method
 *
 * @author xie.wei
 * @date created at 2021-12-14 10:26
 */
@Configuration
public class TestConfig implements CommandLineRunner {

    @Value("${config-map-class-path}")
    String path;

    @Autowired
    ApplicationContext context;

    @Autowired
    ConfigurableEnvironment environment;

    @Override
    public void run(String... args) throws Exception {

        final DefaultResourceLoader loader = new DefaultResourceLoader();
        final Resource resource = loader.getResource(path);
        final String path1 = resource.getURI().getPath();
        final int indexOf = path1.lastIndexOf("/");
        String dir = path1.substring(0, indexOf);
        String filename = path1.substring(indexOf + 1);

        FileAlterationObserver observer = new FileAlterationObserver(dir, new NameFileFilter(filename));
        FileAlterationMonitor monitor = new FileAlterationMonitor(20000);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {

            @Override
            public void onStop(FileAlterationObserver observer) {
                super.onStop(observer);

                System.out.println("=========================stop");
            }

            @Override
            public void onFileChange(File file) {

                FileSystemResource fileSystemResource = new FileSystemResource(file);
                EncodedResource encodedResource = new EncodedResource(fileSystemResource);
                YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
                factory.setResources(encodedResource.getResource());
                Properties properties = factory.getObject();
                environment.getPropertySources()
                        .replace(filename, new PropertiesPropertySource(filename, properties));
                final Properties properties1 = new Properties();
                properties1.setProperty("my-test", "hello-world");
                environment.getPropertySources().addLast(new PropertiesPropertySource("my-test", properties1));
                ConfigurationPropertiesBindingPostProcessor bean = context
                        .getBean(ConfigurationPropertiesBindingPostProcessor.class);

                ConfigMap bean1 = context.getBean(ConfigMap.class);
                bean1.setTopics(null);
                bean1.setScopes(null);
                bean.postProcessBeforeInitialization(bean1, "configMap");
                try {
                    bean1.afterPropertiesSet();
                } catch (Exception e) {
                    // e.printStackTrace();
                }

            }
        };
        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();

    }
}
