package com.onlyvtc.ui.activity.notification_manager;

import com.onlyvtc.base.MvpPresenter;

public interface NotificationManagerIPresenter<V extends NotificationManagerIView> extends MvpPresenter<V> {
    void getNotificationManager();
}
