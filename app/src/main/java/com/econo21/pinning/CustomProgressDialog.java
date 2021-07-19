package com.econo21.pinning;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(Context context){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
    }
}
