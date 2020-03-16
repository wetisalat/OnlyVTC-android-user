package com.onlyvtc.ui.activity.sms;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.ResponseSMS;

public interface GetSMSIView extends MvpView {

    void onSuccess(ResponseSMS result);

    void onError(Throwable e);

}
