package com.srpago.library.communication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.srpago.library.api.WebServiceConnection;
import com.srpago.library.api.parsers.ParserResponseFactory;
import com.srpago.library.common.Logger;
import com.srpago.library.common.PixzelleErrors;
import com.srpago.library.common.SPDefinitions;


/**
 * Created by Rodolfo on 08/12/2015 for TestIt.
 * Pixzelle Studio S. de R.L. All rights reserved.
 */
@SuppressLint("ParcelCreator")
public class ServiceCore extends NetworkHandler {
    private WebServiceListener webServiceListener;
    private int service;
    private int method;
    private String customUrl;
    private WebServiceConnection webServiceConnection;

    public ServiceCore(Context context) {
        super(context);
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public void executeService(int service, final WebServiceListener listener, final Object[] bodyParams, final String[] urlParams) {

        this.webServiceListener = listener;
        this.service = service;
        this.method = getRESTMethod(service);

        try {
            callWebService(bodyParams, urlParams);
        } catch (Exception ex) {
            callWebService(bodyParams, urlParams);
        }
    }

    private void callWebService(final Object[] bodyParams, final String[] urlParams) {
//        PixzelleLogger.logDebug(getContext(),"WS execution", service + "");
        Logger.logDebug("WS execution Id", service + "");
        webServiceConnection = buildWebServiceConnection(this.getContext(), service, this.method);
        webServiceConnection.setListener(this);
        webServiceConnection.setUrlParams(urlParams);
        webServiceConnection.setCustomUrl(customUrl);
        webServiceConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bodyParams);
    }

    protected WebServiceConnection buildWebServiceConnection(Context context, int service, int method) {
        return new WebServiceConnection(context, service, method);
    }

    protected int getRESTMethod(int service) {
        return SPDefinitions.getRESTType(service);
    }

    @Override
    public PixzelleParserResponseFactory buildResponseFactory() {
        return new ParserResponseFactory();
    }

    @Override
    public void onResponseOk(@PixzelleErrors.SERVER_CODES int code, Response response, int webService) {
        customUrl = null;
        if (webServiceListener != null) {
            webServiceListener.onSuccess(code, response, webService);
        }
    }

    @Override
    public void onResponseError(@PixzelleErrors.SERVER_CODES int code, Response response, int webService) {
        customUrl = null;
        if (webServiceListener != null) {
            webServiceListener.onError(code, response, webService);
        }
    }

    public WebServiceListener getWebServiceListener() {
        return webServiceListener;
    }

    public void setWebServiceListener(WebServiceListener webServiceListener) {
        this.webServiceListener = webServiceListener;
    }
}
