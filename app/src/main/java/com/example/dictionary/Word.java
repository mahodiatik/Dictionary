package com.example.dictionary;


public class Word {
    int ai, bi, pi;
    String[][] sub;
    Word(int ai, int bi, int pi){
        this.ai = ai;
        this.bi= bi;
        this.pi = pi;
    }
    void setSub(){
        sub = new String[pi][2];
    }
}
