package com.example.beeradvisor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FindBeerActivity extends AppCompatActivity {

    private static final String TAG="FindBeerActivity";
    private BeerExpert expert = new BeerExpert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_beer);
        Log.d(TAG,"Activity Created");
    }

    public void onClickFindBeer(View view){
        TextView tv = (TextView) findViewById(R.id.brands);
        Spinner sp = (Spinner) findViewById(R.id.color);
        String val = String.valueOf(sp.getSelectedItem());

        List<String> brandsList = expert.getBrands(val);
        StringBuilder brandsFormatted = new StringBuilder();


        for(String brand:brandsList){
            brandsFormatted.append(brand).append('\n');
        }

        tv.setText(brandsFormatted);
    }


}