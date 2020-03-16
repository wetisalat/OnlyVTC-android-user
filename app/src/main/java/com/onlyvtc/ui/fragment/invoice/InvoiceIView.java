package com.onlyvtc.ui.fragment.invoice;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.BrainTreeResponse;
import com.onlyvtc.data.network.model.CheckSumData;
import com.onlyvtc.data.network.model.Message;

public interface InvoiceIView extends MvpView {
    void onSuccess(Message message);

    void onSuccess(Object o);

    void onSuccessPayment(Object o);

    void onError(Throwable e);

    void onSuccess(BrainTreeResponse response);

    void onPayumoneyCheckSumSucess(CheckSumData checkSumData);
}
