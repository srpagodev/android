package com.srpago.library.communication;


import com.srpago.library.common.PixzelleErrors;

/**
 * Created by Rodolfo on 08/12/2015 for Jyb.
 * Pixzelle Studio S. de R.L. All rights reserved.
 */
public interface WebServiceListener<T>{
    void onSuccess(@PixzelleErrors.SERVER_CODES int code, Response<T> response, int webService);
    void onError(@PixzelleErrors.SERVER_CODES int code, Response response, int webService);
}
