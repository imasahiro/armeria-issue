package com.linecorp.armeria.sample;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SampleApi {
    @GET("/api/{id}")
    Single<Response<MyResponse>> api(@Path("id") int id);

    class MyResponse {
        int id;

        public void setId(int id) {
            this.id = id;
        }
    }
}
