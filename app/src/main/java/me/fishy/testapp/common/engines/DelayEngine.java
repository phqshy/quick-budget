package me.fishy.testapp.common.engines;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DelayEngine {
    public static CompletableFuture<Boolean> delay(int seconds){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Thread.sleep(1000L * seconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.complete(true);
        });
        return future;
    }
}
