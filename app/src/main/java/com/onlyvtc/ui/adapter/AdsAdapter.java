package com.onlyvtc.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.onlyvtc.R;
import com.onlyvtc.data.network.model.Advertisement;

import java.util.List;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.MyViewHolder> {

    private List<Advertisement> list;
    private Context context;

    public AdsAdapter(Context context, List<Advertisement> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_advertisement, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        Advertisement obj = list.get(position);
        Glide.with(context)
                .load(obj.getImage())
                .into(holder.image);

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(obj.getClick_url()));
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
        }
    }
}
