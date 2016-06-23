package com.hodachop93.hohoda.model;

import java.util.List;

/**
 * Created by Hop Dac Ho on 23/04/2016.
 */
public class GooglePlaceResponse {
    private String status;
    private List<GooglePlace> predictions;

    public String getStatus() {
        return status;
    }

    public List<GooglePlace> getPredictions() {
        return predictions;
    }
}
