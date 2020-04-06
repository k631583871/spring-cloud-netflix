/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.netflix.eureka.config;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ReactiveCommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration;
import org.springframework.cloud.netflix.eureka.reactive.EurekaReactiveDiscoveryClientConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Eureka-specific helper for config client that wants to lookup the config server via
 * discovery.
 *
 * @author Dave Syer
 */
@ConditionalOnClass(ConfigServicePropertySourceLocator.class)
@ConditionalOnProperty(value = "spring.cloud.config.discovery.enabled",
        matchIfMissing = false)
@Configuration(proxyBeanMethods = false)
@Import({EurekaDiscoveryClientConfiguration.class, // this emulates
        // @EnableDiscoveryClient, the import
        // selector doesn't run before the
        // bootstrap phase
        EurekaClientAutoConfiguration.class,
        EurekaReactiveDiscoveryClientConfiguration.class,
        ReactiveCommonsClientAutoConfiguration.class})
public class EurekaDiscoveryClientConfigServiceBootstrapConfiguration
        implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();

        ConfigurableEnvironment env = context.getEnvironment();
        if ("bootstrap".equals(env.getProperty("spring.config.name"))) {

            context.getBean(EurekaClient.class).shutdown();

            String[] beanNamesForType = context
                    .getBeanNamesForType(DiscoveryClient.class);

            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) context
                    .getAutowireCapableBeanFactory();
            for (String name : beanNamesForType) {
                beanDefinitionRegistry.removeBeanDefinition(name);
            }
        }
    }
}
