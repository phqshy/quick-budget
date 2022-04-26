package me.fishy.quickbudget.common.request;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class HTTPPostRequest {
    protected final URL url;

    public HTTPPostRequest(String strurl) throws MalformedURLException {
        this.url = new URL(strurl);
    }

    public CompletableFuture<String> post(String... args){
        Executor executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                future.complete(null);
            } catch (IOException e) {
                future.cancel(true);
                e.printStackTrace();
            }
        });
        return future;
    }
}
