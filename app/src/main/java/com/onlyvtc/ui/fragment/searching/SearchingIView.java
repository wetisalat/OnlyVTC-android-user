package com.onlyvtc.ui.fragment.searching;

import com.onlyvtc.base.MvpView;

public interface SearchingIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
