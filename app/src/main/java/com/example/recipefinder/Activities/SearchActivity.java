package com.example.recipefinder.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.recipefinder.HorizontalDecoration;
import com.example.recipefinder.Models.Recipe;
import com.example.recipefinder.R;
import com.example.recipefinder.Adapters.SomeRecipeAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText search;

    private RecyclerView someRecipes;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
    }

    private void init(){
        loading = findViewById(R.id.loading);
        someRecipes = findViewById(R.id.someRecipes);
        search = findViewById(R.id.ingredients);

        TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startActivity(new Intent(SearchActivity.this, ResultsActivity.class)
                            .putExtra("search", search.getText().toString()));
                }
                return false;

            }
        };
        search.setOnEditorActionListener(actionListener);

        new GetNewRecipesAsyncTask().execute();

    }

    private class GetNewRecipesAsyncTask extends AsyncTask<Void, Void, Void>{
        List<Recipe> recipesList = new ArrayList<>();

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loading.setVisibility(View.GONE);
            SomeRecipeAdapter adapter = new SomeRecipeAdapter(SearchActivity.this, false);
            adapter.setRecipeList(recipesList);
            adapter.setOnItemClickListener(new SomeRecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    startActivity(new Intent(SearchActivity.this, RecipeDetailsActivity.class)
                            .putExtra("detail", recipesList.get(position).getDetailsUrl()));
                }
            });
            someRecipes.setAdapter(adapter);
            someRecipes.addItemDecoration(new HorizontalDecoration(20));
            someRecipes.setLayoutManager(new LinearLayoutManager(SearchActivity.this,RecyclerView.HORIZONTAL, false));

            someRecipes.setVisibility(View.VISIBLE);
        }

        private boolean valid(String url){
            try {
                Document document = Jsoup.connect(url).get();
                Element titleEl = document.selectFirst(".article-info .headline-wrapper h1");
                if (titleEl==null){
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document document = Jsoup.connect("https://allrecipes.com").get();
                Elements recipes = document.select(".recipe-card");

                int i =0;
                for (Element recipe: recipes) {
                    if (i==6){
                        break;
                    }
                    Element link = recipe.selectFirst(".recipeCard__imageContainer a");
                    Element imageUrl = link.selectFirst(".recipeCard__imageLink .lazy-image-udf");

                    // getting the text info
                    Element title = recipe.selectFirst(".recipeCard__detailsContainer a");
                    Element author = recipe.selectFirst(".recipeCard__author a.recipeCard__authorNameLink .recipeCard__authorName");

                    String titleTxt = title.attr("title");
                    String authorTxt = author.text();
                    String imgUrl = imageUrl.attr("data-src");
                    String detailUrl = title.attr("href");

//                    if (!valid(detailUrl)){
//                        continue;
//                    }
                    Recipe resultRecipe = new Recipe(titleTxt,authorTxt,imgUrl,detailUrl);
                    recipesList.add(resultRecipe);
                    i+=1;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}