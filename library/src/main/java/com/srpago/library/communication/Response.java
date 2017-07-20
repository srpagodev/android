package com.srpago.library.communication;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Rodolfo on 07/09/2015 for GronJobb.
 * Pixzelle Studio S. de R.L. All rights reserved.
 */
@SuppressLint("ParcelCreator")
public class Response<T>{
    private boolean status;
    private Map<String, List<String>> header;
    private ArrayList<T> items;
    private String raw;
    private String code;
    private String message;
    private String url;
    private String body;
    private String method;

    public Response(){
        super();
        setStatus(true);
        items = new ArrayList<>();
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public String findKeyInheader(String key){
        return header.get(key).get(0);
    }

    public void setHeader(Map<String, List<String>> header) {
        this.header = header;
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
