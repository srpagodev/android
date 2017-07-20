package com.srpago.library;


import com.srpago.library.model.SrPagoError;
import com.srpago.library.model.Token;

/**
 * Created by Rodolfo on 22/06/2017.
 */

public interface SpTokenListener {
    void onSuccess(Token spToken);
    void onError(SrPagoError error);
}
