package com.example.projectp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InformationActivity extends AppCompatActivity {
    TextView textName, textType, textHeight, textWeight, textInfo;
    ImageButton btnFinish;
    ImageView imagePokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informationview);
        textName = (TextView) findViewById(R.id.namePokemon);
        textType = (TextView) findViewById(R.id.typePokemon);
        textHeight = (TextView) findViewById(R.id.heightPokemon);
        textWeight = (TextView) findViewById(R.id.weightPokemon);
        textInfo = (TextView) findViewById(R.id.informationPokemon);
        btnFinish = (ImageButton)findViewById(R.id.btnFinish);
        imagePokemon = (ImageView)findViewById(R.id.imagePokemon);


        Intent intent = getIntent();
        int number = intent.getIntExtra("number",42);
        int imageId = intent.getIntExtra("imageId", 0);

        String[] name = getResources().getStringArray(R.array.pokemonName);
        String[] type = getResources().getStringArray(R.array.pokemonType);
        String[] height = getResources().getStringArray(R.array.pokemonHeight);
        String[] weight = getResources().getStringArray(R.array.pokemonWeight);
        String[] information = getResources().getStringArray(R.array.pokemonInfo);

        textName.setText(name[number]);
        textType.setText(type[number]);
        textHeight.setText(height[number]);
        textWeight.setText(weight[number]);
        textInfo.setText(information[number]);
        imagePokemon.setImageResource(imageId);

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
