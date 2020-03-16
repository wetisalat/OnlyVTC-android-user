package com.onlyvtc.ui.activity.sms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onlyvtc.R;
import com.onlyvtc.base.BaseActivity;
import com.onlyvtc.data.network.model.ResponseSMS;
import com.onlyvtc.ui.countrypicker.Country;
import com.onlyvtc.ui.countrypicker.CountryPicker;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetSMSActivity extends BaseActivity implements GetSMSIView {

    @BindView(R.id.countryImage)
    ImageView countryImage;
    @BindView(R.id.countryNumber)
    TextView countryNumber;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;

    private String countryDialCode = "+33";
    private String countryCode = "FR";
    private String countryName = "France";
    private int countryFlag = R.drawable.flag_fr;
    private String email = "";
    private String phone = "";

    private CountryPicker mCountryPicker;

    private GetSMSPresenter<GetSMSActivity> presenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_getsms;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter = new GetSMSPresenter<>();
        presenter.attachView(this);
        setCountryList();
        setTitle(getString(R.string.enter_your_mobile_number));

    }

    private void setCountryList() {
        mCountryPicker = CountryPicker.newInstance("Select Country");
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        mCountryPicker.setCountriesList(countryList);
        if (getIntent().getExtras() != null && getIntent().getExtras().get("data") != null) {
            Bundle data = (Bundle) getIntent().getExtras().get("data");
            countryDialCode = data.getString("dialCode");
            countryCode = data.getString("code");
            countryName = data.getString("name");
            countryFlag = data.getInt("flag_id");
            email = data.getString("email");
            phone = data.getString("phone");

            Country country = new Country(countryCode, countryName, countryDialCode, countryFlag);
            setListener(country);

            phoneNumber.setText(phone);
        } else {
            finish();
        }
    }

    private void setListener(Country country) {
        mCountryPicker.setListener((name, code, dialCode, flagDrawableResID) -> {
            countryNumber.setText(dialCode);
            countryDialCode = dialCode;
            countryImage.setImageResource(flagDrawableResID);
            mCountryPicker.dismiss();
            countryFlag = flagDrawableResID;
        });

        countryImage.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER"));

        countryNumber.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER"));

        setUserCountryInfo(country);
    }

    private void setUserCountryInfo(Country country) {
        if (country == null) return;
        countryImage.setImageResource(country.getFlag());
        countryNumber.setText(country.getDialCode());
        countryDialCode = country.getDialCode();
        countryCode = country.getCode();
    }

    private void requestSMS() {
        Map<String, String> map = new HashMap<>();
        map.put("cellphone", phoneNumber.getText().toString());
        map.put("email", email);
        map.put("country_code", countryDialCode.substring(1));
        showLoading();
        presenter.requestSMS(map);
    }

    @OnClick({R.id.get_sms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.get_sms:
                requestSMS();
                break;
        }
    }

    @Override
    public void onSuccess(ResponseSMS result) {
        hideLoading();
        if (result.isSuccess()) {
            onCompleteGetSMS(result.getAuthy_id());
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

    private void onCompleteGetSMS(String authy_id) {
        Intent intent = new Intent(this, VerifySMSActivity.class);

        Bundle extras = new Bundle();
        extras.putString("code", countryCode);
        extras.putString("name", countryName);
        extras.putString("dialCode", countryDialCode);
        extras.putInt("flag_id", countryFlag);
        extras.putString("phone", phoneNumber.getText().toString());

        extras.putString("authy_id", authy_id);

        intent.putExtra("data", extras);
        startActivityForResult(intent, 2222);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2222 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.setFlags(RESULT_OK);
            intent.putExtra("verified_data", data.getExtras().getBundle("verified_data"));
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
