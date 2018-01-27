package com.takealookonit.roottracer.backend;

import com.takealookonit.roottracer.backend.models.UserWrapper;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

interface API {

    @GET("getUsers.php")
    Call<UserWrapper> getUsers();

}
