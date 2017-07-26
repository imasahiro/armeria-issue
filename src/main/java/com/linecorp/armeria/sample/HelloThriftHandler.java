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

import javax.inject.Named;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

import com.linecorp.armeria.thrift.HelloService;

@Named
public class HelloThriftHandler implements HelloService.AsyncIface {
    @Override
    public void hello(String name, AsyncMethodCallback<String> resultHandler) throws TException {
        resultHandler.onComplete("hello " + name);
    }
}
