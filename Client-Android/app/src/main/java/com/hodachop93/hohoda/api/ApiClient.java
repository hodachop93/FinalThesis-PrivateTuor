package com.hodachop93.hohoda.api;

import com.hodachop93.hohoda.common.AppReferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Hop on 08/03/2016.
 */
public class ApiClient {

    private static Retrofit retrofit;

    private static String BASE_URL = "http://hohodaapi.herokuapp.com/api/";
//    private static String BASE_URL = "http://192.168.1.10:8080/api/";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // add your other interceptors …
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            .header("deviceTokenId", AppReferences.getDeviceToken())
                            .header("x-access-token", AppReferences.getUserTokenID())
                            .header("user_id", AppReferences.getUserID())
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            // add logging as last interceptor
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
