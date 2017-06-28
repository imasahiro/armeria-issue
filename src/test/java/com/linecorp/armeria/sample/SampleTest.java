package com.linecorp.armeria.sample;

import static com.linecorp.armeria.internal.shaded.guava.collect.ImmutableList.toImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.futures.CompletableFutures;

import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.client.logging.LoggingClient;
import com.linecorp.armeria.client.retrofit2.ArmeriaRetrofitBuilder;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);

    static {
        System.setProperty("io.netty.eventLoopThreads", "2");
    }

    @Test
    public void concat() throws Exception {
        SampleApi api = new ArmeriaRetrofitBuilder(ClientFactory.DEFAULT)
                .baseUrl("h1c://localhost:8080/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .withClientOptions((s, cb) -> cb.decorator(HttpRequest.class, HttpResponse.class,
                                                           LoggingClient.newDecorator()))
                .build()
                .create(SampleApi.class);
        Completable.concat(IntStream.range(0, 1024)
                                    .mapToObj(i -> api.api(i).toCompletable())
                                    .collect(toImmutableList()))
                   .blockingAwait();
    }

    @Test
    public void blockingAwait() throws Exception {
        SampleApi api = new ArmeriaRetrofitBuilder(ClientFactory.DEFAULT)
                .baseUrl("h1c://localhost:8080/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .withClientOptions((s, cb) -> cb.decorator(HttpRequest.class, HttpResponse.class,
                                                           LoggingClient.newDecorator()))
                .build()
                .create(SampleApi.class);
        Flowable.fromIterable(IntStream.range(0, 1024)
                                       .boxed()
                                       .collect(toImmutableList()))
                .flatMapSingle(api::api)
                .flatMapCompletable(unused -> Completable.complete()).blockingAwait();
    }

    @Test
    public void httpClient() throws Exception {
        HttpClient httpClient = Clients.newClient("none+h1c://localhost:8080/", HttpClient.class);
        List<CompletableFuture<AggregatedHttpMessage>> futures = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            futures.add(httpClient.get("/" + i).aggregate());
        }

        CompletableFutures.allAsList(futures).join().forEach(v -> logger.info("{}", v));
    }
}
