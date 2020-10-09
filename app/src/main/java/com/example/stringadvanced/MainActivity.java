package com.example.stringadvanced;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final Random RANDOM = new Random();
    private static final String WEB = "https://lapkins.ru/dog/";
    private ImageView imageView;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    String https;
    public String LOG_TAG = "MyLog";
    private ArrayList<String> listName;
    private ArrayList<String> listImg;
    private int numberOfQuestion;
    private int numberOfRightAnswer;
    private ArrayList<Button> buttons;
    private int rightCount;
    private int wrongCount;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        https = "https://lapkins.ru/";
        listName = new ArrayList<>();
        listImg = new ArrayList<>();
        buttons = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(10);

        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);

        getContent();
        playGame();

        progressBar.getProgressDrawable().setColorFilter(
                Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void getContent() {
        DownloadPage downloadPage = new DownloadPage();
        try {
            String result = downloadPage.execute(WEB).get();
            Pattern patternImg = Pattern.compile("img src='/(.*?)'");
            Matcher matcherImg = patternImg.matcher(result);
            while (matcherImg.find()) {
                String img = matcherImg.group(1);
                listImg.add(https + img);
            }

//            for(String s: listImg){
//                Log.i(LOG_TAG, s);
//            }

            Pattern patternName = Pattern.compile("<span>(.*?)</span>");
            Matcher matcherName = patternName.matcher(result);
            while (matcherName.find()) {
                String name = matcherName.group(1);
                listName.add(name);
            }

            Log.i(LOG_TAG, String.valueOf(listName.size()));
            Log.i(LOG_TAG, String.valueOf(listImg.size()));
//            for(String s: listName){
//                Log.i(LOG_TAG, s);
//            }

        } catch (ExecutionException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void playGame() {
        if ((rightCount + wrongCount) == 10) {
            Log.i(LOG_TAG, "Блок if");
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("rightAnswer", rightCount);
            rightCount = 0;
            wrongCount = 0;
            startActivity(intent);
        } else {
            DownloadImage downloadImage = new DownloadImage();
            generateQuestion();
            try {
                Bitmap bitmap = downloadImage.execute(listImg.get(numberOfQuestion)).get();
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    for (int i = 0; i < buttons.size(); i++) {
                        if (i == numberOfRightAnswer) {
                            buttons.get(i).setText(listName.get(numberOfQuestion));
                        } else {
                            int wrongAnswer = generateWrongAnswer();
                            buttons.get(i).setText(listName.get(wrongAnswer));
                        }
                    }
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateQuestion() {
        numberOfQuestion = RANDOM.nextInt(listName.size());
        numberOfRightAnswer = RANDOM.nextInt(buttons.size());
    }

    private int generateWrongAnswer() {
        return (int)  RANDOM.nextInt(listName.size());
    }

    public void onClickAnswer(View view) {
        Button button = (Button) view;
        int tag = Integer.parseInt((String) button.getTag());
        if (tag == numberOfRightAnswer) {
            Toast.makeText(this, "Правильно:)", Toast.LENGTH_SHORT).show();
            rightCount++;
            progressBar.incrementProgressBy(1);
        } else {
            Toast.makeText(this, "Не правильно:( " + listName.get(numberOfQuestion), Toast.LENGTH_SHORT).show();
            wrongCount++;
            progressBar.incrementProgressBy(1);
        }
        playGame();
    }


    private static class DownloadPage extends AsyncTask<String, Void, String> {
        URL url = null;
        HttpsURLConnection httpsURLConnection = null;

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                }
            }
            return stringBuilder.toString();
        }
    }

    private static class DownloadImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            URL url = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}