package com.example.graduationproject.network;

import com.example.graduationproject.models.Profile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
    @FormUrlEncoded
    @PUT("registration.php")
    Call<ResponseBody> insertNewProfile(@Field("firstname") String firstname,
                                        @Field("lastname") String lastname,
                                        @Field("email") String email,
                                        @Field("birthDate") String birthDate,
                                        @Field("gender") String gender,
                                        @Field("password") String password,
                                        @Field("phoneNumber") String phoneNumber,
                                        @Field("profileType") String profileType);

    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> loginCheck(@Field("email") String email,
                                  @Field("password") String password);

    @PUT("updateLoginState.php")
    Call<String> updateLogin(@Field("email") String email);
}
