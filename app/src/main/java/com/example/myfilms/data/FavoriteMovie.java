package com.example.myfilms.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity (tableName = "favorite_movies")
public class FavoriteMovie extends Film { //создаём конструктор
    public FavoriteMovie(int uniqueId, int id, int voteCount, String title, String originalTitle, String overview, String posterPath, String bigPosterPath, String backdropPath, double voteAverage, String realiseDate) {
        super(uniqueId, id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, realiseDate);
    }

    //т.к. этот класс является таблицей в базе данных, то ему нужен только 1 конструктор, второй  помечаем аннотацией ignore
    @Ignore
    public FavoriteMovie(Film movie) { //создаём конструктор. Он принимает в качесте параметра объект родительского класса
        super(movie.getUniqueId(), movie.getId(), movie.getVoteCount(), movie.getTitle(), movie.getOriginalTitle(), movie.getOverview(), movie.getPosterPath(), movie.getBigPosterPath(), movie.getBackdropPath(), movie.getVoteAverage(), movie.getRealiseDate()); // возвращает этот же фильм, но преоразованный в дочерний класс
    }
}
