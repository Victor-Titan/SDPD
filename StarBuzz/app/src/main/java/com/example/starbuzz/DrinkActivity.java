package com.example.starbuzz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DrinkActivity extends AppCompatActivity {

    public static final String EXTRA_DRINKID = "drinkId";
    public static final String EXTRAITEM_ID = "itemID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        Toolbar toolbar = findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int itemId = (Integer)getIntent().getExtras().get(EXTRAITEM_ID);
        int drinkId = (Integer)getIntent().getExtras().get(EXTRA_DRINKID);
        TextView name = (TextView) findViewById(R.id.name);
        Drink drink = Drink.drinks[itemId][drinkId];
        name.setText(drink.getName());
        TextView desc = (TextView) findViewById(R.id.description);
        desc.setText(drink.getDescription());

        ImageView photo = (ImageView) findViewById(R.id.photo);
        photo.setImageResource(drink.getImageResourceId());
        photo.setContentDescription(drink.getName());
    }
}