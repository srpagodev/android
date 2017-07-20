package com.srpago.library.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.srpago.library.SpTokenListener;
import com.srpago.library.common.PixzelleErrors;
import com.srpago.library.common.SPDefinitions;
import com.srpago.library.communication.Response;
import com.srpago.library.communication.ServiceCore;
import com.srpago.library.communication.WebServiceListener;
import com.srpago.library.model.Card;
import com.srpago.library.model.SrPagoError;
import com.srpago.library.model.Token;


/**
 * Created by Rodolfo on 23/06/2017.
 */

public class SPTokenService {
    public static void requestToken(@NonNull Context context, @NonNull Card spCard, @NonNull final SpTokenListener spTokenListener) {
        Object[] params = {spCard};
        ServiceCore serviceCore = new ServiceCore(context);
        serviceCore.executeService(SPDefinitions.SP_ID_CREATE_TOKEN, new WebServiceListener<Token>() {
            @Override
            public void onSuccess(@PixzelleErrors.SERVER_CODES int code, Response<Token> response, int webService) {
                spTokenListener.onSuccess(response.getItems().get(0));
            }

            @Override
            public void onError(@PixzelleErrors.SERVER_CODES int code, Response response, int webService) {
                spTokenListener.onError((SrPagoError)response.getItems().get(0));
            }
        }, params, null);
    }
}
