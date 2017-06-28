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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.codahale.metrics.MetricRegistry;

import com.linecorp.armeria.common.DefaultHttpResponse;
import com.linecorp.armeria.common.DeferredHttpResponse;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.PathMapping;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.metric.DropwizardMetricCollectingService;
import com.linecorp.armeria.spring.HttpServiceRegistrationBean;

@SpringBootApplication
public class SampleApplication {
    private static final Logger logger = LoggerFactory.getLogger(SampleApplication.class);
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    @Bean
    HttpServiceRegistrationBean httpService(MetricRegistry metricRegistry) {
        return new HttpServiceRegistrationBean()
                .setServiceName("my-service")
                .setService(new HttpService() {
                    @Override
                    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                        if (req.method() != HttpMethod.GET) {
                            final DefaultHttpResponse res = new DefaultHttpResponse();
                            res.respond(HttpStatus.METHOD_NOT_ALLOWED);
                            return res;
                        }
                        DeferredHttpResponse res = new DeferredHttpResponse();
                        logger.info("req: {}", req.path());
                        if (req.path().lastIndexOf('0') == req.path().length() - 1) {
                            logger.info("================= {}", req.path());
                            executorService.schedule(() -> res.delegate(HttpResponse.of(HttpStatus.OK,
                                                                                        MediaType.ANY_TEXT_TYPE,
                                                                                        "{\"id\": 1}")),
                                                     5, TimeUnit.SECONDS);
                        } else {
                            res.delegate(HttpResponse.of(HttpStatus.OK, MediaType.ANY_TEXT_TYPE,
                                                         "{\"id\": 1}"));
                        }
                        return res;
                    }
                }.decorate(DropwizardMetricCollectingService.newDecorator(metricRegistry, "service"))
                 .decorate(LoggingService.newDecorator()))
                .setPathMapping(PathMapping.ofPrefix("/api"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
