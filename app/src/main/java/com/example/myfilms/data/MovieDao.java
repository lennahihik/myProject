package com.example.myfilms.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Film>> getAllMovies();

    @Query("SELECT * FROM favorite_movies")
    LiveData<List<FavoriteMovie>> getAllFavoriteMovies();

    @Query("SELECT * FROM movies WHERE  id == :movieId")
    Film getMovieById (int movieId);  //метод возвращающий экземпляр фильма по его ИД

    @Query("SELECT * FROM favorite_movies WHERE  id == :movieId")
    FavoriteMovie getFavoriteMovieById (int movieId);

    @Query("DELETE FROM movies")
    void deleteAllMovies(); // метод удаляющий все фильмы из таблицы

    @Insert
    void insertMovie (Film movie); //метод вставляющий данные

    @Delete
    void deleteMovie (Film movie); // удаление одного фильма

    @Insert
    void insertFavoriteMovie (FavoriteMovie movie); //метод вставляющий данные

    @Delete
    void deleteFavoriteMovie (FavoriteMovie movie); // удаление одного фильма
}
