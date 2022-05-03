package me.fishy.testapp.common.request;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginGetRequest extends HTTPGetRequest{
    public LoginGetRequest(String strurl, String username, String password) throws MalformedURLException {
        super(strurl + "?username=" + username + "&password=" + password);
    }

    @Override
    public CompletableFuture<String> get() {
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
