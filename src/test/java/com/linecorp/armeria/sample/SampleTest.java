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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.common.thrift.ThriftCompletableFuture;
import com.linecorp.armeria.thrift.HelloService;

public class SampleTest {
    @Test
    public void test() throws Exception {
        ThriftCompletableFuture<String> future = new ThriftCompletableFuture<>();
        HelloService.AsyncIface service = Clients.newClient("tcompact+http://localhost:8080/api/data",
                                                            HelloService.AsyncIface.class);
        service.hello("imasahiro", future);
        assertThat(future.get()).isEqualTo("hello imasahiro");
    }
}
