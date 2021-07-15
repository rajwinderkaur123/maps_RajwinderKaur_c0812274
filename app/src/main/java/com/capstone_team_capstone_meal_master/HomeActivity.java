package com.capstone_team_capstone_meal_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    FrameLayout flContent;
    Fragment categoryFragment, orderFragment, cartFragment;
    Button btLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btLogout = findViewById(R.id.btLogout);
        if (auth.getCurrentUser() == null) {
            btLogout.setVisibility(View.GONE);
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        categoryFragment = CategoryFragment.newInstance();

        orderFragment = new OrderFragment();
        cartFragment = new CartFragment();
        setCurrentFragment(categoryFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.cart) {
                setCurrentFragment(cartFragment);
            } else if (item.getItemId() == R.id.category) {
                setCurrentFragment(categoryFragment);
            } else if (item.getItemId() == R.id.order) {
                setCurrentFragment(orderFragment);
            }
            return true;
        });
        flContent = findViewById(R.id.flContent);
    }

    public void logout(View view) {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
    }
}