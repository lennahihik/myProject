package com.example.myfilms.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myfilms.R;
import com.example.myfilms.data.Film;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Film> movies;
    private OnPosterClickListener onPosterClickListener;
    private OnReachEndListener onReachEndListener;

    public MovieAdapter () {
        movies = new ArrayList<>(); // setter + getter
    }
    @NonNull
    @NotNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    public interface OnPosterClickListener { // слушатель нажатия на картинку с фильмом
        void onPosterClick (int position);

    }

    public interface OnReachEndListener { // слушатель при достижения конца страницы ( что бы  начать подгружать данные следующей страницы)
        void onReachEnd();
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) { // сеттер добавляем на OnPosterClickListener
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MovieAdapter.MovieViewHolder movieViewHolder, int i) {
        if (movies.size() >= 20 && i > movies.size() - 4 && onReachEndListener != null) { // если до конца списка осталось 4 элемента ( что бы начинало подгружать элементы заранее, а не достигнув конца списка)  и слушатель конца не равен налл
          onReachEndListener.onReachEnd(); // то вызываем у него метод
        }
        Film movie = movies.get(i);
        Picasso.get().load(movie.getPosterPath()).into(movieViewHolder.imageViewSmallPoster); // загружаем изображения  помощью picasso (+ dependencies)


    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void clear() { //метод очищающий список
        this.movies.clear();
        notifyDataSetChanged();
    }

    public void setMovies(List<Film> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
    public void addMovies(List<Film> movies) { // утод для добавления фильмов
        this.movies.addAll(movies);
        notifyDataSetChanged(); // оповещаем адаптер об изменении данных
    }

    public List<Film> getMovies() {
        return movies;
    }
}
