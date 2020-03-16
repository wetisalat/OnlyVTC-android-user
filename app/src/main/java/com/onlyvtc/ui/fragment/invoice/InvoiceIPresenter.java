package com.onlyvtc.ui.fragment.invoice;

import com.onlyvtc.base.MvpPresenter;

import java.util.HashMap;

public interface InvoiceIPresenter<V extends InvoiceIView> extends MvpPresenter<V> {
    void payment(HashMap<String, Object> obj);

    void updateRide(HashMap<String, Object> obj);

    void payuMoneyChecksum();

    void getBrainTreeToken();

    void updatePayment(HashMap<String, Object> obj);


}
