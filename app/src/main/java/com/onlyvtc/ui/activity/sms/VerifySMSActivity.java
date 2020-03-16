package com.onlyvtc.ui.activity.sms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.onlyvtc.R;
import com.onlyvtc.base.BaseActivity;
import com.onlyvtc.data.network.model.ResponseSMS;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifySMSActivity extends BaseActivity implements VerifySMSIView {

    @BindView(R.id.auth_token)
    EditText auth_token;

    private String countryDialCode = "+33";
    private String countryCode = "FR";
    private String countryName = "France";
    private int countryFlag = R.drawable.flag_fr;
    private String phone = "";
    private String authy_id = "";

    private VerifySMSPresenter<VerifySMSActivity> presenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_verifysms;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter = new VerifySMSPresenter<>();
        presenter.attachView(this);
        setTitle(getString(R.string.verify_sms));

        if (getIntent().getExtras() != null && getIntent().getExtras().get("data") != null) {
            Bundle data = (Bundle) getIntent().getExtras().get("data");
            countryDialCode = data.getString("dialCode");
            countryCode = data.getString("code");
            countryName = data.getString("name");
            countryFlag = data.getInt("flag_id");

            phone = data.getString("phone");

            authy_id = data.getString("authy_id");

        } else {
            finish();
        }
    }

    private void verifySMS() {
        Map<String, String> map = new HashMap<>();
        map.put("authy_id", authy_id);
        map.put("auth_token", auth_token.getText().toString());
        showLoading();
        presenter.verifySMS(map);
    }

    @OnClick({R.id.verify_sms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.verify_sms:
                verifySMS();
                break;
        }
    }

    @Override
    public void onSuccess(ResponseSMS result) {
        hideLoading();
        if (result.isSuccess()) {
            onCompleteVerified();
        } else {
            Toast.makeText(this, R.string.some_thing_wrong, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        Toast.makeText(this, R.string.some_thing_wrong, Toast.LENGTH_SHORT).show();
//        if (e != null)
//            onErrorBase(e);
    }

    private void onCompleteVerified() {
        Intent intent = getIntent();
        intent.setFlags(RESULT_OK);

        Bundle extras = new Bundle();
        extras.putString("code", countryCode);
        extras.putString("name", countryName);
        extras.putString("dialCode", countryDialCode);
        extras.putInt("flag_id", countryFlag);
        extras.putString("phone", phone);
        intent.putExtra("verified_data", extras);
        setResult(RESULT_OK, intent);

        finish();
    }

}
