package com.onlyvtc.ui.fragment.dispute;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.DisputeResponse;
import com.onlyvtc.data.network.model.Help;

import java.util.List;

public interface DisputeIView extends MvpView {

    void onSuccess(Object object);

    void onSuccessDispute(List<DisputeResponse> responseList);

    void onError(Throwable e);

    void onSuccess(Help help);
}
