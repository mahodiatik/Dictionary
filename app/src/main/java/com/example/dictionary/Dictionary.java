package com.example.dictionary;


import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Random;

public class Dictionary extends AppCompatActivity {
    public long p = 1000000003,m = 103647,a = 23458768,b = 9874326;
    Word[] word = new Word[(int) m];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        EditText input = findViewById(R.id.input);
        Button search = findViewById(R.id.search);
        TextView meaning = findViewById(R.id.meaning);
        Database();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searched = input.getText().toString().toLowerCase();
                int key = (int) KeyGenerator(searched);
                int i = (int) PrimaryHash(key, a, b);
                int j = (int) SecondaryHash(key,word[i].ai,word[i].bi,word[i].pi);
                if(j >= 0 && searched.equals(word[i].sub[j][0])){
                    meaning.setText(word[i].sub[j][1]);
                }
                else{
                    meaning.setText("Meaning couldn't be found");
                }
            }
        });
    }

    long PrimaryHash(long k, long a, long b){
        return (( a * k + b ) % p )%m;
    }

    long SecondaryHash(long k, long aj, long bj, long mj){
        if(mj==0) return -1;
        return  ((aj * k + bj) % p)% mj;
    }

    long KeyGenerator(String searched){
        long key = 0;
        long t = 1;
        for(long i = 0; i < searched.length(); i++){
            key = (key + (t*((int)searched.charAt((int) i)-97)))%p;
            t = (t*29)%p;
        }
        return key;
    }

    public void Database(){
        String db;
        try {
            InputStream inputStream = getAssets().open("Dataset.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            db = new String(buffer, "UTF-8");
            JSONArray jsnArra = new JSONArray(db);

            for(int i = 0; i < m; i++){
                word[i] = new Word(0, 0, 0);
            }
            for (int i = 0; i < jsnArra.length(); i++){
                JSONObject jsnObj = jsnArra.getJSONObject(i);
                String en = jsnObj.getString("en").toLowerCase();
                word[(int) PrimaryHash(KeyGenerator(en), a, b)].pi++;
            }
            Random random = new Random();
            for(int i = 0; i < m; i++){
                word[i].pi *= word[i].pi;
                word[i].ai = 1 + random.nextInt((int) (p-1));
                word[i].bi = random.nextInt((int) p);
                word[i].setSub();
            }

            for(int i = 0; i < jsnArra.length(); i++){
                JSONObject jsnObj = jsnArra.getJSONObject(i);
                String english = jsnObj.getString("en").toLowerCase();
                String bangla = jsnObj.getString("bn");
                int key = (int) KeyGenerator(english);
                int j = (int) PrimaryHash(key, a, b);
                int k = (int) SecondaryHash(key, word[j].ai,word[j].bi,word[j].pi);
                word[j].sub[k][0] = english;
                word[j].sub[k][1] = bangla;
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }




}
