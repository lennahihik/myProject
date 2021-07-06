package com.example.myfilms.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Film>> movies;
    private LiveData<List<FavoriteMovie>> favoriteMovies;

    private static MovieDatabase database;

    public MainViewModel(@NonNull @NotNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        favoriteMovies = database.movieDao().getAllFavoriteMovies();
    }
    //теперь создаём методы для доступа к базе данных

    public Film getMovieById (int id) { //все действия в этом этом методе надо выполнить в другом программнои потоке
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    public FavoriteMovie getFavoriteMovieById (int id) {
        try {
            return new GetFavoriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() { //getter на favoriteMovies
        return favoriteMovies;
    }

    public void deleteAllMovies() { // и создаём  для этого метода новый асинк класс DeleteMoviesTask
        new DeleteMoviesTask().execute();

    }

    public void insertMovie(Film movie) { // и создаём класс InsertTask
        new InsertTask().execute(movie);

    }

    public void deleteMovie(Film movie) { //создаём  для этого метода новый асинк класс DeleteTask
        new DeleteTask().execute(movie);

    }

    public void insertFavoriteMovie(FavoriteMovie movie) {
        new InsertFavoriteTask().execute(movie);

    }

    public void deleteFavoriteMovie(FavoriteMovie movie) {
        new DeleteFavoriteTask().execute(movie);

    }

    public LiveData<List<Film>> getMovies() { //getter на List
        return movies;
    }

    private static class DeleteTask extends AsyncTask<Film, Void, Void> { // принимает в качестве парарметра Film  и ничего не возвращает во втором параметре (<Film, Void, Void>)
        @Override
        protected Void doInBackground(Film... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteMovie(movies[0]);
            }
            return null; // если вдруг не передали параметры, то этод класс возвратит налл
        }
    }

    private static class InsertTask extends AsyncTask<Film, Void, Void> { // принимает в качестве парарметра Film  и ничего не возвращает во втором параметре (<Film, Void, Void>)
        @Override
        protected Void doInBackground(Film... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertMovie(movies[0]);
            }
            return null; // если вдруг не передали параметры, то этод класс возвратит налл
        }
    }

    private static class DeleteFavoriteTask extends AsyncTask<FavoriteMovie, Void, Void> { // принимает в качестве парарметра Film  и ничего не возвращает во втором параметре (<Film, Void, Void>)
        @Override
        protected Void doInBackground(FavoriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteFavoriteMovie(movies[0]);
            }
            return null; // если вдруг не передали параметры, то этод класс возвратит налл
        }
    }

    private static class InsertFavoriteTask extends AsyncTask<FavoriteMovie, Void, Void> { // принимает в качестве парарметра Film  и ничего не возвращает во втором параметре (<Film, Void, Void>)
        @Override
        protected Void doInBackground(FavoriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().insertFavoriteMovie(movies[0]);
            }
            return null; // если вдруг не передали параметры, то этод класс возвратит налл
        }
    }

    private static class DeleteMoviesTask extends AsyncTask<Void, Void, Void> { //он не принимает и не возращает параметров (Void, Void, Void)

        @Override
        protected Void doInBackground(Void... integers) {
                database.movieDao().deleteAllMovies();
            return null;
        }
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, Film> {

        @Override
        protected Film doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getMovieById(integers[0]);
            }
            return null; // если вдруг не передали параметры, то этод класс возвратит налл
        }
    }

    private static class GetFavoriteMovieTask extends AsyncTask<Integer, Void, FavoriteMovie> {

        @Override
        protected FavoriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getFavoriteMovieById(integers[0]);
            }
            return null; // если вдруг не передали параметры, то этод класс возвратит налл
        }
    }
}
