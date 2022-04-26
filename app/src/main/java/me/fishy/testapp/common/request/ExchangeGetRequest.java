package me.fishy.testapp.common.request;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExchangeGetRequest extends HTTPGetRequest{
    private final Context context;

    public ExchangeGetRequest(String strurl, Context context) throws MalformedURLException {
        super(strurl);
        this.context = context;
    }

    @Override
    public CompletableFuture<String> get() throws IOException {
        Executor executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                InputStream is = url.openStream();
                Scanner scanner = new Scanner(is);
                String responseBody = scanner.useDelimiter("\\A").next();
                future.complete(responseBody);
                is.close();
            } catch (IOException e){
                future.cancel(true);
                e.printStackTrace();
            }
        });
        return future;
    }
}