package com.example.recipefinder.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipefinder.R;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private boolean isInstructions;

    public IngredientAdapter(boolean isInstructions) {
        this.isInstructions = isInstructions;
    }

    private List<String> items;

    public void setItems(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        if (isInstructions){
            String instruction = "Step " +(position+1) +"\n\n"+items.get(position);
            holder.itemname.setText(instruction);
        }else{
            holder.itemname.setText(items.get(position));

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder{
        private TextView itemname;
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            itemname = itemView.findViewById(R.id.itemName);
        }
    }
}
