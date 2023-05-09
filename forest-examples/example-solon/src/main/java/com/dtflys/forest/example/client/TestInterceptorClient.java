package org.dromara.forest.example.client;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.DataParam;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.example.interceptors.ApiClientInterceptor;


@ForestClient
@BaseRequest(baseURL = "localhost:8080")
public interface TestInterceptorClient {

    @Request(
            url = "/receive-interceptor",
            type = "post",
            dataType = "text",
            interceptor = ApiClientInterceptor.class
    )
    String testInterceptor(@DataParam("username") String username);
}
