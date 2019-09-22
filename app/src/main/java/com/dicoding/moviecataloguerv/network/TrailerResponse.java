package com.dicoding.moviecataloguerv.network;

import com.dicoding.moviecataloguerv.model.Trailer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrailerResponse {
    @SerializedName("results")
    @Expose
    private ArrayList<Trailer> trailers;

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }
}
