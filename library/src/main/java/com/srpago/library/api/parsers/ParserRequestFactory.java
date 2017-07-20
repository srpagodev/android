package com.srpago.library.api.parsers;

import android.content.Context;


import com.srpago.library.api.Encryptor;
import com.srpago.library.common.SPDefinitions;
import com.srpago.library.communication.PixzelleParserRequestFactory;
import com.srpago.library.model.Card;

import org.json.JSONObject;


/**
 * Created by Rodolfo on 15/05/2017.
 */

public class ParserRequestFactory extends PixzelleParserRequestFactory {
    @Override
    public String convertToJSON(Context context, int webService, Object... params) throws Exception {
        switch (webService) {
            case SPDefinitions.SP_ID_CREATE_TOKEN:
                return parseCard((Card) params[0]);

        }

        return null;
    }

    private String parseCard(Card spCard) {
        JSONObject jsonObject;
        String data = null;
        int retry = 0;
        if (spCard != null) {
            do {
                String key;
                key = Encryptor.getRandomKey();
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("key", Encryptor.rsaEncrypt(key).replaceAll("\t", "").replaceAll(" ", "").replaceAll("\r", ""));
                    data = Encryptor.aesEncrypt(spCard.toJSON(), key).replaceAll("\t", "").replaceAll(" ", "").replaceAll("\r", "");
                    jsonObject.put("data", data);
                } catch (Exception ex) {
                    data = null;
                }
                retry ++;
            } while (data == null || data.equals("") || retry < 100);
            return jsonObject.toString();
        }

        return "";
    }
}