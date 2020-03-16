package com.onlyvtc.ui.activity.passbook;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);

    void onError(Throwable e);
}
