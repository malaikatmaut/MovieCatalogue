package com.ryanrvldo.moviecatalogue.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ryanrvldo.moviecatalogue.BuildConfig;
import com.ryanrvldo.moviecatalogue.R;
import com.ryanrvldo.moviecatalogue.data.source.model.TvShow;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class TvShowsAdapter extends RecyclerView.Adapter<TvShowsAdapter.TvShowViewHolder> {

    private List<TvShow> tvShowItems;
    private OnItemClicked onItemClicked;
    private String type;

    public TvShowsAdapter(ArrayList<TvShow> tvShowItems, OnItemClicked onItemClicked, String type) {
        this.tvShowItems = tvShowItems;
        this.onItemClicked = onItemClicked;
        this.type = type;
    }

    public void refillTv(List<TvShow> items) {
        this.tvShowItems.clear();
        this.tvShowItems.addAll(items);
        notifyDataSetChanged();
    }

    public TvShow getTvShowAt(int position) {
        return tvShowItems.get(position);
    }

    @NonNull
    @Override
    public TvShowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (type.equalsIgnoreCase("tvShow")) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie, viewGroup, false);
            return new TvShowViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search, viewGroup, false);
            return new TvShowViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TvShowViewHolder holder, int position) {
        if (type.equalsIgnoreCase("tvShow")) {
            holder.bindTv(tvShowItems.get(position));
        } else {
            holder.bindSearch(tvShowItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return tvShowItems.size();
    }

    public interface OnItemClicked {
        void onItemClick(TvShow tvShow);
    }

    class TvShowViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvRating;
        ShapeableImageView tvPoster;
        TextView tvOverview;
        TvShow tvShow;

        TvShowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.movie_title);
            tvRating = itemView.findViewById(R.id.movie_rating);
            tvPoster = itemView.findViewById(R.id.movie_poster);
            tvOverview = itemView.findViewById(R.id.movie_overview);

            itemView.setOnClickListener(v -> onItemClicked.onItemClick(tvShow));
        }

        private void bindTv(TvShow tvShow) {
            this.tvShow = tvShow;

            tvTitle.setText(tvShow.getTitle());
            Glide.with(itemView)
                    .load(BuildConfig.TMDB_IMAGE_BASE_URL + tvShow.getPosterPath())
                    .placeholder(R.drawable.ic_undraw_images)
                    .error(R.drawable.ic_undraw_404).centerCrop().transition(withCrossFade())
                    .into(tvPoster);
        }

        private void bindSearch(TvShow tvShow) {
            this.tvShow = tvShow;

            tvTitle.setText(tvShow.getTitle());
            tvRating.setText(String.valueOf(tvShow.getRating()));
            tvOverview.setText(tvShow.getOverview());
            Glide.with(itemView)
                    .load(BuildConfig.TMDB_IMAGE_BASE_URL + tvShow.getBackdrop())
                    .placeholder(R.drawable.ic_undraw_images)
                    .error(R.drawable.ic_undraw_404).centerCrop().transition(withCrossFade())
                    .into(tvPoster);
        }
    }
}
