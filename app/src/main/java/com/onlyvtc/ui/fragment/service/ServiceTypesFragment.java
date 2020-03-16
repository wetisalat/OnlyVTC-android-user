package com.onlyvtc.ui.fragment.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.LogUtil;
import com.onlyvtc.R;
import com.onlyvtc.base.BaseActivity;
import com.onlyvtc.base.BaseFragment;
import com.onlyvtc.common.EqualSpacingItemDecoration;
import com.onlyvtc.common.InfoWindowData;
import com.onlyvtc.data.SharedHelper;
import com.onlyvtc.data.network.APIClient;
import com.onlyvtc.data.network.model.Datum;
import com.onlyvtc.data.network.model.EstimateFare;
import com.onlyvtc.data.network.model.Provider;
import com.onlyvtc.data.network.model.Service;
import com.onlyvtc.ui.activity.main.MainActivity;
import com.onlyvtc.ui.activity.payment.PaymentActivity;
import com.onlyvtc.ui.adapter.ServiceAdapter;
import com.onlyvtc.ui.fragment.RateCardFragment;
import com.onlyvtc.ui.fragment.book_ride.BookRideFragment;
import com.onlyvtc.ui.fragment.schedule.ScheduleFragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.onlyvtc.MvpApplication.RIDE_REQUEST;
import static com.onlyvtc.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.CALCULATE_STATE;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.CARD_ID;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.CARD_LAST_FOUR;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.DEST_LONG;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.DISTANCE_VAL;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.SERVICE_TYPE;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.SRC_LONG;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.STOP_ADD;
import static com.onlyvtc.common.Constants.RIDE_REQUEST.TOTAL_PRICE;
import static com.onlyvtc.data.SharedHelper.getKey;
import static com.onlyvtc.data.SharedHelper.getProviders;
import static com.onlyvtc.data.SharedHelper.putKey;
import static com.onlyvtc.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class ServiceTypesFragment extends BaseFragment implements ServiceTypesIView {

    @BindView(R.id.service_rv)
    RecyclerView serviceRv;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.error_layout)
    TextView errorLayout;
    Unbinder unbinder;
    ServiceAdapter adapter;
    List<Service> mServices = new ArrayList<>();
    @BindView(R.id.use_wallet)
    CheckBox useWallet;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.surge_value)
    TextView surgeValue;
    @BindView(R.id.tv_demand)
    TextView tvDemand;

    private ServiceTypesPresenter<ServiceTypesFragment> presenter = new ServiceTypesPresenter<>();
    private boolean isFromAdapter = true;
    private int servicePos = 0;
    private EstimateFare mEstimateFare;
    private double walletAmount;
    private int surge;

    private ServiceListener mListener = pos -> {
        isFromAdapter = true;
        servicePos = pos;
        String key = mServices.get(pos).getName() + mServices.get(pos).getId();
        RIDE_REQUEST.put(SERVICE_TYPE, mServices.get(pos).getId());
        showLoading();
        estimatedApiCall(mServices.get(pos));
        List<Provider> providers = new ArrayList<>();
        if (getProviders(Objects.requireNonNull(getActivity())) != null) {
            for (Provider provider : getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId() == mServices.get(pos).getId())
                    providers.add(provider);
        }

        ((MainActivity) getActivity()).addSpecificProviders(providers, key);
    };
    private InfoWindowData route_info;
    private String distance;
    private String current_time;
    private int hours;
    private int minutes;

    public ServiceTypesFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
//        presenter.services();   // old one

        Bundle args = getArguments();
        if (args != null)
            route_info = (InfoWindowData) args.getSerializable("route_info");

        distance = String.format("%.1f", (route_info.getDistance_val() / 1000.0));

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        current_time = df.format(Calendar.getInstance().getTime());
        int totalMinutes = route_info.getArrival_time_val() / 60;
        hours = totalMinutes / 60;
        minutes = totalMinutes % 60;

        if (RIDE_REQUEST.containsKey(STOP_ADD)) {
//            Toast.makeText(getActivity(), "false", Toast.LENGTH_SHORT).show();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("passenger", "1");
            hashMap.put("suitcase", "0");
            hashMap.put("vehicle_type", "0");
            hashMap.put("total_kilometer", distance);
            hashMap.put("total_hours", hours);
            hashMap.put("total_minutes", minutes);
            hashMap.put("start_time", current_time);
            hashMap.put("chbs_service_type_id", "1");
            presenter.getServiceType(hashMap);

        } else {
//            Toast.makeText(getActivity(), "true", Toast.LENGTH_SHORT).show();
            HashMap<String, Object> map = new HashMap<>();
            map.put("start_location_lat", RIDE_REQUEST.get(SRC_LAT));
            map.put("start_location_lng", RIDE_REQUEST.get(SRC_LONG));
            map.put("dest_location_lat", RIDE_REQUEST.get(DEST_LAT));
            map.put("dest_location_lng", RIDE_REQUEST.get(DEST_LONG));
            Log.d("ServiceTypesFragment", "SERVICE==>>>" + map.toString());

            presenter.checkPoiPriceLogic(map);
        }
        return view;
    }

    @OnClick({R.id.payment_type, R.id.get_pricing, R.id.schedule_ride, R.id.ride_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_type:
                ((MainActivity) Objects.requireNonNull(getActivity())).updatePaymentEntities();
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
            case R.id.get_pricing:
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null) {
                        RIDE_REQUEST.put(SERVICE_TYPE, service.getId());
                        if (RIDE_REQUEST.containsKey(SERVICE_TYPE) && RIDE_REQUEST.get(SERVICE_TYPE) != null) {
                            showLoading();
                            estimatedApiCall(service);
                        }
                    }
                }
                break;
            case R.id.schedule_ride:
                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:
                sendRequest();
                break;
            default:
                break;
        }
    }

    private void estimatedApiCall(Service selectedService) {
        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(RIDE_REQUEST);
        call.enqueue(new Callback<EstimateFare>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EstimateFare> call,
                                   @NonNull Response<EstimateFare> response) {
//                if (ServiceTypesFragment.this.isVisible()) {
                hideLoading();
                if (response.body() != null) {
                    EstimateFare estimateFare = response.body();
                    estimateFare.setEstimatedFare(selectedService.getFixed());
                    try {
                        estimateFare.setDistance(Double.parseDouble(distance));
                    } catch (Exception e) {}
                    estimateFare.setService(selectedService);
                    estimateFare.setTime(route_info.getArrival_time());

                    RateCardFragment.SERVICE = estimateFare.getService();
                    mEstimateFare = estimateFare;
                    surge = estimateFare.getSurge();
                    walletAmount = estimateFare.getWalletBalance();

                    if (getContext() != null)
                        putKey(getContext(), "wallet", String.valueOf(estimateFare.getWalletBalance()));
                    if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
                    else {
                        walletBalance.setVisibility(View.VISIBLE);
                        walletBalance.setText(getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
                    }
                    if (surge == 0) {
                        surgeValue.setVisibility(View.GONE);
                        tvDemand.setVisibility(View.GONE);
                    } else {
                        surgeValue.setVisibility(View.VISIBLE);
                        surgeValue.setText(estimateFare.getSurgeValue());
                        tvDemand.setVisibility(View.VISIBLE);
                    }
                    if (isFromAdapter) {
                        mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());
                        RIDE_REQUEST.put(DISTANCE_VAL, estimateFare.getDistance());
                        RIDE_REQUEST.put(TOTAL_PRICE, selectedService.getFixed());
                        adapter.setEstimateFare(mEstimateFare);
                        adapter.notifyDataSetChanged();
                        if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                        else errorLayout.setVisibility(View.GONE);
                    } else if (adapter != null) {
                        Service service = adapter.getSelectedService();
                        if (service != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("service_name", service.getName());
                            bundle.putSerializable("mService", service);
                            bundle.putSerializable("estimate_fare", estimateFare);
                            bundle.putDouble("use_wallet", walletAmount);
                            BookRideFragment bookRideFragment = new BookRideFragment();
                            bookRideFragment.setArguments(bundle);
                            ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);
                        }
                    }
                } else if (response.raw().code() == 500) try {
                    JSONObject object = new JSONObject(response.errorBody().string());
                    if (object.has("error"))
                        Toast.makeText(baseActivity(), object.optString("error"), Toast.LENGTH_SHORT).show();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
//                }
            }

            @Override
            public void onFailure(@NonNull Call<EstimateFare> call, @NonNull Throwable t) {
                onErrorBase(t);
                hideLoading();
            }
        });
    }

    @Override
    public void onSuccess(List<Service> services) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (services != null && !services.isEmpty()) {
            RIDE_REQUEST.put(SERVICE_TYPE, 1);
            mServices.clear();
            mServices.addAll(services);

            try {
                AsyncTask.execute(() -> {
                    for (Service s : mServices) {
                        String key = s.getName() + s.getId();
                        if (!TextUtils.isEmpty(s.getMarker()))
                            if (TextUtils.isEmpty(getKey(Objects.requireNonNull(getActivity()), key))) {
                                Bitmap b = ((BaseActivity) getActivity()).getBitmapFromURL(s.getMarker());
                                if (b != null)
                                    putKey(getActivity(), key, ((BaseActivity) getActivity()).encodeBase64(b));
                            }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            adapter = new ServiceAdapter(getActivity(), mServices, mListener, capacity, mEstimateFare);
            serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            serviceRv.setItemAnimator(new DefaultItemAnimator());
            serviceRv.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            serviceRv.setAdapter(adapter);

            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) RIDE_REQUEST.put(SERVICE_TYPE, mService.getId());
            }
            mListener.whenClicked(0);
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put(PAYMENT_MODE, data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put(CARD_ID, data.getStringExtra("card_id"));
                RIDE_REQUEST.put(CARD_LAST_FOUR, data.getStringExtra("card_last_four"));
            }
            initPayment(paymentType);
        }
    }

    private void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        showLoading();
        presenter.rideNow(map);
    }

    @Override
    public void onSuccess(@NonNull Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
    }

    @Override
    public void onSuccessPriceLogic(Datum datum) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("passenger", "1");
        hashMap.put("suitcase", "0");
        hashMap.put("vehicle_type", "0");
        hashMap.put("total_kilometer", distance);
        hashMap.put("total_hours", hours);
        hashMap.put("total_minutes", minutes);
        hashMap.put("start_time", current_time);

        if (datum.getStatus().equalsIgnoreCase("true")) {
            hashMap.put("poiPrice", datum.getPrice());
            hashMap.put("Poi_service_type_id", datum.getServiceTypeId());
            hashMap.put("poi_service_id", datum.getServiceId());
            hashMap.put("_token", SharedHelper.getKey(getActivity(), "device_token"));

            presenter.checkPoiDistanceInfo(hashMap);

        } else {
            hashMap.put("chbs_service_type_id", "1");
            presenter.getServiceType(hashMap);
        }

        Log.d("ServiceTypesFragment", "check==>>" + hashMap.toString());

    }

    @Override
    public void onSuccessPoiDistance(List<Service> services) {
        if (services == null)
            Log.d("ServiceTypesFragment", "null");
        else
            Log.d("ServiceTypesFragment", "test check service==>>" + services.size());

        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (services != null && !services.isEmpty()) {
            RIDE_REQUEST.put(SERVICE_TYPE, 1);
            RIDE_REQUEST.put(CALCULATE_STATE, "poi");
            mServices.clear();
            mServices.addAll(services);
            Log.d("ServiceTypesFragment", "check service==>>" + mServices.size());

            try {
                AsyncTask.execute(() -> {
                    for (Service s : mServices) {
                        String key = s.getName() + s.getId();
                        if (!TextUtils.isEmpty(s.getMarker()))
                            if (TextUtils.isEmpty(getKey(Objects.requireNonNull(getActivity()), key))) {
                                Bitmap b = ((BaseActivity) getActivity()).getBitmapFromURL(s.getMarker());
                                if (b != null)
                                    putKey(getActivity(), key, ((BaseActivity) getActivity()).encodeBase64(b));
                            }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            adapter = new ServiceAdapter(getActivity(), mServices, mListener, capacity, mEstimateFare);
            serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            serviceRv.setItemAnimator(new DefaultItemAnimator());
            serviceRv.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            serviceRv.setAdapter(adapter);

            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) RIDE_REQUEST.put(SERVICE_TYPE, mService.getId());
            }
            mListener.whenClicked(0);
        }
    }

    @Override
    public void onSuccessService(List<Service> serviceList) {
        Log.d("ServiceTypesFragment", "test check service==>> 2  " + mServices.size());

        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (serviceList != null && !serviceList.isEmpty()) {
            RIDE_REQUEST.put(SERVICE_TYPE, 1);
            RIDE_REQUEST.put(CALCULATE_STATE, "distance");
            mServices.clear();
            mServices.addAll(serviceList);
            Log.d("ServiceTypesFragment", "check service==>> 2  " + mServices.size());


            try {
                AsyncTask.execute(() -> {
                    for (Service s : mServices) {
                        String key = s.getName() + s.getId();
                        if (!TextUtils.isEmpty(s.getMarker()))
                            if (TextUtils.isEmpty(getKey(Objects.requireNonNull(getActivity()), key))) {
                                Bitmap b = ((BaseActivity) getActivity()).getBitmapFromURL(s.getMarker());
                                if (b != null)
                                    putKey(getActivity(), key, ((BaseActivity) getActivity()).encodeBase64(b));
                            }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            adapter = new ServiceAdapter(getActivity(), mServices, mListener, capacity, mEstimateFare);
            serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            serviceRv.setItemAnimator(new DefaultItemAnimator());
            serviceRv.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            serviceRv.setAdapter(adapter);

            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) RIDE_REQUEST.put(SERVICE_TYPE, mService.getId());
            }
            mListener.whenClicked(0);
        }

    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPayment(paymentType);
    }

    public interface ServiceListener {
        void whenClicked(int pos);
    }

}
