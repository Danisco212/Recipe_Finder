package com.example.recipefinder.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipefinder.R;

import java.util.List;

public class SearchTermsAdapter extends RecyclerView.Adapter<SearchTermsAdapter.SearchTermsViewHolder> {

    private List<String> terms;

    public void setTerms(List<String> terms) {
        this.terms = terms;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchTermsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchTermsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.terms_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTermsViewHolder holder, int position) {
        holder.term.setText(terms.get(position));
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public static class SearchTermsViewHolder extends RecyclerView.ViewHolder{
        private TextView term;
        public SearchTermsViewHolder(@NonNull View itemView) {
            super(itemView);

            term = itemView.findViewById(R.id.term);
        }
    }
}
