package com.example.recipefinder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipefinder.Models.Recipe;
import com.example.recipefinder.R;

import java.util.List;

public class SomeRecipeAdapter extends RecyclerView.Adapter<SomeRecipeAdapter.SomeRecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;
    private boolean isGrid;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public SomeRecipeAdapter(Context context, boolean isGrid) {
        this.context = context;
        this.isGrid = isGrid;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public SomeRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (isGrid){
            view = inflater.inflate(R.layout.recipe_card, parent, false);
        }else{
            view = inflater.inflate(R.layout.recipe_list, parent, false);
        }
        SomeRecipeViewHolder viewHolder = new SomeRecipeViewHolder(view, onItemClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SomeRecipeViewHolder holder, int position) {
        holder.title.setText(recipeList.get(position).getTitle());
        holder.author.setText(recipeList.get(position).getAuthor());

        Glide.with(context).load(recipeList.get(position).getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class SomeRecipeViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title, author;

        public SomeRecipeViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);

            image = itemView.findViewById(R.id.recipeImg);
            title = itemView.findViewById(R.id.recipeTitle);
            author = itemView.findViewById(R.id.recipeAuthor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null){
                        int position = SomeRecipeViewHolder.this.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
