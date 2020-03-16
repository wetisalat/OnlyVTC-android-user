package com.onlyvtc.ui.fragment.past_trip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.onlyvtc.MvpApplication;
import com.onlyvtc.R;
import com.onlyvtc.base.BaseFragment;
import com.onlyvtc.data.network.model.Datum;
import com.onlyvtc.data.network.model.Payment;
import com.onlyvtc.data.network.model.ServiceType;
import com.onlyvtc.ui.activity.past_trip_detail.PastTripDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PastTripFragment extends BaseFragment implements PastTripIView {

    @BindView(R.id.past_trip_rv)
    RecyclerView pastTripRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.progress_bar)
    LottieAnimationView progressBar;
    @BindView(R.id.tv_error)
    TextView error;
    Unbinder unbinder;

    List<Datum> list = new ArrayList<>();
    TripAdapter adapter;

    private PastTripPresenter<PastTripFragment> presenter = new PastTripPresenter<>();

    public PastTripFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_past_trip;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        error.setText(getString(R.string.no_past_found));
        adapter = new TripAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);
        pastTripRv.setLayoutManager(mLayoutManager);
        pastTripRv.setItemAnimator(new DefaultItemAnimator());
        pastTripRv.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        presenter.pastTrip();
        return view;
    }

    @Override
    public void onSuccess(List<Datum> datumList) {
        progressBar.setVisibility(View.GONE);

        list.clear();
        list.addAll(datumList);
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
        else errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onError(Throwable e) {
        progressBar.setVisibility(View.GONE);
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    private class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

        private List<Datum> list;
        private Context mContext;

        private TripAdapter(List<Datum> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_past_trip, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Datum datum = list.get(position);

            String strCurrentDate = datum.getFinishedAt();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
            SimpleDateFormat timeFormat;
            Date newDate;
            try {
                newDate = format.parse(strCurrentDate);
                format = new SimpleDateFormat("dd-MM-yyyy");
                timeFormat = new SimpleDateFormat("HH:mm:ss");
                String date = format.format(newDate);
                String time = timeFormat.format(newDate);
                holder.finishedAt.setText(date + " " + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            holder.finishedAt.setText(datum.getFinishedAt());
            holder.bookingId.setText(datum.getBookingId());

            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(datum.getStaticMap())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                            .dontAnimate().
                                    error(R.drawable.ic_launcher_background).diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.staticMap);

            ServiceType mService = datum.getServiceType();
            if (mService != null) {
                holder.serviceType.setText(mService.getName());
                Glide.with(baseActivity())
                        .load(mService.getImage())
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_car)
                                .dontAnimate()
                                .error(R.drawable.ic_car).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(holder.avatar);
            }

            Payment payment = datum.getPayment();
            if (payment != null) {
                String s = getNewNumberFormat(payment.getTotal());
                holder.payable.setText(s);
//                holder.payable.setText(SharedHelper.getKey(mContext, "currency") + " " + s);
            }
            //  holder.payable.setText(payment.getTotal().toString());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private CardView itemView;
            private TextView bookingId, payable, finishedAt, serviceType;
            private ImageView staticMap, avatar;

            MyViewHolder(View view) {
                super(view);
                itemView = view.findViewById(R.id.item_view);
                bookingId = view.findViewById(R.id.booking_id);
                payable = view.findViewById(R.id.payable);
                finishedAt = view.findViewById(R.id.finished_at);
                staticMap = view.findViewById(R.id.static_map);
                serviceType = view.findViewById(R.id.serviceType);
                avatar = view.findViewById(R.id.avatar);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (view.getId() == R.id.item_view) {
                    MvpApplication.DATUM = list.get(position);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                            (baseActivity(), staticMap, ViewCompat.getTransitionName(staticMap));
                    Intent intent = new Intent(getActivity(), PastTripDetailActivity.class);
                    startActivity(intent, options.toBundle());
                }
            }
        }
    }
}
