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
    BigInteger p = new BigInteger("100000000003");
    int m = 103647;
    BigInteger a = new BigInteger("23458768");
    BigInteger b = new BigInteger("9874326");
    Word[] word = new Word[m];
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
                BigInteger key = KeyGenerator(searched);
                int i = PrimaryHash(key, a, b);
                int j = SecondaryHash(key, BigInteger.valueOf(word[i].ai), BigInteger.valueOf(word[i].bi), BigInteger.valueOf(word[i].pi));
                if(j >= 0 && searched.equals(word[i].sub[j][0])){
                    meaning.setText(word[i].sub[j][1]);
                }
                else{
                    meaning.setText("Meaning couldn't be found");
                }
            }
        });
    }

    int PrimaryHash(BigInteger k, BigInteger a, BigInteger b){
        return a.multiply(k).add(b).mod(p).mod(BigInteger.valueOf(m)).intValue();
    }

    int SecondaryHash(BigInteger k, BigInteger aj, BigInteger bj, BigInteger mj){
        if(mj.equals(BigInteger.valueOf(0))) return -1;
        return  aj.multiply(k).add(bj).mod(p).mod(mj).intValue();
    }

    BigInteger KeyGenerator(String ss){
        BigInteger key = new BigInteger("0");
        BigInteger t = new BigInteger("1");
        for(int i = 0; i < ss.length(); i++){
            key = key.add(t.multiply(BigInteger.valueOf((int)ss.charAt(i)-97)));
            key = key.mod(p);
            t = t.multiply(BigInteger.valueOf(29));
            t = t.mod(p);
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
                word[PrimaryHash(KeyGenerator(en), a, b)].pi++;
            }
            Random random = new Random();
            for(int i = 0; i < m; i++){
                word[i].pi *= word[i].pi;
                word[i].ai = 1 + random.nextInt(p.intValue()-1);
                word[i].bi = random.nextInt(p.intValue());
                word[i].setSub();
            }

            for(int i = 0; i < jsnArra.length(); i++){
                JSONObject jsnObj = jsnArra.getJSONObject(i);
                String english = jsnObj.getString("en").toLowerCase();
                String bangla = jsnObj.getString("bn");
                BigInteger key = KeyGenerator(english);
                int j = PrimaryHash(key, a, b);
                int k = SecondaryHash(key, BigInteger.valueOf(word[j].ai), BigInteger.valueOf(word[j].bi), BigInteger.valueOf(word[j].pi));
                word[j].sub[k][0] = english;
                word[j].sub[k][1] = bangla;
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }




}
