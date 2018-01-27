package com.takealookonit.roottracer.backend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.takealookonit.roottracer.backend.models.PointWrapper;
import com.takealookonit.roottracer.backend.models.UserWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

public class GetPoints {

    private GetPointsInterface getPointsInterface;

    public GetPoints(GetPointsInterface getPointsInterface) {
        this.getPointsInterface = getPointsInterface;
    }

    public interface GetPointsInterface {
        void getPointsWrapper(PointWrapper pointWrapper);
    }

    public void getPointsWrapper() {
        API service = BaseURL.getRetrofit();
        Call<PointWrapper> call = service.getPoints();
        call.enqueue(new Callback<PointWrapper>() {
            @Override
            public void onResponse(@NonNull Call<PointWrapper> call,
                                   @NonNull Response<PointWrapper> response) {
                Log.d("mylog", "server - GetPoints " + response.code());
                getPointsInterface.getPointsWrapper(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<PointWrapper> call,
                                  @NonNull Throwable t) {
                Log.d("mylog", "server - GetPoints " + t.getLocalizedMessage());
                getPointsInterface.getPointsWrapper(null);
            }
        });
    }

}
