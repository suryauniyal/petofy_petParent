package com.cynoteck.petofy.response.getPetReportsResponse;


import com.cynoteck.petofy.response.Header;
import com.cynoteck.petofy.response.Response;

import java.util.ArrayList;

public class GetReportsTypeResponse {
    private ArrayList<GetReportsTypeData> data;

    private Response response;

    private Header header;

    public ArrayList<GetReportsTypeData> getData ()
    {
        return data;
    }

    public void setData (ArrayList<GetReportsTypeData> data)
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
