package com.srpago.library.api;

import android.content.Context;
import android.util.Base64;
import android.util.Pair;


import com.srpago.library.SrPago;
import com.srpago.library.api.parsers.ParserRequestFactory;
import com.srpago.library.common.Logger;
import com.srpago.library.common.SPDefinitions;
import com.srpago.library.communication.PixzelleParserRequestFactory;
import com.srpago.library.communication.PixzelleWebServiceConnection;
import com.srpago.library.services.SPUserAgentService;

import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Rodolfo on 21/07/2016 for Test it.
 * Pixzelle Studio S. de R.L. All rights reserved.
 */
public class WebServiceConnection extends PixzelleWebServiceConnection<Object, Void> {

    public WebServiceConnection(final Context activity, final int webService) {
        super(activity, webService);
    }

    public WebServiceConnection(final Context context, final int webService, final int method) {
        super(context, webService, method);
    }

    @Override
    public String buildUrl(Object... params) {
        return getURL(this.getWebService());
    }

    @Override
    public String[] getUrlParams() {
        return urlParams;
    }



//    public String getURL(){
//        if (super.getCustomUrl() == null || super.getCustomUrl().isEmpty() || super.getCustomUrl().equals(PixzelleDefinitions.STRING_NULL)) {
//            return url;
//        } else {
//            return super.getCustomUrl();
//        }
//    }



    @Override
    public ArrayList<Pair<String, String>> buildHeader() {
        ArrayList<Pair<String, String>> headers = new ArrayList<>();
        headers.add(new Pair<>("Content-Type", "application/json"));
        headers.add(new Pair<>("Accept", "application/json"));
        try {
            SrPago srPago = SrPago.getInstance(null);
            String publicKey = String.format(Locale.getDefault(), "%s:", srPago.getPublishableKey());
            headers.add(new Pair<>("Authorization", String.format(Locale.getDefault(), "Basic %s", Base64.encodeToString(publicKey.getBytes(), Base64.DEFAULT))));
        }catch (Exception ex){

        }


        final String userAgent = SPUserAgentService.parseUserAgent(SPUserAgentService.getUserAgent(getContext()));
        Logger.logDebug("User agent", userAgent);
        headers.add(new Pair<>("X-User-Agent", userAgent));

//        if (!Global.getStringKey(this.getContext(), PixzelleDefinitions.REGISTER_TOKEN).equals(PixzelleDefinitions.STRING_NULL)
//                && this.getWebService() != PixzelleDefinitions.WEB_SERVICE_LOGIN) {
//            headers.add(new Pair<>(PixzelleDefinitions.AUTH_TOKEN, String.format(Locale.getDefault(), "%s %s", PixzelleDefinitions.BEARER, Global.getStringKey(this.getContext(), PixzelleDefinitions.REGISTER_TOKEN))));
//        } else {
//            try {
//                ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
//                Bundle bundle = applicationInfo.metaData;
//                headers.add(new Pair<>(PixzelleDefinitions.AUTH_TOKEN, String.format(Locale.getDefault(), "%s %s", bundle.getString("token"), bundle.getString("secret"))));
//            } catch (Exception ex) {
//                PixzelleLogger.logError(ex);
//            }
//        }
//
//        try {
//            headers.add(new Pair<>("X-User-Agent", "{\"agent\":\"" + PixzelleUtilities.getApplicationName(this.getContext()) + " Android" + "/" + BuildConfig.VERSION_NAME + "\"}"));
//        } catch (Exception ex) {
//            //PixzelleLogger.logError(ex);
//        }
//        ArrayList<Pair<String, String>> extras = addExtraHeader(this.getWebService());
//
//        if (extras != null) {
//            headers.addAll(extras);
//        }


//        if (getHeaders() != null) {
//            for (Pair pair : getHeaders()) {
//                headers.add(pair);
//            }
//        }

        return headers;
    }

    @Override
    public byte[] buildBody(Object... params) {
        try {
            PixzelleParserRequestFactory parserRequestFactory = buildParserRequestFactory();
            String str;
            if (getRequestType(getWebService()) == PixzelleWebServiceConnection.REQUEST_TYPE_JSON) {
                str = parserRequestFactory.convertToJSON(this.getContext(), this.getWebService(), params);
                //PixzelleLogger.logDebug(getContext(), "Body", str);
                Logger.logDebug("Body", str);
            } else {
//                str = parserRequestFactory.convertToEncoded(this.getContext(), this.getWebService(), params);
//                PixzelleLogger.logDebug("Encoded URL", str);
                str = "";
            }
            return str.getBytes("UTF-8");
        } catch (Exception ex) {
            //PixzelleLogger.logError(ex);
        }
        return null;
    }


    public String getURL(int webService) {
        try {
            return String.format(Locale.getDefault(), "%s/%s/%s", getURL(SrPago.getInstance(null).isLiveMode()), SPDefinitions.SP_API_VERSION, SPDefinitions.getMethod(webService));
        }catch (Exception ex){
            return "";
        }
    }

    protected ArrayList<Pair<String, String>> addExtraHeader(int service) {
        return null;
    }

    protected PixzelleParserRequestFactory buildParserRequestFactory() {
        return new ParserRequestFactory();
    }

    protected int getRequestType(int webService) {
        return PixzelleWebServiceConnection.REQUEST_TYPE_JSON;
    }

    @Override
    public boolean cancelWS(boolean mayInterrupt) {
        return super.cancelWS(true);
    }

    @Override
    public int wsType() {
        return OAUTH;
    }

    private String getURL(boolean liveMode){
        if(liveMode){
            return "https://api.srpago.com";
        }else{
            return "https://sandbox-api.srpago.com";
        }
    }
}