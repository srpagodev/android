package com.srpago.library.communication;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;

import com.srpago.library.common.Logger;
import com.srpago.library.common.PixzelleErrors;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * Class for making the things easier for calling a web service.
 *
 * @author Rodolfo Pena - Pixzelle Studios S. de R.L. de C.V.
 * @version 2.5
 * @since 2015-02-09
 */
public abstract class PixzelleWebServiceConnection<Object, Void> extends AsyncTask<Object, Void, Void> {
    private Context context;
    private PixzelleWebServiceListener listener;
    private String result;
    private String finalUrl;
    private String finalMethod;
    private String finalBody;
    @PixzelleErrors.SERVER_CODES
    int resultCode;
    private Map<String, List<String>> headers;
    private java.lang.Object[] params;
    protected String[] urlParams;
    private Pair[] headersToUse;
    private int webService;
    private int retry;
    private int method;

    public final static int POST = 1;
    public final static int GET = 2;
    public final static int PUT = 3;
    public final static int DELETE = 4;

    private HttpsURLConnection httpsURLConnection = null;
    private HttpURLConnection httpURLConnection = null;

    public final static int JSON_TOKEN = 1;
    public final static int OAUTH = 2;

    public final static int REQUEST_TYPE_URL_ENCODED = 1;
    public final static int REQUEST_TYPE_JSON = 2;

    protected String customUrl;

    public PixzelleWebServiceConnection(final Context context, final int webService) {
        this.context = context;
        this.webService = webService;
        this.resultCode = PixzelleErrors.OK;
    }

    public PixzelleWebServiceConnection(final Context context, final int webService, final int method) {
        this.context = context;
        this.webService = webService;
        this.resultCode = PixzelleErrors.OK;
        this.method = method;
    }

    public Pair[] getHeaders() {
        return headersToUse;
    }

    public void setHeaders(Pair... headers) {
        this.headersToUse = headers;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PixzelleWebServiceListener getListener() {
        return listener;
    }


    public void setListener(PixzelleWebServiceListener listener) {
        this.listener = listener;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getWebService() {
        return webService;
    }

    public void setWebService(int webService) {
        this.webService = webService;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public
    @PixzelleErrors.SERVER_CODES
    int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static String writeFile(Context context, InputStream inputStream, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean hasInternet() {
        if (!isConnectingToInternet(this.context)) {
            this.resultCode = PixzelleErrors.NO_INTERNET;
            return false;
        }
        return true;
    }

    private static boolean isConnectingToInternet(Context _context) {
        if (_context == null) return true;

        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        customUrl = null;

        if (this.getListener() != null) {
            //this.getListener().result(this.getResultCode(), this.getResult(), this.headers, this.getWebService(), this.params, this.urlParams);
            this.getListener().result(this.getResultCode(), this.getResult(), this.headers, this.finalUrl, this.finalMethod, this.finalBody, this.getWebService(), this.params, this.urlParams);
        }
    }

    @Override
    protected Void doInBackground(java.lang.Object... params) {
        this.params = params;
        if (!hasInternet())
            return null;

        if (isConnectingToInternet(this.getContext())) {
            try {
                String url;
                if(customUrl == null) {
                    url = buildUrl(params);
                }else{
                    url = customUrl;
                }

                urlParams = getUrlParams();
                if (urlParams != null) {
                    url = String.format(url, getUrlParams());
                }


                ResponseRaw response = getConn(url, params);
                response.setUrl(url);
                InputStream inputStream = response.getInputStream();
                //Log.d("Response code", "" + response.first);
//                if(response.first > 400){
                this.setResultCode(response.getResponseCode());
                this.finalBody = response.getBody() != null ? response.getBody() : "";
                this.finalUrl = response.getUrl();
                this.finalMethod = response.getMethod();
//                }

                if (inputStream != null) {

                    if (!response.findKeyInheader("Content-Type").equals("image/png") &&
                            !response.findKeyInheader("Content-Type").equals("image/jpeg") &&
                            !response.findKeyInheader("Content-Type").equals("application/pdf")) {
                        this.setResult(convertInputStreamToString(inputStream));
                    } else {
                        if(response.findKeyInheader("Content-Type").equals("image/png")) {
                            this.setResult(writeFile(context, inputStream, String.format("%s_image.png", getUrlParams()[0])));
                        }else if(response.findKeyInheader("Content-Type").equals("image/jpg")){
                            this.setResult(writeFile(context, inputStream, String.format("%s_image.jpg", getUrlParams()[0])));
                        } else if(response.findKeyInheader("Content-Type").equals("application/pdf")) {
                            this.setResult(writeFile(context, inputStream, String.format("%s_file.pdf", getUrlParams()[0])));
                        }else if(response.findKeyInheader("Content-Type").equals("image/jpeg")) {
                            this.setResult(writeFile(context, inputStream, String.format("%s_file.jpg", getUrlParams()[0])));
                        }
                    }
                    //Log.d("Response", this.getResult());
                }

                if (response.getHeader() != null) {
                    this.headers = response.getHeader();
                }
            } catch (SocketTimeoutException ex) {
//                PixzelleErrors.printException(ex);
                Logger.logWarning("Line ", "Socket");
                this.setResultCode(PixzelleErrors.TIME_OUT);
            } catch (ConnectTimeoutException ex) {
//                PixzelleErrors.printException(ex);
                Logger.logWarning("Line ", "Timeout");
                this.setResultCode(PixzelleErrors.TIME_OUT);
            } catch (FileNotFoundException ex) {
                Logger.logWarning("Line ", "File");
//                PixzelleErrors.printException(ex);
                this.setResultCode(PixzelleErrors.UNKNOWN_CODE);
            } catch (MalformedURLException ex) {
                Logger.logWarning("Line ", "Malformed ");
//                PixzelleErrors.printException(ex);
                this.setResultCode(PixzelleErrors.UNKNOWN_CODE);
            } catch (IOException ex) {
                Logger.logWarning("Line ", "IO");
//                PixzelleErrors.printException(ex);
                this.setResultCode(PixzelleErrors.UNKNOWN_CODE);
            } catch (Exception ex) {
                Logger.logWarning("Line ", "Other");
//                PixzelleErrors.printException(ex);
                this.setResultCode(PixzelleErrors.UNKNOWN_CODE);
            }
        }
        return null;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    /**
     * The only method that the developer has to call to handle the url.
     *
     * @param params Parameters to build the url.
     * @return The url built with all the parameters
     * @see PixzelleErrors
     */
    public abstract String buildUrl(java.lang.Object... params);

    private ResponseRaw getConn(String urlString, java.lang.Object... params) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        ResponseRaw responseRaw = new ResponseRaw();
        Logger.logDebug("URL", urlString);

        URL url = new URL(urlString);

        if (urlString.contains("https")) {
            httpsURLConnection = null;
            httpsURLConnection = (HttpsURLConnection) url.openConnection();

            if (method == PixzelleWebServiceConnection.GET) {
                httpsURLConnection.setRequestMethod("GET");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    try {
                        httpsURLConnection.setSSLSocketFactory(new TLSSocketFactory());
                    }catch (Exception ex){
                        //PixzelleErrors.printException(ex);
                        Logger.logError(ex);
                    }
                }
                responseRaw.setMethod("GET");
                for (Pair<String, String> header : buildHeader()) {
                    httpsURLConnection.setRequestProperty(header.first, header.second);
                }
            } else if (method == PixzelleWebServiceConnection.POST) {
//                conn.setDoOutput(true);
                httpsURLConnection.setRequestMethod("POST");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    httpsURLConnection.setSSLSocketFactory(new TLSSocketFactory());
                }
                responseRaw.setMethod("POST");
//                conn.setDoInput(true);

                for (Pair<String, String> header : buildHeader()) {
                    httpsURLConnection.setRequestProperty(header.first, header.second);
                }
                byte[] bodyBytes = buildBody(params);



                if (bodyBytes != null) {
                    OutputStream os = httpsURLConnection.getOutputStream();
                    os.write(bodyBytes);
                    os.close();
                    responseRaw.setBody(new String(bodyBytes, "UTF-8"));
                }
            } else if (method == PixzelleWebServiceConnection.PUT) {
                httpsURLConnection.setRequestMethod("PUT");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    httpsURLConnection.setSSLSocketFactory(new TLSSocketFactory());
                }
                responseRaw.setMethod("PUT");

                for (Pair<String, String> header : buildHeader()) {
                    httpsURLConnection.setRequestProperty(header.first, header.second);
                }
                byte[] bodyBytes = buildBody(params);
                if (bodyBytes != null) {
                    OutputStream os = httpsURLConnection.getOutputStream();
                    os.write(bodyBytes);
                    os.close();
                    responseRaw.setBody(new String(bodyBytes, "UTF-8"));
                }
            } else if (method == PixzelleWebServiceConnection.DELETE) {
                httpsURLConnection.setRequestMethod("DELETE");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    httpsURLConnection.setSSLSocketFactory(new TLSSocketFactory());
                }
                responseRaw.setMethod("DELETE");

                for (Pair<String, String> header : buildHeader()) {
                    httpsURLConnection.setRequestProperty(header.first, header.second);
                }
//                byte[] bodyBytes = buildBody(params);
//                if (bodyBytes != null) {
//                    OutputStream os = httpsURLConnection.getOutputStream();
//                    os.write(bodyBytes);
//                    os.close();
//                }
            }

//            SSLContext sc;
//            sc = SSLContext.getInstance("TLS");
//            sc.init(null, null, new java.security.SecureRandom());
//            conn.setSSLSocketFactory(sc.getSocketFactory());

            httpsURLConnection.setReadTimeout(120000);
            httpsURLConnection.setConnectTimeout(65000);

//            if(httpsURLConnection != null) {
//                PixzelleLogger.logDebug(context, "Code connection", "" + httpsURLConnection.getResponseCode());
//            }


            responseRaw.setResponseCode(httpsURLConnection.getResponseCode());
            responseRaw.setInputStream(httpsURLConnection.getResponseCode() >= 400 ? httpsURLConnection.getErrorStream() : httpsURLConnection.getInputStream());
            responseRaw.setHeader(httpsURLConnection.getHeaderFields());

            return responseRaw;
        } else {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(1120000);
            httpURLConnection.setConnectTimeout(65000);
            for (Pair<String, String> header : buildHeader()) {
                httpURLConnection.setRequestProperty(header.first, header.second);
            }
            if (method == PixzelleWebServiceConnection.GET) {
                httpURLConnection.setRequestMethod("GET");
                responseRaw.setMethod("GET");
                httpURLConnection.setDoInput(true);
            } else if (method == PixzelleWebServiceConnection.POST) {
                //conn = (HttpURLConnection) url.openConnection();
//                conn.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                responseRaw.setMethod("POST");
//                conn.setDoInput(true);

                byte[] bodyBytes = buildBody(params);
                if (bodyBytes != null) {
                    OutputStream os = httpURLConnection.getOutputStream();
                    os.write(bodyBytes);
                    os.close();
                    responseRaw.setBody(new String(bodyBytes, "UTF-8"));
                }
            } else if (method == PixzelleWebServiceConnection.PUT) {
                httpURLConnection.setRequestMethod("PUT");
                responseRaw.setMethod("PUT");
                byte[] bodyBytes = buildBody(params);
                if (bodyBytes != null) {
                    OutputStream os = httpURLConnection.getOutputStream();
                    os.write(bodyBytes);
                    os.close();
                    responseRaw.setBody(new String(bodyBytes, "UTF-8"));
                }
            } else if (method == PixzelleWebServiceConnection.DELETE) {
                httpURLConnection.setRequestMethod("DELETE");
                responseRaw.setMethod("DELETE");
//                byte[] bodyBytes = buildBody(params);
//                if (bodyBytes != null) {
//                    OutputStream os = conn.getOutputStream();
//                    os.write(bodyBytes);
//                    os.close();
//                }
            }
            //conn.setDoInput(true);

            httpURLConnection.connect();

            responseRaw.setResponseCode(httpURLConnection.getResponseCode());
            responseRaw.setInputStream(httpURLConnection.getResponseCode() >= 400 ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream());
            responseRaw.setHeader(httpURLConnection.getHeaderFields());
            return responseRaw;
        }
    }

    public byte[] buildBody(java.lang.Object... params) {
        return null;
    }

    public void setUrlParams(String... params) {
        this.urlParams = params;
    }

    public String[] getUrlParams(){
        return this.urlParams;
    }

    public ArrayList<Pair<String, String>> buildHeader() {
        return null;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public boolean cancelWS(boolean mayInterrupt) {
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        if (httpsURLConnection != null) {
            httpsURLConnection.disconnect();
        }
        return super.cancel(mayInterrupt);
    }

    public abstract int wsType();
}