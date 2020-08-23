package com.example.recipefinder.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.recipefinder.Adapters.SearchTermsAdapter;
import com.example.recipefinder.Adapters.SomeRecipeAdapter;
import com.example.recipefinder.HorizontalDecoration;
import com.example.recipefinder.Models.Recipe;
import com.example.recipefinder.Models.SearchObject;
import com.example.recipefinder.R;
import com.example.recipefinder.VerticalDecoration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private String searchTerm;

    private RecyclerView searchTerms, results;
    private Spinner sorter;
    private ProgressBar loading;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        init();
    }

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchTerms = findViewById(R.id.tags);
        results = findViewById(R.id.results);
        sorter = findViewById(R.id.sort);
        loading = findViewById(R.id.loading);
        searchTerm = getIntent().getStringExtra("search");
        setUpSpinner();
        splitTerms();
    }

    private void setUpSpinner(){
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.sort, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorter.setAdapter(arrayAdapter);

        sorter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loading.setVisibility(View.VISIBLE);
                results.setVisibility(View.GONE);
                GetResultsAsyncTask getResultsAsyncTask = new GetResultsAsyncTask();
                getResultsAsyncTask.execute(new SearchObject(searchTerm, position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void splitTerms(){
        List<String> terms = new ArrayList<>();
        String[] split = searchTerm.split(",");
        for (String s: split){
            if (!s.replaceAll("\\s","").equals("")){
                terms.add(s.replaceAll("\\s",""));
            }
        }

        if (terms.size()>0){
            SearchTermsAdapter adapter = new SearchTermsAdapter();
            adapter.setTerms(terms);
            searchTerms.setAdapter(adapter);
            searchTerms.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            searchTerms.addItemDecoration(new HorizontalDecoration(20));
        }
    }

    private class GetResultsAsyncTask extends AsyncTask<SearchObject, Void, Void>{

        private List<Recipe> recipeList = new ArrayList<>();

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SomeRecipeAdapter adapter = new SomeRecipeAdapter(ResultsActivity.this, true);
            adapter.setRecipeList(recipeList);
            adapter.setOnItemClickListener(new SomeRecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    startActivity(new Intent(ResultsActivity.this, RecipeDetailsActivity.class)
                            .putExtra("detail", recipeList.get(position).getDetailsUrl()));
                }
            });
            results.setAdapter(adapter);
            results.setLayoutManager(new GridLayoutManager(ResultsActivity.this, 2));
            results.addItemDecoration(new VerticalDecoration(20));
            loading.setVisibility(View.GONE);
            results.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(SearchObject... searchObjects) {
            String url;
            if (searchObjects[0].getSortOrder()==1){
               url = "https://www.allrecipes.com/search/results/?ingIncl="+ searchObjects[0].getSearchTerm() +"&sort=n";
            }else if(searchObjects[0].getSortOrder()==2){
                url = "https://www.allrecipes.com/search/results/?ingIncl="+ searchObjects[0].getSearchTerm() +"&sort=p";
            }else{
                url = "https://www.allrecipes.com/search/results/?ingIncl="+ searchObjects[0].getSearchTerm() +"&sort=re";
            }

            Document document;
            try {
                document = Jsoup.connect(url).get();
                Elements results = document.select(".recipe-section article.fixed-recipe-card");

                if	(results.size()>0) {
                    for(Element result : results) {
                        String title = result.selectFirst(".fixed-recipe-card__info").selectFirst(".fixed-recipe-card__h3 a .fixed-recipe-card__title-link").text();
                        String author = result.selectFirst(".fixed-recipe-card__profile a .cook-submitter-info").selectFirst("li:has(h4) h4").text();
                        String imgUrl = result.selectFirst(".grid-card-image-container a img.fixed-recipe-card__img").attr("data-original-src");
                        String detailsUrl = result.selectFirst(".grid-card-image-container a").attr("href");

                        Recipe recipe = new Recipe(title,author,imgUrl,detailsUrl);
                        recipeList.add(recipe);
                    }
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