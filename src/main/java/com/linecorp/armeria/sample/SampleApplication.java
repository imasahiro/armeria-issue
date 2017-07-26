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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.linecorp.armeria.common.thrift.ThriftSerializationFormats;
import com.linecorp.armeria.server.thrift.THttpService;
import com.linecorp.armeria.spring.ThriftServiceRegistrationBean;
import com.linecorp.armeria.thrift.HelloService;

@SpringBootApplication
public class SampleApplication {
    @Bean
    public ThriftServiceRegistrationBean rpcService(HelloService.AsyncIface helloService) {
        return new ThriftServiceRegistrationBean()
                .setServiceName("HelloService")
                .setService(THttpService.of(helloService, ThriftSerializationFormats.COMPACT))
                .setPath("/api/data");
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
