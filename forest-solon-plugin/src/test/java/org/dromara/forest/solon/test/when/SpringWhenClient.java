package org.dromara.forest.solon.test.when;

import org.dromara.forest.annotation.*;
import org.dromara.forest.callback.DefaultRetryWhen;
import org.dromara.forest.callback.DefaultSuccessWhen;
import org.dromara.forest.callback.OnError;
import org.dromara.forest.callback.OnSuccess;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Retry;
import org.dromara.forest.annotation.Success;
import org.dromara.forest.annotation.Var;

@ForestClient
public interface SpringWhenClient {

    @Get("http://localhost:${port}/")
    @Retry(condition = DefaultRetryWhen.class)
    String testSuccessWhen(@Var("port") int port, OnError onError);

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "3")
    @Success(condition = DefaultSuccessWhen.class)
    String testRetryWhen(@Var("port") int port, OnSuccess onSuccess);

}
