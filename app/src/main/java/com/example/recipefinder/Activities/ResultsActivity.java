package com.example.recipefinder.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.recipefinder.Adapters.SearchTermsAdapter;
import com.example.recipefinder.HorizontalDecoration;
import com.example.recipefinder.R;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private String searchTerm;

    private RecyclerView searchTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        init();
    }

    private void init(){
        searchTerms = findViewById(R.id.tags);
        splitTerms();
    }

    private void splitTerms(){
        List<String> terms = new ArrayList<>();
        searchTerm = getIntent().getStringExtra("search");
        String[] split = searchTerm.split(",");
        for (String s: split){
            if (!s.replaceAll("\\s","").equals("")){
                terms.add(s.replaceAll("\\s",""));
            }
        }

        if (terms.size()<=0){
            SearchTermsAdapter adapter = new SearchTermsAdapter();
            adapter.setTerms(terms);
            searchTerms.setAdapter(adapter);
            searchTerms.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            searchTerms.addItemDecoration(new HorizontalDecoration(20));
        }

    }
}