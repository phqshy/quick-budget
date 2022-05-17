package me.fishy.testapp.common.request.get;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.fishy.testapp.common.request.HTTPGetRequest;

public class SessionGetRequest extends HTTPGetRequest {
    public SessionGetRequest(String strurl, String username, String session) throws MalformedURLException {
        super(strurl + "?username=" + username + "&uuid=" + session);
    }

    @Override
    public CompletableFuture<String> get(){
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
