package com.takealookonit.roottracer.backend.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

public class PointWrapper {

    @SerializedName("points")
    @Expose
    private List<Point> points = null;

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

}
