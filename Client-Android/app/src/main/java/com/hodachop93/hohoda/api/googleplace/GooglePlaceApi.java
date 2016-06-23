package com.hodachop93.hohoda.api.googleplace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.model.GooglePlaceResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Hop Dac Ho on 23/04/2016.
 */
public class GooglePlaceApi {

    private static String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static Retrofit retrofit;
    private static GooglePlaceApiInterface apiInterface;

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
/*            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);*/

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // add your other interceptors …
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            // add logging as last interceptor
//            httpClient.addInterceptor(logging);  // <-- this is the important line!

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static GooglePlaceApiInterface getInstance() {
        if (apiInterface == null) {
            apiInterface = getRetrofit().create(GooglePlaceApiInterface.class);
        }
        return apiInterface;
    }

    public interface GooglePlaceApiInterface {
        @GET("autocomplete/json")
        Call<GooglePlaceResponse> getGooglePlace(@Query("input") String input,
                                                 @Query("types") String type,
                                                 @Query("key") String key);

        @GET("details/json")
        Call<JsonObject> getLatLngFromGooglePlaceId(@Query("placeid") String placeId,
                                                    @Query("key") String key);
    }
}
