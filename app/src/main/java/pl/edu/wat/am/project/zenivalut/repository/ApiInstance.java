package pl.edu.wat.am.project.zenivalut.repository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiInstance {
    private static Retrofit instance;

    private ApiInstance(){}

    public static Retrofit getInstance(){
        if(instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
