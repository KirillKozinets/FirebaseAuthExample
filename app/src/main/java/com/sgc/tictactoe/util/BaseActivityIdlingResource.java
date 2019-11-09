package com.sgc.tictactoe.util;

import android.app.ProgressDialog;

import com.sgc.tictactoe.ui.BaseActivity;
import com.sgc.tictactoe.ui.EntryActivity;
import com.sgc.tictactoe.ui.RegistrationActivity;

import androidx.test.espresso.IdlingResource;

public class BaseActivityIdlingResource implements IdlingResource {

    private BaseActivity mActivity;
    private ResourceCallback mCallback;


    public BaseActivityIdlingResource(EntryActivity activity) {
        mActivity = activity;
    }

    public BaseActivityIdlingResource(RegistrationActivity activity) {
        mActivity = activity;
    }

    @Override
    public String getName() {
        return "BaseActivityIdlingResource:" + mActivity.getLocalClassName();
    }

    @Override
    public boolean isIdleNow() {
        ProgressDialog dialog = mActivity.mProgressDialog;
        boolean idle = (dialog == null || !dialog.isShowing());

        if (mCallback != null && idle) {
            mCallback.onTransitionToIdle();
        }

        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }
}