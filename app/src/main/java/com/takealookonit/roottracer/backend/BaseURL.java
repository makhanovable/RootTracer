package com.takealookonit.roottracer.backend;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

class BaseURL {

    private static Retrofit retrofit = null;

    static API getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://zhusupov-74.000webhostapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(API.class);
    }

}