package me.fishy.quickbudget.common.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JSONPostRequest extends HTTPPostRequest{
    public JSONPostRequest(String strurl) throws MalformedURLException {
        super(strurl);
    }
    public CompletableFuture<String> post(String json){
        Executor executor = Executors.newSingleThreadExecutor();
        CompletableFuture<String> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                byte[] out = json.getBytes(StandardCharsets.UTF_8);
                con.setFixedLengthStreamingMode(out.length);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.connect();

                OutputStream os = con.getOutputStream();
                os.write(out);

                InputStream is = con.getInputStream();
                Scanner scanner = new Scanner(is);
                String responseBody = scanner.useDelimiter("\\A").next();
                future.complete(responseBody);
                is.close();
            } catch (IOException e) {
                future.cancel(true);
                e.printStackTrace();
            }
        });
        return future;
    }
}
