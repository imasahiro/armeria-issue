package com.linecorp.armeria.sample;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
public class SampleController {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @RequestMapping(path = "/async", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<String>> async(HttpServletResponse servletResponse) {
        CompletableFuture<String> future = new CompletableFuture<>();
        executorService.schedule(() -> future.complete("hello"), 2, TimeUnit.SECONDS);
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>(1000L);
        future.thenAccept(text -> {
            ResponseEntity<String> entity = new ResponseEntity<>(text, HttpStatus.OK);
            result.setResult(entity);
        });
        return result;
    }

    @RequestMapping(path = "/sync", method = RequestMethod.GET)
    public ResponseEntity<String> sync(HttpServletResponse servletResponse) {
        return new ResponseEntity<>("hello", HttpStatus.OK);
    }
}
