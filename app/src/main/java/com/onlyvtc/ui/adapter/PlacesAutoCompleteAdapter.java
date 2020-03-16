package com.onlyvtc.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;
import com.onlyvtc.R;
import com.onlyvtc.ui.activity.location_pick.PlaceClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {
    private static final String TAG = "PlacesAutoAdapter";
    private final LatLng boundsLatlng;
    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private final PlacesClient placesClient;
    private PlaceClickListener clickListener;


    public PlacesAutoCompleteAdapter(Context context, LatLng bounds) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        placesClient = Places.createClient(context);
        this.boundsLatlng = bounds;

    }

    public void setClickListener(PlaceClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private RectangularBounds setBounds(LatLng latLng) {
        double radiusInMeters = 50000;
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
//        LogUtil.debug("distance==>>" + distanceFromCenterToCorner);
        LatLng southwestCorner = SphericalUtil.computeOffset(latLng, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner = SphericalUtil.computeOffset(latLng, distanceFromCenterToCorner, 45.0);
        return RectangularBounds.newInstance(southwestCorner, northeastCorner);
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                try {
                    // Skip the autocomplete query if no constraints are given.
                    if (constraint != null) {
                        // Query the autocomplete API for the (constraint) search string.
                        mResultList = getPredictions(constraint);
                        if (mResultList != null) {
                            // The API successfully returned results.
                            results.values = mResultList;
                            results.count = mResultList.size();
//                            LogUtil.debug("resultList==>>" + mResultList.size());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    Log.i(TAG, "result==>>" + results.count);
                    notifyDataSetChanged();
                    if (mResultList.size() > 0) {
                        for (PlaceAutocomplete autocomplete : mResultList)
                            Log.i(TAG, autocomplete.address + "  " + autocomplete.area);

                    } else {
                        Log.i(TAG, "no list results");
                    }
                } else {
                    Log.i(TAG, "no results");
                    notifyDataSetChanged();
                }
            }
        };
    }


    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {

        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(setBounds(boundsLatlng))
//                .setCountry("IN")
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()));
                }
            return resultList;
        } else {
            return resultList;
        }
    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(R.layout.list_item_location, viewGroup, false);
        return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {
        mPredictionHolder.address.setText(mResultList.get(i).address);
        mPredictionHolder.area.setText(mResultList.get(i).area);
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public void clearList() {
        mResultList.clear();
        notifyDataSetChanged();
    }

    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView address, area;
        private LinearLayout item_view;

        PredictionHolder(View itemView) {
            super(itemView);
            area = itemView.findViewById(R.id.area);
            address = itemView.findViewById(R.id.address);
            item_view = itemView.findViewById(R.id.item_view);
            item_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PlaceAutocomplete item = mResultList.get(getAdapterPosition());
            if (v.getId() == R.id.item_view) {

                String placeId = String.valueOf(item.placeId);

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    clickListener.onPlaceClick(place);
                }).addOnFailureListener(exception -> {
                    if (exception instanceof ApiException) {
                        Toast.makeText(mContext, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence address, area;

        PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @NotNull
        @Override
        public String toString() {
            return area.toString();
        }
    }
}