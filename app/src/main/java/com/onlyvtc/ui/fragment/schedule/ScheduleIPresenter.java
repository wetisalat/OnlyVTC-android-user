package com.onlyvtc.ui.fragment.schedule;

import com.onlyvtc.base.MvpPresenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ScheduleIPresenter<V extends ScheduleIView> extends MvpPresenter<V> {
    void sendRequest(HashMap<String, Object> obj);

    void sendRequest(HashMap<String, Object> obj, ArrayList<Integer> repeat);
}
