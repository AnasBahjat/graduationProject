package com.example.graduationproject.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInitializer {
    public static Retrofit retrofitInitializer = null;
    public static Retrofit getClient(String URL){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if(retrofitInitializer == null ){
            retrofitInitializer = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(URL)
                    .build();
        }
        return retrofitInitializer;
    }
}
