package me.fishy.testapp.common.request;

import java.util.concurrent.CompletableFuture;

public interface Request{
    CompletableFuture<String> execute();
}
