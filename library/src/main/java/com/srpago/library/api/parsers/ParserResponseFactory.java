package com.srpago.library.api.parsers;

import android.content.Context;

import com.srpago.library.common.SPDefinitions;
import com.srpago.library.communication.PixzelleParserResponseFactory;
import com.srpago.library.communication.Response;
import com.srpago.library.model.SrPagoError;
import com.srpago.library.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Rodolfo on 15/05/2017.
 */

public class ParserResponseFactory extends PixzelleParserResponseFactory {
    @Override
    public Response parseResponse(Context context, int webService, String json) throws JSONException {
        Response<?> response = new Response<Object>();


        switch (webService) {
            case SPDefinitions.SP_ID_CREATE_TOKEN:
                response = new Response<Token>();
                parseToken(json, response);
                break;
        }

        response.setRaw(json);

        return response;
    }

    @SuppressWarnings("unchecked")
    private void parseToken(String json, Response response) throws JSONException {
        response.setItems(new ArrayList<Token>());

        final JSONObject root = new JSONObject(json);

        if(root.has("success") && root.getBoolean("success")){
            response.getItems().add(new Token(){{
                setToken(root.getJSONObject("result").getString("token"));
            }});
        }
    }

    @Override
    public Response parseError(String json) throws JSONException {
        Response<SrPagoError> error = new Response<>();
        error.setRaw(json);
        error.setItems(new ArrayList<SrPagoError>());
        error.getItems().add(SrPagoError.parseError(json));

        return error;
    }
}
