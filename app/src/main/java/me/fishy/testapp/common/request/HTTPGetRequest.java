package me.fishy.testapp.common.request;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class HTTPGetRequest {
    protected final URL url;

    public HTTPGetRequest(String strurl) throws MalformedURLException {
        this.url = new URL(strurl);
    }

    public CompletableFuture<String> get() throws Exception {
        Executor executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                InputStream is = url.openStream();
                Scanner scanner = new Scanner(is);
                String responseBody = scanner.useDelimiter("\\A").next();
                future.complete(responseBody);
                is.close();
            } catch (Exception e){
                future.cancel(true);
            }
        });
        return future;
    }
}
