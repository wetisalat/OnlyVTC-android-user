package com.onlyvtc.base;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.onlyvtc.R;
import com.onlyvtc.common.Constants;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.onlyvtc.MvpApplication.RIDE_REQUEST;
import static com.onlyvtc.MvpApplication.isCard;
import static com.onlyvtc.MvpApplication.isCash;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.CARD_LAST_FOUR;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.PAYMENT_MODE;

public abstract class BaseFragment extends Fragment implements MvpView {

    private View view;
    private BaseActivity mBaseActivity;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container, false);
            initView(view);
        }

        return view;
    }

    protected abstract int getLayoutId();

    protected abstract View initView(View view);

    @Override
    public FragmentActivity baseActivity() {
        return getActivity();
    }

    @Override
    public void showLoading() {
        if (mBaseActivity != null) {
            mBaseActivity.showLoading();
        }
    }

    @Override
    public void hideLoading() {
        if (mBaseActivity != null) {
            mBaseActivity.hideLoading();
        }
    }

    protected void datePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(baseActivity(), dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    protected void timePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.mBaseActivity = (BaseActivity) context;
        }
    }

    protected void initPayment(TextView paymentMode) {
        if (RIDE_REQUEST.containsKey(PAYMENT_MODE))
            switch (RIDE_REQUEST.get(PAYMENT_MODE).toString()) {
                case Constants.PaymentMode.CASH:
                    paymentMode.setText(getString(R.string.cash));
                    //    paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                    break;
                case Constants.PaymentMode.CARD:
                    if (RIDE_REQUEST.containsKey(CARD_LAST_FOUR))
                        paymentMode.setText(getString(R.string.card_, RIDE_REQUEST.get("card_last_four")));
                    else paymentMode.setText(getString(R.string.add_card_));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                    break;
                case Constants.PaymentMode.PAYPAL:
                    paymentMode.setText(getString(R.string.paypal));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                    break;
                case Constants.PaymentMode.BRAINTREE:
                    paymentMode.setText(getString(R.string.braintree));
                    break;

                case Constants.PaymentMode.PAYTM:
                    paymentMode.setText(getString(R.string.paytm));
                    break;

                case Constants.PaymentMode.PAYUMONEY:
                    paymentMode.setText(getString(R.string.payumoney));
                    break;

                case Constants.PaymentMode.WALLET:
                    paymentMode.setText(getString(R.string.wallet));
                    break;
            }
        else {
            if (isCash) {
                RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.CASH);
                paymentMode.setText(getString(R.string.cash));
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
            } else if (isCard) {
                paymentMode.setText(R.string.add_card_);
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.CARD);
            }
        }
    }

    protected void onErrorBase(Throwable t) {
        if (mBaseActivity != null) {
            mBaseActivity.onErrorBase(t);
        }
    }

    protected void handleError(Throwable e) {
        try {
            try {
                hideLoading();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (mBaseActivity != null) {
            mBaseActivity.handleError(e);
        }
    }

    @Override
    public void onSuccessLogout(Object object) {
        if (mBaseActivity != null) {
            mBaseActivity.onSuccessLogout(object);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (mBaseActivity != null) {
            mBaseActivity.onError(throwable);
        }
    }

    protected String getNewNumberFormat(double d) {
        return BaseActivity.getNumberFormat().format(d);
    }

    protected JSONObject getAddressJson(LatLng latLng) {
        JSONObject json = new JSONObject();
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if ((addresses != null) && !addresses.isEmpty()) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                json.put("lat", latLng.latitude);
                json.put("lng", latLng.longitude);
                json.put("formatted_address", add);
                json.put("region_name", obj.getLocality());
                json.put("country", obj.getCountryName());
                json.put("address", add);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }


}
