package com.sgc.tictactoe.util;

import android.text.TextUtils;
import android.widget.EditText;

public class ValidateUtil{
        public static boolean validateEditTexts(EditText[] editTexts) {
            boolean valid = true;

            for(int i = 0 ; i < editTexts.length ; i++) {
                EditText editText = editTexts[i];
                String value = editText.getText().toString();

                if (TextUtils.isEmpty(value)) {
                    editText.setError("Зполните поле.");
                    valid = false;
                } else {
                    editText.setError(null);
                }
            }

            return valid;
        }
}
