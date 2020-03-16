package com.onlyvtc.ui.activity.help;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.onlyvtc.BuildConfig;
import com.onlyvtc.R;
import com.onlyvtc.base.BaseActivity;
import com.onlyvtc.data.network.model.Help;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends BaseActivity implements HelpIView {

    private String ContactNumber = null;
    private String Mail = null;
    private HelpPresenter<HelpActivity> presenter = new HelpPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_help;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        setTitle(getString(R.string.help));
        presenter.help();
    }

    @Override
    public void onSuccess(Help help) {
        ContactNumber = help.getContactNumber();
        Mail = help.getContactEmail();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    private void callContactNumber() {

        Dexter.withActivity(HelpActivity.this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        if (ContactNumber != null) {
                            if (ActivityCompat.checkSelfPermission(HelpActivity.this, Manifest.permission.CALL_PHONE)
                                    == PackageManager.PERMISSION_GRANTED)
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ContactNumber)));
                            else
                                ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE);
                        }

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }

                }).check();

    }

    private void sendMail() {
        if (Mail != null) {
            String appName = getString(R.string.app_name) + " " + getString(R.string.help);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: " + Mail));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
        }
    }

    @OnClick({R.id.call, R.id.mail, R.id.web})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.call:
                callContactNumber();
                break;
            case R.id.mail:
                sendMail();
                break;
            case R.id.web:
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(BuildConfig.BASE_URL)));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_PHONE)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                callContactNumber();
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
