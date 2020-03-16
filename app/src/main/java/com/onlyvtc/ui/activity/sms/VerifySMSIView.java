package com.onlyvtc.ui.activity.sms;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.ResponseSMS;

public interface VerifySMSIView extends MvpView {

    void onSuccess(ResponseSMS result);

    void onError(Throwable e);

}
