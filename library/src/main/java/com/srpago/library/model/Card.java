package com.srpago.library.model;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Rodolfo on 22/06/2017.
 */

public class Card {
    private String name;
    private String number;
    private String expMonth;
    private String expYear;
    private String cvv;

    //Posible validation responses
    public String VALID_CARD;
    public String INVALID_NUMBER = "Numero de Tarjeta Invalido";
    public String INVALID_CVV = "CVV invalido";
    public String INVALID_EXPIRATION_YEAR = "El año es requerido, el año debe ser entre el año actual y 40";
    public String INVALID_EXPIRATION_MONTH = "El mes es requerido, el mes debe ser entre 1 - 12";
    public String INVALID_EXPIRATION_DATE = "La fecha es inválida";
    public String INVALID_EXPIRATION;

    //Posible CardTypes
    public static String UNKNOWN_CARD = "TIPO INVALIDO";
    public static String AMEX_CARD = "AMEX";
    public static String VISA_CARD = "VISA";
    public static String MAST_CARD = "MAST";


    public String toJSON() {
        String json;
        try {
            JSONObject root = new JSONObject();

            root.put("cardholder_name", this.name);
            root.put("number", this.number);
            root.put("expiration", String.format(Locale.getDefault(), "%s%s", this.expYear, this.expMonth));
            root.put("cvv", this.cvv);

            json = root.toString();
        } catch (Exception ex) {
            json = "";
        }
        return json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }


    public String validateCard() {

        if (!isValidCarNumber(this.getNumber())) {
            return INVALID_NUMBER;
        } else if (!isValidCVV(this.getCvv())) {
            return INVALID_CVV;
        } else if (!isValidExpiration(this.getExpYear(), this.getExpMonth())) {
            return INVALID_EXPIRATION;
        } else {
            return VALID_CARD;
        }

    }

    //Methods to validate Type and Number

    public boolean isValidCarNumber(String cardNUmber) {

        return isValidLuhnNumber(cardNUmber) && isValidCardLength(cardNUmber);

    }

    public boolean isValidLuhnNumber(@Nullable String cardNumber) {
        if (cardNumber == null) {
            return false;
        }

        boolean isOdd = true;
        int sum = 0;

        for (int index = cardNumber.length() - 1; index >= 0; index--) {
            char c = cardNumber.charAt(index);
            if (!Character.isDigit(c)) {
                return false;
            }

            int digitInteger = Character.getNumericValue(c);
            isOdd = !isOdd;

            if (isOdd) {
                digitInteger *= 2;
            }

            if (digitInteger > 9) {
                digitInteger -= 9;
            }

            sum += digitInteger;
        }

        return sum % 10 == 0;
    }

    public boolean isValidCardLength(@Nullable String cardNumber) {
        if (cardNumber == null || getPossibleCardType(cardNumber).equals(UNKNOWN_CARD)) {
            return false;
        }

        int length = cardNumber.length();
        if (length >= 15 && length <= 16) {
            return true;
        } else {
            return false;
        }
    }

    public String getPossibleCardType(@Nullable String cardNumber) {
        if (cardNumber == null) {
            return UNKNOWN_CARD;
        }

        int cardPrefix = Integer.parseInt(cardNumber.substring(0, 2));

        if (cardPrefix >= 34 && cardPrefix <= 37) {
            VALID_CARD = AMEX_CARD;
            return AMEX_CARD;
        } else if (cardPrefix >= 40 && cardPrefix <= 49) {
            VALID_CARD = VISA_CARD;
            return VISA_CARD;
        } else if (cardPrefix >= 50 && cardPrefix <= 59) {
            VALID_CARD = MAST_CARD;
            return MAST_CARD;
        } else if (cardPrefix >= 22 && cardPrefix <= 27) {
            VALID_CARD = MAST_CARD;
            return MAST_CARD;
        } else {
            return UNKNOWN_CARD;
        }
    }

    //Method to calidate CVV
    public boolean isValidCVV(String cvv) {

        if (cvv.length() >= 3 && cvv.length() <= 4) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isValidExpiration(String Year, String Month) {

        int expYear = Integer.parseInt(Year);
        int expMonth = Integer.parseInt(Month);

        Calendar calendar = Calendar.getInstance();
        int actualMonth = calendar.get(Calendar.MONTH);
        int actualYear = calendar.get(Calendar.YEAR) - 2000;

        if (expMonth < 1 || expMonth > 12) {
            INVALID_EXPIRATION = INVALID_EXPIRATION_MONTH;
            return false;
        } else if (expYear < actualYear || expYear > 40) {
            INVALID_EXPIRATION = INVALID_EXPIRATION_YEAR;
            return false;
        } else if ((expYear < actualYear || expYear > 35) || (expMonth < actualMonth || expMonth > 12)) {
            INVALID_EXPIRATION = INVALID_EXPIRATION_DATE;
            return false;
        } else {
            return true;
        }

    }

}
