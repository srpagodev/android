package com.srpago.library.communication;

import android.content.Context;

/**
 * Created by Rodolfo on 04/09/2015 for GronJobb.
 * Pixzelle Studio S. de R.L. All rights reserved.
 */
public abstract class PixzelleParserRequestFactory {
    public abstract String convertToJSON(Context context, int webService, Object... params) throws Exception;
}
