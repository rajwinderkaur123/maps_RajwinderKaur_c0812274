package com.capstone_team_capstone_meal_master.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone_team_capstone_meal_master.R;
import com.capstone_team_capstone_meal_master.interfaces.onFoodItemClick;
import com.capstone_team_capstone_meal_master.model.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    List<Food> foodItems;
    Context context;
    onFoodItemClick listener;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uuid = firebaseAuth.getUid();

    public FoodAdapter(Context context, List<Food> foodItems, onFoodItemClick listener) {
        this.context = context;
        this.foodItems = foodItems;
        this.listener = listener;

    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodViewHolder(LayoutInflater.from(context).inflate(R.layout.single_row_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodItems.get(position);
        Glide.with(context).load(food.getUrl()).into(holder.imvFoodItem);
        holder.tvFoodItem.setText(food.getName());
      /*  if (food.getCategory() != null)
            holder.tvFoodCategory.setText(food.getCategory().getCategory());
        holder.tvFoodPrice.setText(String.format(Locale.US, "%s %.1f", context.getString(R.string.dollar), food.getPrice()));*/
        if (uuid != null) {
            firebaseFirestore.collection("cart").document(uuid).addSnapshotListener((value, error) -> {
                if (value != null) {
                    int count = 0;
                    if (value.contains(food.getId())) {
                        count = Integer.parseInt(String.valueOf(value.get(food.getId())));
                    }
                    holder.tvFoodItemCount.setText(String.valueOf(count));
                    holder.imbMinus.setEnabled(count != 0);
                }
            });
            holder.imbMinus.setOnClickListener(v -> listener.onItemClick(position, -1));
            holder.imbPlus.setOnClickListener(v -> listener.onItemClick(position, +1));
        }
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        ImageView imvFoodItem;
        TextView tvFoodItem;
        TextView tvFoodCategory;
        TextView tvFoodPrice;
        ImageButton imbMinus, imbPlus;
        TextView tvFoodItemCount;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imvFoodItem = itemView.findViewById(R.id.imvFoodItem);
            tvFoodItem = itemView.findViewById(R.id.tvFoodItem);
            tvFoodCategory = itemView.findViewById(R.id.tvFoodCategory);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvFoodItemCount = itemView.findViewById(R.id.tvFoodItemCount);
            imbMinus = itemView.findViewById(R.id.imbMinus);
            imbPlus = itemView.findViewById(R.id.imbPlus);

        }

    }
}
