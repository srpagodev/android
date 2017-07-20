package com.srpago.library.model;

import org.json.JSONObject;

/**
 * Created by Rodolfo on 22/06/2017.
 */

public class SrPagoError {
    private String message;
    private String description;
    private String code;

    public static SrPagoError parseError(String json){
        SrPagoError spError;

        try{
            spError = new SrPagoError();
            JSONObject root = new JSONObject(json).getJSONObject("error");

            spError.setCode(root.has("code") ? root.getString("code") : "");
            spError.setDescription(root.has("description") ? root.getString("description") : "");
            spError.setMessage(root.has("message") ? root.getString("message") : "");
        }catch (Exception ex){
            spError = null;
        }

        return spError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
