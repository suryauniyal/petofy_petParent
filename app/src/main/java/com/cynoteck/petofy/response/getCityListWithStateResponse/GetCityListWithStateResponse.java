package com.cynoteck.petofy.response.getCityListWithStateResponse;

import com.cynoteck.petofy.response.Header;
import com.cynoteck.petofy.response.Response;

import java.util.List;

public class GetCityListWithStateResponse {
    private List<GetCityListWithData> data;

    private Response response;

    private Header header;

    public List<GetCityListWithData> getData ()
    {
        return data;
    }

    public void setData (List<GetCityListWithData> data)
    {
        this.data = data;
    }

    public Response getResponse ()
    {
        return response;
    }

    public void setResponse (Response response)
    {
        this.response = response;
    }

    public Header getHeader ()
    {
        return header;
    }

    public void setHeader (Header header)
    {
        this.header = header;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", response = "+response+", header = "+header+"]";
    }
}


