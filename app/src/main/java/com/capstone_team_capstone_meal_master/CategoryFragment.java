package com.capstone_team_capstone_meal_master;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone_team_capstone_meal_master.adapter.FoodAdapter;
import com.capstone_team_capstone_meal_master.interfaces.onFoodItemClick;
import com.capstone_team_capstone_meal_master.model.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryFragment extends Fragment {

    private ArrayList<Food> foodItems;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RecyclerView rvFoodItems;
    private FoodAdapter foodAdapter;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodItems = new ArrayList<>();
        foodAdapter = new FoodAdapter(getActivity(), foodItems, new onFoodItemClick() {
            @Override
            public void onItemClick(int position, int mode) {
                Food food = foodItems.get(position);
                String uuid = firebaseAuth.getUid();
                if (uuid != null)
                    if (mode == 1) {
                        firebaseFirestore.collection("cart").document(uuid).update(food.getId(), FieldValue.increment(1));
                    } else {
                        firebaseFirestore.collection("cart").document(uuid).update(food.getId(), FieldValue.increment(-1));
                    }
            }
        });
        fetchCategoryData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);
        rvFoodItems = view.findViewById(R.id.rvFoodItems);
        rvFoodItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFoodItems.setAdapter(foodAdapter);
        return view;
    }

    public void fetchCategoryData() {

        firebaseFirestore.collection("food").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("MYMSG", error.getMessage());
                return;
            }

            if (value != null) {
                for (DocumentSnapshot ds : value.getDocuments()) {
                    firebaseFirestore.collection("food")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                    for (DocumentSnapshot mealSnapshot : documents) {
                                        Map<String, Object> mealSnapshotData = mealSnapshot.getData();
                                        if (mealSnapshotData != null
                                                && mealSnapshotData.containsKey("id")
                                                && mealSnapshotData.containsKey("category")
                                                && mealSnapshotData.containsKey("name")
                                                && mealSnapshotData.containsKey("url")
                                                && mealSnapshotData.containsKey("price")) {
                                            Food food = new Food( (String) mealSnapshotData.get("category") ,
                                                    (String) mealSnapshotData.get("id"),
                                                    (String) mealSnapshotData.get("name"),
                                                    (String) mealSnapshotData.get("url"),
                                                    Double.parseDouble(String.valueOf(mealSnapshotData.get("price"))));

                                            foodItems.add(food);
                                            foodAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                }

            }
        });
    }


}