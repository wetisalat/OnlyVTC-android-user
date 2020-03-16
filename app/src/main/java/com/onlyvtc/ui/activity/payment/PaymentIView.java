package com.onlyvtc.ui.activity.payment;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.BrainTreeResponse;
import com.onlyvtc.data.network.model.Card;
import com.onlyvtc.data.network.model.CheckSumData;

import java.util.List;

public interface PaymentIView extends MvpView {

    void onSuccess(Object card);

    void onSuccess(BrainTreeResponse response);

    void onSuccess(List<Card> cards);

    void onAddCardSuccess(Object cards);

    void onError(Throwable e);

    void onPayumoneyCheckSumSucess(CheckSumData checkSumData);

}
