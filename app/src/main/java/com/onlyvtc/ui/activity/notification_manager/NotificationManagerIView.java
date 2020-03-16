package com.onlyvtc.ui.activity.notification_manager;

import com.onlyvtc.base.MvpView;
import com.onlyvtc.data.network.model.NotificationManager;

import java.util.List;

public interface NotificationManagerIView extends MvpView {

    void onSuccess(List<NotificationManager> notificationManager);

    void onError(Throwable e);

}