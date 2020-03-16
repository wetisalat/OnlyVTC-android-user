package com.onlyvtc.ui.activity.passbook;

import com.onlyvtc.base.MvpPresenter;

public interface WalletHistoryIPresenter<V extends WalletHistoryIView> extends MvpPresenter<V> {
    void wallet();
}
