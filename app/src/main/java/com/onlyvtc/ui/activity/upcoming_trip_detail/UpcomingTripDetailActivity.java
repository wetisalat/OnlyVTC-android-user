package com.onlyvtc.ui.activity.upcoming_trip_detail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onlyvtc.R;
import com.onlyvtc.base.BaseActivity;
import com.onlyvtc.common.CancelRequestInterface;
import com.onlyvtc.data.network.model.Datum;
import com.onlyvtc.data.network.model.Provider;
import com.onlyvtc.ui.activity.zoom_view.ZoomPhotoActivity;
import com.onlyvtc.ui.fragment.cancel_ride.CancelRideDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.onlyvtc.MvpApplication.DATUM;

public class UpcomingTripDetailActivity extends BaseActivity implements
        CancelRequestInterface, UpcomingTripDetailsIView {

    @BindView(R.id.static_map)
    ImageView staticMap;
    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.schedule_at)
    TextView scheduleAt;
    @BindView(R.id.s_address)
    TextView sAddress;
    @BindView(R.id.d_address)
    TextView dAddress;
    @BindView(R.id.stop_address)
    TextView stopAddress;
    @BindView(R.id.payment_mode)
    TextView paymentMode;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.call)
    Button call;
    @BindView(R.id.payment_image)
    ImageView paymentImage;
    @BindView(R.id.checkbox0)
    CheckBox checkbox0;
    @BindView(R.id.checkbox1)
    CheckBox checkbox1;
    @BindView(R.id.checkbox2)
    CheckBox checkbox2;
    @BindView(R.id.checkbox3)
    CheckBox checkbox3;
    @BindView(R.id.checkbox4)
    CheckBox checkbox4;
    @BindView(R.id.checkbox5)
    CheckBox checkbox5;
    @BindView(R.id.checkbox6)
    CheckBox checkbox6;
    @BindView(R.id.sep0)
    View sep0;
    @BindView(R.id.sep1)
    View sep1;
    @BindView(R.id.sep2)
    View sep2;
    @BindView(R.id.sep3)
    View sep3;
    @BindView(R.id.sep4)
    View sep4;
    @BindView(R.id.sep5)
    View sep5;
    @BindView(R.id.sep6)
    View sep6;
    @BindView(R.id.payable)
    TextView payable;
    String providerPhoneNumber = null;
    String providerImgUrl = "";
    private CancelRequestInterface callback;
    private UpcomingTripDetailsPresenter<UpcomingTripDetailActivity> presenter = new UpcomingTripDetailsPresenter();

    @Override
    public int getLayoutId() {
        return R.layout.activity_upcoming_trip_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        setTitle(getString(R.string.upcoming_trip_details));

        callback = this;
        if (DATUM != null) {
            Datum datum = DATUM;
            showLoading();
            presenter.getUpcomingTripDetails(datum.getId());
        }
    }

    @OnClick({R.id.cancel, R.id.call, R.id.avatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                CancelRideDialogFragment cancelRideDialogFragment = new CancelRideDialogFragment(callback);
                cancelRideDialogFragment.show(getSupportFragmentManager(), cancelRideDialogFragment.getTag());
                break;
            case R.id.call:
                callProvider();
                break;
            case R.id.avatar:
                Intent intent = new Intent(UpcomingTripDetailActivity.this, ZoomPhotoActivity.class);
                intent.putExtra(ZoomPhotoActivity.URL, providerImgUrl);
                startActivity(intent);
                break;
        }
    }

    private void callProvider() {
        if (providerPhoneNumber != null && !providerPhoneNumber.isEmpty()) {
            if (ActivityCompat.checkSelfPermission(baseActivity(),
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + providerPhoneNumber)));
            else
                ActivityCompat.requestPermissions(baseActivity(),
                        new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE);
        }
    }

    @Override
    public void cancelRequestMethod() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_PHONE)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(baseActivity(), "Permission Granted. Try Again!", Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public void onSuccess(List<Datum> upcomingTripDetails) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Datum datum = upcomingTripDetails.get(0);
        if (datum != null) {
            bookingId.setText(datum.getBookingId());
            String strCurrentDate = datum.getScheduleAt();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
            SimpleDateFormat timeFormat;
            Date newDate;
            try {
                newDate = format.parse(strCurrentDate);
                format = new SimpleDateFormat("dd-MM-yyyy");
                timeFormat = new SimpleDateFormat("HH:mm:ss");
                String date = format.format(newDate);
                String time = timeFormat.format(newDate);
                scheduleAt.setText(date + " " + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            scheduleAt.setText(datum.getScheduleAt());
            Glide.with(this)
                    .load(datum.getStaticMap())
                    .apply(RequestOptions
                            .placeholderOf(R.drawable.ic_launcher_background)
                            .dontAnimate()
                            .error(R.drawable.ic_launcher_background))
                    .into(staticMap);
            initPayment(datum.getPaymentMode(), paymentMode, paymentImage);
            sAddress.setText(datum.getSAddress());
            dAddress.setText(datum.getDAddress());

            if (datum.getWay_points() != null) {
                try {
                    stopAddress.setVisibility(View.VISIBLE);
                    JSONArray jsonArray = new JSONArray(datum.getWay_points());
                    if (jsonArray.length() > 0)
                        stopAddress.setText(jsonArray.getJSONObject(0).get("address").toString());
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }


            Provider provider = datum.getProvider();
            if (provider != null) {
                providerPhoneNumber = provider.getCountryCode() + provider.getMobile();
                Glide.with(this)
                        .load(provider.getAvatar())
                        .apply(RequestOptions
                                .placeholderOf(R.drawable.ic_user_placeholder)
                                .dontAnimate()
                                .error(R.drawable.ic_user_placeholder))
                        .into(avatar);
                name.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
                rating.setRating(Float.parseFloat(provider.getRating()));
                providerImgUrl = provider.getAvatar();
            }
            if (datum.getRepeated_date() == null) {
                checkbox0.setVisibility(View.GONE);
                sep0.setVisibility(View.GONE);
                checkbox1.setVisibility(View.GONE);
                sep1.setVisibility(View.GONE);
                checkbox2.setVisibility(View.GONE);
                sep2.setVisibility(View.GONE);
                checkbox3.setVisibility(View.GONE);
                sep3.setVisibility(View.GONE);
                checkbox4.setVisibility(View.GONE);
                sep4.setVisibility(View.GONE);
                checkbox5.setVisibility(View.GONE);
                sep5.setVisibility(View.GONE);
                checkbox6.setVisibility(View.GONE);
                sep6.setVisibility(View.GONE);

            } else {
                checkbox0.setChecked(datum.getRepeated().contains("0"));
                checkbox1.setChecked(datum.getRepeated().contains("1"));
                checkbox2.setChecked(datum.getRepeated().contains("2"));
                checkbox3.setChecked(datum.getRepeated().contains("3"));
                checkbox4.setChecked(datum.getRepeated().contains("4"));
                checkbox5.setChecked(datum.getRepeated().contains("5"));
                checkbox6.setChecked(datum.getRepeated().contains("6"));
            }

            if (datum.getStatus().equals("SCHEDULED") && datum.getProvider() != null && datum.getProvider().getId() != 0 && datum.getManual_assigned_at() == null) {
                call.setVisibility(View.VISIBLE);
            } else {
                call.setVisibility(View.GONE);
            }
            payable.setText(getNumberFormat().format(
                    Double.parseDouble(String.valueOf(Double.valueOf(datum.getEstimated().getEstimatedFare())))));

        }
    }

    @Override
    public void onSuccess(Object o) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Toast.makeText(this, getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.mybutton).setVisible(false);
//        return super.onPrepareOptionsMenu(menu);
//    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            ArrayList<Integer> repeated = new ArrayList<>();
            if (checkbox0.isChecked()) repeated.add(0);
            if (checkbox1.isChecked()) repeated.add(1);
            if (checkbox2.isChecked()) repeated.add(2);
            if (checkbox3.isChecked()) repeated.add(3);
            if (checkbox4.isChecked()) repeated.add(4);
            if (checkbox5.isChecked()) repeated.add(5);
            if (checkbox6.isChecked()) repeated.add(6);
            // update request
            if (DATUM != null) {
                Datum datum = DATUM;
                showLoading();
                presenter.updateRecurrent(datum.getUser_req_recurrent_id(), repeated);
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
