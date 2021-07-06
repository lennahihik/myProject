package com.example.myfilms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfilms.R;
import com.example.myfilms.data.Trailer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> trailers;

    private OnTrailerClickListener onTrailerClickListener;

    @NonNull
    @NotNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TrailerAdapter.TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.textViewNameOfVideo.setText(trailer.getName());

    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public interface OnTrailerClickListener {
      void onTrailerClick(String url);
    }


    public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
        this.onTrailerClickListener = onTrailerClickListener;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNameOfVideo;

        public TrailerViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textViewNameOfVideo = itemView.findViewById(R.id.textViewNameOfVideo); // при клике на видео, надо чтобы видео открылось в интернете, поэтому нужен слушатель событий
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onTrailerClickListener != null) {
                        onTrailerClickListener.onTrailerClick(trailers.get(getAdapterPosition()).getKey()); // получаем URL
                    }
                }
            });
        }
    }
}
