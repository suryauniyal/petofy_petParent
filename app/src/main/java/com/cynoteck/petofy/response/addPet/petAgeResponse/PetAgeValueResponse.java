package com.cynoteck.petofy.response.addPet.petAgeResponse;


import com.cynoteck.petofy.response.Header;
import com.cynoteck.petofy.response.Response;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PetAgeValueResponse {
    private Header header;

    @SerializedName("data")
    private List<PetAgeModel> data = null;

    private Response response;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<PetAgeModel> getData() {
        return data;
    }

    public void setData(List<PetAgeModel> data) {
        this.data = data;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [cityModel = "+ data +", response = "+ response +", header = "+header+"]";
    }
}
