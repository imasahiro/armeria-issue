package com.linecorp.armeria.sample;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.http.HttpClient;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    @Test
    public void test() throws Exception {
        HttpClient httpClient = Clients.newClient("none+http://localhost:8080/", HttpClient.class);
        while(true) {
            logger.info("{}", httpClient.get("/internal/healthcheck").aggregate().get().content()
                                        .toStringUtf8());
            Thread.sleep(100);
        }
    }
}
