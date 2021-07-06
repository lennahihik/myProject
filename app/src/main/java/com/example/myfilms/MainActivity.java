package com.example.myfilms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfilms.adapters.MovieAdapter;
import com.example.myfilms.data.Film;
import com.example.myfilms.data.MainViewModel;
import com.example.myfilms.utils.JSONUtils;
import com.example.myfilms.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> { // в качестве параметра данные, которыемы хотим получить из загрузчика

    private Switch switchSort;
    private MovieAdapter movieAdapter;
    private TextView textViewPopularity;
    private TextView textViewTopRated;
    private ProgressBar progressBarLoading;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 133;
    private LoaderManager loaderManager;

    private static int page = 1; // страница загрузки данных
    private static int methodOfSort;
    private static boolean isLoading = false;

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

    private int getColumnCount () { //мутод рассчитвает кол-во колонок в зависимости от ширины экрана (ориентации экрана)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); // получаем характеристики нашего экрана и помещаеи их в объект displayMetrics
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density); // чтобы получить ширину в  аппаратно независимых пикселях(dp), надо обычные пиксели разделить на плотность(density)
        return  width / 185 > 2 ? width / 185 : 2; // получаем кол-во колонок. 185 у нас ширина  постреа/ ? : тернальная операция заменяет if/else - т.е. проверяе ширина / 185 больше двух
        //  , если да, то возвращаем  width / 185 , если нет , то мы возвращаем 2

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        String url = NetworkUtils.buildURL(NetworkUtils.POPULARITY, 2).toString();
//        Log.i("MyResult", url);

//        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.TOP_RATED, 5);
//        if (jsonObject == null) {
//            Toast.makeText( this, "Error!!!,", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText( this, "OK!!", Toast.LENGTH_SHORT).show();
//        }

//        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 1);
//        ArrayList<Film> movies = JSONUtils.getMoviesFromJSON(jsonObject);
//        StringBuilder builder = new StringBuilder();
//        for (Film movie : movies) {
//            builder.append(movie.getTitle()).append("\n");
//        }
//        Log.i("MyResult", builder.toString());

        loaderManager = LoaderManager.getInstance(this); // pattern singleton .  Получаем экземпляр загрузчика, который получаемт все загрузки которые происходят в приложении
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        switchSort = findViewById(R.id.switchSort);
        RecyclerView recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount())); // создали отдельно метод, вычисляющий кол-во колонок getColumnCount()
        movieAdapter = new MovieAdapter();
        switchSort.setChecked(true);//изначальное положение
        recyclerViewPosters.setAdapter(movieAdapter);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false); // как только выполняется это, то запускается код написанный выше
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
               // Toast.makeText(MainActivity.this, "clicked : " + position, Toast.LENGTH_SHORT).show();
                Film movie = movieAdapter.getMovies().get(position);//получаем фильм, на который нажали/Берём его из адаптера
                Intent intent = new Intent(MainActivity.this, DetailActivity.class); //открываем в новой активности
                intent.putExtra("id", movie.getId()); //вставляем в интент инфо с id
                startActivity(intent);
            }
        });
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() { // когда мы долистываем до конца списка, вызывается метод  onReachEnd()
                if (!isLoading) { // и если загрузка не началась, то мы должны её начать
                    //Toast.makeText(MainActivity.this, "End of list", Toast.LENGTH_SHORT).show();
                    downloadData(methodOfSort, page);
                }
            }
        });

        LiveData<List<Film>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Film>>() { // создаём обсёрвер
            @Override
            public void onChanged(List<Film> movies) {
                //теперь всякий раз, когда данные в базе данных будут меняться  мы их будем устанавливать у адаптера
                if (page == 1) {  //если мы только запустили приложение или изменили метод сортировки , то значение переменной  page = 1 и мы начинаем подгрузку данных,
                    //// и если вдруг интернета нет, то подгрузка не произойдёт и данные возьмутся из базы данных, что бы можно было всё равно пользоваться  приложением
                    movieAdapter.setMovies(movies);
                }
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodOfSort (boolean isTopRated) {
        if (isTopRated) {
            textViewTopRated.setTextColor(ContextCompat.getColor(this, R.color.popularity));// устанавливаем цвета тексту Топ
            textViewPopularity.setTextColor(ContextCompat.getColor(this, R.color.white));
            methodOfSort = NetworkUtils.TOP_RATED; // устанавливаем спомоб сортировки
        } else {
            textViewTopRated.setTextColor(ContextCompat.getColor(this, R.color.white));
            textViewPopularity.setTextColor(ContextCompat.getColor(this, R.color.popularity));
            methodOfSort = NetworkUtils.POPULARITY;
        }
        downloadData(methodOfSort, page); // запутсили загрузку данных в страницу

    }

    private void downloadData(int methodOfSort, int page) {  //выносим загрузку данных в отдельный метод.  Будем загружать данные в зависимости от сортировки и какая страница
//        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1); // получаем список фильмов
//        ArrayList<Film> movies = JSONUtils.getMoviesFromJSON(jsonObject);
//        if (movies != null && !movies.isEmpty()) { // если пришли новые данные, то мы их загружаем, удаляем предыдущие данные  и вставляем новые
//            viewModel.deleteAllMovies();//очищаем предыдущие данные
//            for (Film movie : movies) { //и вставляем новые данные в цикле
//              viewModel.insertMovie(movie);
//            }
//        }
        URL url = NetworkUtils.buildURL(methodOfSort, page);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this); // 3тий параметр это слушатель событий, т.к. мы реализовали его в MainActivity  пишем this
    }

    //переопределили 3 метода т.к. у нас MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject>
    @NonNull
    @NotNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() { // при начале загрузки будет вызван метод onStartLoading() где присваивается  isLoading = true;
                progressBarLoading.setVisibility(View.VISIBLE); //при начале загрузки устанафливаем видимость загрузчику
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<JSONObject> loader, JSONObject data) { // когда загрузятся все фильмы, вызывается метод onLoadFinished()
        ArrayList<Film> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && !movies.isEmpty()) { // если пришли новые данные, то мы их загружаем, удаляем предыдущие данные  и вставляем новые
            if(page == 1) {
                viewModel.deleteAllMovies();//очищаем все фильмы из базы только если  page == 1
                movieAdapter.clear();
            }
            for (Film movie : movies) { //и вставляем новые данные в цикле
                viewModel.insertMovie(movie); // в базу данных добавляются все фильмы
            }
            movieAdapter.addMovies(movies); // в адаптер тоже загружаются  все фильмы
            page++;//после того как фильмы загружены увеличиваем страницу на 1
        }
        isLoading = false; // после того как азгрузка завершена, присваивам false
        progressBarLoading.setVisibility(View.INVISIBLE); // когда загрузка завершена прямчем загрузчик
        loaderManager.destroyLoader(LOADER_ID);

    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<JSONObject> loader) {

    }
}