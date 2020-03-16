package com.onlyvtc.ui.activity.wallet;

import com.onlyvtc.base.MvpPresenter;

import java.util.HashMap;

public interface WalletIPresenter<V extends WalletIView> extends MvpPresenter<V> {
    void addMoney(HashMap<String, Object> obj);

    void getBrainTreeToken();
}
