package com.onlyvtc.ui.activity.location_pick;

import com.onlyvtc.base.MvpPresenter;

public interface LocationPickIPresenter<V extends LocationPickIView> extends MvpPresenter<V> {
    void address();
}
