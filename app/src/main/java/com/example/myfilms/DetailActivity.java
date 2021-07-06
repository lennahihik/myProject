package com.example.myfilms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfilms.adapters.ReviewAdapter;
import com.example.myfilms.adapters.TrailerAdapter;
import com.example.myfilms.data.FavoriteMovie;
import com.example.myfilms.data.Film;
import com.example.myfilms.data.MainViewModel;
import com.example.myfilms.data.Review;
import com.example.myfilms.data.Trailer;
import com.example.myfilms.utils.JSONUtils;
import com.example.myfilms.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ImageView imageViewAddToFavorites;
    private ScrollView scrollViewInfo;

    private RecyclerView recyclerViewTrailers;
    private TrailerAdapter trailerAdapter;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;

    private int id;
    private Film movie;
    private MainViewModel viewModel;
    private FavoriteMovie favoriteMovie;

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
        setContentView(R.layout.activity_detail);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewAddToFavorites = findViewById(R.id.imageViewAddToFavorites);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) { //если интент не равен null и содержит ключ id
            id = intent.getIntExtra("id", -1);//получаем id фильма
        } else {
            finish(); // в противном случае, закрываем этой командой активность. Закроется эта активность и откроется активность которая вызвала данную активность.
        }
        //теперь надо получить фильм из базы данных
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        movie = viewModel.getMovieById(id);//и получаем фильм
        //теперь у всех элементов устанавливаем нужное значение ->
        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.placeholder).into(imageViewBigPoster); //у Imageview устанавливаем нужное значение с помощью picasso
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getRealiseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        //создаётся активность, и в конце мы проверяем, существует ли фильм в избранном! ->
        setFavorite(); //если фильма не существует, то этой переменной( которая в мктоде) приствоится значение null
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() { //проверяем рботу слушателя
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); // Используем не явный интент. хотим отобразить информацию в интернете -> ACTION_VIEW используем для этого
                startActivity(intentToTrailer);
            }
        });
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId()); // передать в качестве парарметра ИД фильма
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());
        //из полученных JSON объектов надо получить трейлеры и отзывы
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        //теперь полкченные массивы надо установить у адаптеров
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
        scrollViewInfo.smoothScrollTo(0, 0); // самый верх. Позицию прокрутке устанавливаем

    }

    public void onClickChangeFavorites(View view) {
        //перед тем как добавить в избранное фильм надо проверить, может быть он уже там находится и тогда надо его удалить

        if(favoriteMovie == null) { // фильма в базе данных нет, значит добавлем его
            viewModel.insertFavoriteMovie(new FavoriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favorites, Toast.LENGTH_SHORT).show();
        } else { // если объект уже существует
            viewModel.deleteFavoriteMovie(favoriteMovie);
            Toast.makeText(this, R.string.delete_from_favorites, Toast.LENGTH_SHORT).show();
        }
        setFavorite(); // устанавливаем правильное значение у  favoriteMovie
    }

    private void setFavorite() { // устанавливаем знаение существует этот фильм или нет/ Если нет, то присвоится null
        favoriteMovie = viewModel.getFavoriteMovieById(id);
        if (favoriteMovie == null) { // если объект не добавлен
            imageViewAddToFavorites.setImageResource(R.drawable.stargolddd); // то устанавливаем серую звезду
        } else {
            imageViewAddToFavorites.setImageResource(R.drawable.star);//если добавлен, то желтая
        }

    }
}