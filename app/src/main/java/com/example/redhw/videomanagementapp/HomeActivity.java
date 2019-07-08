package com.example.redhw.videomanagementapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.redhw.videomanagementapp.models.Movie;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mMovieList;
    private DatabaseReference mDatabase;
    private FirebaseAuth rAuth;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("movies");
        mDatabase.keepSynced(true);

        mMovieList = (RecyclerView)findViewById(R.id.myrecycleview);
        mMovieList.setHasFixedSize(true);
        mMovieList.setLayoutManager(new LinearLayoutManager(this));


        BottomNavigationView mBtmView;
        mBtmView = (BottomNavigationView) findViewById(R.id.navigation);
        mBtmView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBtmView.getMenu().findItem(R.id.navigation_home).setChecked(true);

        mBtmView.setOnNavigationItemSelectedListener((item)->{
            switch(item.getItemId()){

                case R.id.navigation_home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    break;

                case R.id.navigation_dashboard:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    break;


                case R.id.navigation_notifications:
                    FirebaseAuth.getInstance().signOut();

                    //Firebase registration instance
                    rAuth = FirebaseAuth.getInstance();

                    //If user is already logged in, will be redirected to HomeActivity
                    if(rAuth.getCurrentUser()==null){
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                    break;

            }
            return false;

        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Movie, MovieViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Movie, MovieViewHolder>
                (Movie.class, R.layout.movie_row, MovieViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(MovieViewHolder viewHolder, Movie model, int position) {

                viewHolder.setMovieGenre(model.getGenre());
                viewHolder.setMovieImdbRatings(model.getIMDB());
                viewHolder.setMoviePrice(model.getPrice());
                viewHolder.setMovieRuntime(model.getRuntime());
                viewHolder.setMovieTitle(model.getTitle());
            }
        };

        mMovieList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MovieViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        public void setMovieTitle(String title){
            TextView post_title=(TextView)mView.findViewById(R.id.movie_name);
            post_title.setText(title);
        }

        public void setMovieGenre(String genre){
            TextView post_genre=(TextView)mView.findViewById(R.id.genre);
            post_genre.setText(genre);
        }

        public void setMovieRuntime(String runtime){
            TextView post_runtime=(TextView)mView.findViewById(R.id.runtime);
            post_runtime.setText(runtime);
        }

        public void setMovieImdbRatings(String imdbRatings){
            TextView post_imdbRatings=(TextView)mView.findViewById(R.id.imdb);
            post_imdbRatings.setText(imdbRatings);
        }

        public void setMoviePrice(String price){
            TextView post_price=(TextView)mView.findViewById(R.id.price);
            post_price.setText(price);
        }
    }
}
