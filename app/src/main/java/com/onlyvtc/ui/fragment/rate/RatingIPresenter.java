package com.onlyvtc.ui.fragment.rate;

import com.onlyvtc.base.MvpPresenter;

import java.util.HashMap;

public interface RatingIPresenter<V extends RatingIView> extends MvpPresenter<V> {

    void rate(HashMap<String, Object> obj);
}
