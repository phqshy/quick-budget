package me.fishy.testapp.common.request;

import android.icu.util.Output;

import androidx.annotation.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class GetRequest implements Request{
    private String url;
    private Map<String, String> args = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Function<String, String> after = (s) -> s;

    public GetRequest(String url, @Nullable Map<String, String> args, @Nullable Map<String, String> headers, @Nullable Function<String, String> executeAfter){
        this.url = url;
        if (!url.substring(url.length() - 1).equals("?")) url += "?";
        this.args = args;
        this.headers = headers;
        if (executeAfter != null){
            this.after = executeAfter;
        }
    }

    @Override
    public CompletableFuture<String> execute() {
        Executor executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                if (args != null){
                    for (String s : args.keySet()){
                        url = url + s + "=" + args.get(s) + "&";
                    }
                }
                HttpURLConnection con = (HttpURLConnection) URI.create(url).toURL().openConnection();
                con.setRequestMethod("GET");
                con.setDoOutput(true);
                if (headers != null){
                    for (String s : headers.keySet()){
                        con.addRequestProperty(s, headers.get(s));
                    }
                }
                con.connect();
                InputStream out = con.getInputStream();
                Scanner scan = new Scanner(out);
                String response = "";
                while (scan.hasNext()) {
                    response += scan.useDelimiter("\\A").next();
                }
                response = after.apply(response);
                future.complete(response);
            } catch (Exception e){
                future.cancel(true);
            }
        });
        return future;
    }
}
