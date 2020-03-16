package com.onlyvtc.ui.activity.wallet;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.AddWallet;
import com.onlyvtc.data.network.model.BrainTreeResponse;

public interface WalletIView extends MvpView {
    void onSuccess(AddWallet object);

    void onSuccess(BrainTreeResponse response);

    void onError(Throwable e);
}
