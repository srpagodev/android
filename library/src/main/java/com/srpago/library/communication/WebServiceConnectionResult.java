package com.srpago.library.communication;

import android.content.Context;

import com.srpago.library.common.PixzelleErrors;

import java.util.List;
import java.util.Map;


/**
 * Created by Rodolfo on 08/12/2015 for Jyb.
 * Pixzelle Studio S. de R.L. All rights reserved.
 */
public abstract class WebServiceConnectionResult implements PixzelleWebServiceListener {
    private Context context;
    protected int webServiceRefresh;
    protected Object[] paramsToRefresh;
    protected String[] urlParamsToRefresh;

    public WebServiceConnectionResult(){

    }

    public WebServiceConnectionResult(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void result(String result, int webService) {

    }

    @Override
    public void result(@PixzelleErrors.SERVER_CODES int code, String result, Map<String, List<String>> headers, String url, String method, String body, int webService, Object[] params, String[] urlParams) {
        switch (code) {
            case PixzelleErrors.OK:
            case PixzelleErrors.CREATED:
            case PixzelleErrors.NO_CONTENT:
                onOkConnection(code, result, headers, webService);
                break;

            case PixzelleErrors.NO_INTERNET:
                webServiceRefresh = webService;
                paramsToRefresh = params;
                urlParamsToRefresh = urlParams;
                onNoInternetConnection(webService);
                break;

            case PixzelleErrors.TIME_OUT:
                webServiceRefresh = webService;
                paramsToRefresh = params;
                urlParamsToRefresh = urlParams;
                onTimeoutConnection(webService);
                break;

            case PixzelleErrors.UNKNOWN_CODE:
                webServiceRefresh = webService;
                paramsToRefresh = params;
                urlParamsToRefresh = urlParams;
                onServerError(code, result, headers,url, body, method, webService, params, urlParams);
                //onUnknownConnectionError(webService);
                break;

            default:
                webServiceRefresh = webService;
                paramsToRefresh = params;
                urlParamsToRefresh = urlParams;
                onServerError(code, result, headers, url, body, method, webService, params, urlParams);
                break;
        }
    }

    public abstract void onNoInternetConnection(int webService);

    public abstract void onTimeoutConnection(int webService);

    public abstract void onUnknownConnectionError(int webService);

    public abstract void onServerError(@PixzelleErrors.SERVER_CODES int code, String error, Map<String, List<String>> headers, String url, String body, String method, int webService, Object[] params, String[] urlParams);

    public abstract void onOkConnection(@PixzelleErrors.SERVER_CODES int code, String result, Map<String, List<String>> headers, int webService);
}
