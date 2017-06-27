/*
 * Copyright 2017 Masahiro Ide
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.sample;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.linecorp.armeria.server.PathMapping;
import com.linecorp.armeria.server.http.tomcat.TomcatService;
import com.linecorp.armeria.spring.HttpServiceRegistrationBean;

@SpringBootApplication
@EnableWebMvc
public class SampleApplication {

    @Bean
    HttpServiceRegistrationBean httpService(final EmbeddedWebApplicationContext applicationContext) {
        Connector connector = getConnector(applicationContext);
        return new HttpServiceRegistrationBean()
                .setServiceName("buggy-service")
                .setService(TomcatService.forConnector(connector))
                .setPathMapping(PathMapping.ofCatchAll());
    }

    private static Connector getConnector(EmbeddedWebApplicationContext applicationContext) {
        final TomcatEmbeddedServletContainer container =
                (TomcatEmbeddedServletContainer) applicationContext.getEmbeddedServletContainer();
        try {
            Field serviceConnectorsField = TomcatEmbeddedServletContainer.class.getDeclaredField(
                    "serviceConnectors");
            serviceConnectorsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Service, Connector[]> connectors =
                    (Map<Service, Connector[]>) serviceConnectorsField.get(container);
            return connectors.values().stream().findFirst().orElseThrow(
                    () -> new IllegalStateException("Connectors not found"))[0];
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
