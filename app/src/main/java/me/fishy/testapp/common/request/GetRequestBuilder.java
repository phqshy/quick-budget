package me.fishy.testapp.common.request;

import java.util.Map;
import java.util.function.Function;

public class GetRequestBuilder {
    private String url = null;
    private Map<String, String> args = null;
    private Map<String, String> headers = null;
    private Function<String, String> executeAfter = null;

    public GetRequestBuilder setUrl(String url){
        this.url = url;
        return this;
    }

    public GetRequestBuilder setArgs(Map<String, String> args){
        this.args = args;
        return this;
    }

    public GetRequestBuilder setHeaders(Map<String, String> headers){
        this.headers = headers;
        return this;
    }

    public GetRequestBuilder setAfterFunction(Function<String, String> executeAfter){
        this.executeAfter = executeAfter;
        return this;
    }

    public GetRequest build(){
        if (url == null) throw new IllegalArgumentException("There is no URL in this request builder!");
        return new GetRequest(url, args, headers, executeAfter);
    }
}
