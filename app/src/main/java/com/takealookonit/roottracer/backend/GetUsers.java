package com.takealookonit.roottracer.backend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.takealookonit.roottracer.backend.models.UserWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

public class GetUsers {

    private GetUserInterface getUserInterface;

    public GetUsers(GetUserInterface getUserInterface) {
        this.getUserInterface = getUserInterface;
    }

    public interface GetUserInterface {
        void getUserWrapper(UserWrapper userWrapper);
    }

    public void getUserWrapper() {
        API service = BaseURL.getRetrofit();
        Call<UserWrapper> call = service.getUsers();
        call.enqueue(new Callback<UserWrapper>() {
            @Override
            public void onResponse(@NonNull Call<UserWrapper> call,
                                   @NonNull Response<UserWrapper> response) {
                Log.d("mylog", "server - GetUsers " + response.code());
                getUserInterface.getUserWrapper(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<UserWrapper> call,
                                  @NonNull Throwable t) {
                Log.d("mylog", "server - GetUsers " + t.getLocalizedMessage());
                getUserInterface.getUserWrapper(null);
            }
        });
    }

}