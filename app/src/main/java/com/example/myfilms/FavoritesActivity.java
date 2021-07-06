package com.example.myfilms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.myfilms.adapters.MovieAdapter;
import com.example.myfilms.data.FavoriteMovie;
import com.example.myfilms.data.Film;
import com.example.myfilms.data.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavoriteMovies;
    private MovieAdapter adapter; // здесь всё так же будет выводиться как в MainActivity , поэтому используем тот же адаптер
    private MainViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // переопределяем метод  onCreateOptionsMenu, чтобы вставить меню
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); // передаём меню которое создали
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // переопределем метод  для реагирования на нажатие пунктов меню. В качестве параметра пункт меню
        int id = item.getItemId(); // получаем знаение. И в зависимости от значения будем выполнять разные действия
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavorite:
                Intent intentToFavorites = new Intent(this, FavoritesActivity.class);
                startActivity(intentToFavorites);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        recyclerViewFavoriteMovies = findViewById(R.id.recyclerViewFavoriteMovies);
        recyclerViewFavoriteMovies.setLayoutManager(new GridLayoutManager(this , 2)); // 2 -  это 2 колонки
        adapter = new MovieAdapter();
        recyclerViewFavoriteMovies.setAdapter(adapter);// устанавливаем адаптер у recyclerView
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class); // присваиваем значение. Что бы получить список любимых фильмов
        LiveData<List<FavoriteMovie>> favoriteMovies = viewModel.getFavoriteMovies();// получаем список любимых фильмов т следующей строкой добавляем обсёрвер
        favoriteMovies.observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(List<FavoriteMovie> favoriteMovies) {
                List<Film> movies = new ArrayList<>(); // создаём новый лист.
                if (favoriteMovies != null) {
                    movies.addAll(favoriteMovies);
                    adapter.setMovies(movies);//при изменении данных мы в нашем адаптере устанавливаем фильмы
                }
            }
        });

       adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                // Toast.makeText(MainActivity.this, "clicked : " + position, Toast.LENGTH_SHORT).show();
                Film movie = adapter.getMovies().get(position);//получаем фильм, на который нажали/Берём его из адаптера
                Intent intent = new Intent(FavoritesActivity.this, DetailActivity.class); //открываем в новой активности
                intent.putExtra("id", movie.getId()); //вставляем в интент инфо с id
                startActivity(intent);
            }
        });

    }
}