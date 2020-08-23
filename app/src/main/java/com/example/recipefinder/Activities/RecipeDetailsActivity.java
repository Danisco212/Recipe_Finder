package com.example.recipefinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.recipefinder.Adapters.IngredientAdapter;
import com.example.recipefinder.Models.Recipe;
import com.example.recipefinder.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private  CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar loading;

    private TextView title, description;
    private LinearLayout alert;
    private ImageView pic;
    private Button siteBtn;

    private RecyclerView ingredientsRecycler, instructionsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        init();
    }



    private void init(){
        loading = findViewById(R.id.loading);
        coordinatorLayout = findViewById(R.id.cordinator);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);

        pic = findViewById(R.id.pic);
        title = findViewById(R.id.recipeTitle);
        description = findViewById(R.id.recipeDesc);
        alert = findViewById(R.id.noInfo);
        siteBtn = findViewById(R.id.site);
        ingredientsRecycler = findViewById(R.id.ingredients);
        instructionsRecycler = findViewById(R.id.directions);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Toast.makeText(this, getIntent().getStringExtra("detail"), Toast.LENGTH_SHORT).show();
        new GetDetailsAsyncTask().execute(getIntent().getStringExtra("detail"));

    }

    private void fillView(Recipe recipe){
        showTitle(recipe.getTitle());
        Glide.with(this).load(recipe.getImageUrl()).into(pic);

        title.setText(recipe.getTitle());
        description.setText(recipe.getDescription());

        IngredientAdapter adapter = new IngredientAdapter(false);
        adapter.setItems(recipe.getIngredients());
        ingredientsRecycler.setAdapter(adapter);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(this));

        IngredientAdapter adapter1 = new IngredientAdapter(true);
        adapter1.setItems(recipe.getInstructions());
        instructionsRecycler.setAdapter(adapter1);
        instructionsRecycler.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this));

    }


    private void showTitle(final String title){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(title);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }

        });
    }

    private class GetDetailsAsyncTask extends AsyncTask<String, Void, Void> {

        private Recipe recipe;
        private boolean noInfo = false;
        private String url;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (noInfo){
                loading.setVisibility(View.GONE);
                alert.setVisibility(View.VISIBLE);
                siteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
                    }
                });
            }else{
                fillView(recipe);
                loading.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
            }


        }

        @Override
        protected Void doInBackground(String... strings) {
            url = strings[0];
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Element titleEl = document.selectFirst(".article-info .headline-wrapper h1");
                if (titleEl==null){
                    // Toast its an ad sorry
                    noInfo = true;
                }else{
                    String title = titleEl.text();
                    String desc = document.selectFirst(".recipe-summary p").text();
                    String imageUrl = document.selectFirst(".image-container img").attr("src");
                    Elements ingredients = document.select(".ingredients-item");

                    List<String> ingredientsList = new ArrayList<>();
                    for (Element ingredient : ingredients) {
                        ingredientsList.add(ingredient.selectFirst(".ingredients-item-name").text());
                    }

                    Elements instructions = document.select(".instructions-section-item");
                    List<String> instructionList = new ArrayList<>();
                    for (Element instruction : instructions) {
                        instructionList.add(instruction.selectFirst(".paragraph").text());
                    }

                    recipe = new Recipe(title, "", desc, imageUrl, strings[0], ingredientsList, instructionList);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}