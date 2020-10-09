package com.example.stringadvanced;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    private int rightAnswer;
    private ImageView imageView;
    private Button buttonNext;
    private Button buttonStop;
    private TextView resultCount;
    private TextView result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        imageView = findViewById(R.id.imageViewResultActivity);
        buttonNext = findViewById(R.id.buttonNext);
        resultCount = findViewById(R.id.textViewResultCount);
        result = findViewById(R.id.textViewResult);

        Intent intent = getIntent();
//        if (intent.hasExtra("rightAnswer")) {
            rightAnswer = intent.getIntExtra("rightAnswer", 0);
//        } else {
//            Intent back = new Intent(this, MainActivity.class);
//            startActivity(back);
//        }

        if (rightAnswer < 3) {
            imageView.setImageResource(R.drawable.saddog);
            resultCount.setText("Верных ответов:  "+ rightAnswer);
            result.setText(R.string.text_sad_result);
        }
        if (rightAnswer > 2 && rightAnswer < 8 ) {
            imageView.setImageResource(R.drawable.gooddog);
            resultCount.setText("Верных ответов:  "+ rightAnswer);
            result.setText(R.string.text_good_result);
        }
        if (rightAnswer > 7) {
            imageView.setImageResource(R.drawable.perfectdog);
            resultCount.setText("Верных ответов:  "+ rightAnswer);
            result.setText(R.string.text_great);
        }
    }

    public void continueTheGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}