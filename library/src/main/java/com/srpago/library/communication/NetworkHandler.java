package com.srpago.library.communication;

import android.content.Context;

import com.srpago.library.common.Logger;
import com.srpago.library.common.PixzelleErrors;
import com.srpago.library.model.SrPagoError;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Rodolfo on 10/08/2015.
 */
public abstract class NetworkHandler extends WebServiceConnectionResult {
    public NetworkHandler(Context context) {
        super(context);
    }

    @Override
    public void onNoInternetConnection(final int webService) {
        Response<SrPagoError> response = new Response<>();
        response.setItems(new ArrayList<SrPagoError>());
        response.getItems().add(new SrPagoError(){{
            setCode("31");
            setMessage("Internet");
            setDescription("No cuentas con una conexión a Internet.");
        }});
        onResponseError(PixzelleErrors.NO_INTERNET, response, webService);
    }

    @Override
    public void onTimeoutConnection(final int webService) {
        Response<SrPagoError> response = new Response<>();
        response.setItems(new ArrayList<SrPagoError>());
        response.getItems().add(new SrPagoError(){{
            setCode("32");
            setMessage("Internet");
            setDescription("El servidor no responde.");
        }});
        onResponseError(PixzelleErrors.TIME_OUT, response, webService);
    }

    @Override
    public void onUnknownConnectionError(final int webService) {
        Response<SrPagoError> response = new Response<>();
        response.setItems(new ArrayList<SrPagoError>());
        response.getItems().add(new SrPagoError(){{
            setCode("33");
            setMessage("Internet");
            setDescription("Hubo un error fuera de lo normal.");
        }});
        onResponseError(PixzelleErrors.UNKNOWN_CODE, response, webService);
    }

    @Override
    public void onServerError(@PixzelleErrors.SERVER_CODES int code, String error, Map<String, List<String>> headers, String url, String body, String method, int webService, Object[] params, String[] urlParams) {
        Logger.logWarning("SrPagoError code " + webService, code + "");
        Logger.logWarning("SrPagoError response " + webService, error);
        try {
            if (code != PixzelleErrors.NOT_FOUND) {
                if (code == PixzelleErrors.MAINTENANCE) {
                    onUnknownConnectionError(webService);
                } else {
                    PixzelleParserResponseFactory pixzelleParserResponseFactory = buildResponseFactory();
                    Response response;
                    try {
                        response = pixzelleParserResponseFactory.parseError(error);
                    }catch (JSONException ex){
                        response = new Response();
                    }
                    if (response != null) {
                        response.setHeader(headers);
                        response.setUrl(url);
                        response.setBody(body);
                        response.setMethod(method);
                        onResponseError(code, response, webService);
//                        if(code == Pixzelle.SERVER_ERROR){
//                            onUnknownConnectionError(webService);
//                        }
                    } else {
                        onUnknownConnectionError(webService);
                    }
                }
            } else {
                onResponseError(code, null, webService);
            }
        } catch (Exception ex) {
            //PixzelleLogger.logError(ex);
            onUnknownConnectionError(webService);
        }
    }

    @Override
    public void onOkConnection(@PixzelleErrors.SERVER_CODES int code, String result, Map<String, List<String>> headers, int webService) {
        Logger.logMessage("Ok code " + webService, code + "");
        Logger.logMessage("Ok response" + webService, result);
        try {
            PixzelleParserResponseFactory pixzelleParserResponseFactory = buildResponseFactory();
            Response response = pixzelleParserResponseFactory.parseResponse(getContext(), webService, result);
            if (response != null) {
                response.setHeader(headers);
                onResponseOk(code, response, webService);
            } else {
                onUnknownConnectionError(webService);
            }
        } catch (Exception ex) {
            Logger.logError(ex);
            onUnknownConnectionError(webService);
        }
    }

    public abstract void onResponseOk(@PixzelleErrors.SERVER_CODES int code, Response<?> response, int webService);

    public abstract void onResponseError(@PixzelleErrors.SERVER_CODES int code, Response<?> response, int webService);

    public abstract PixzelleParserResponseFactory buildResponseFactory();
}
