package com.example.travelplus.register;

import com.example.travelplus.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class DuplicateCheckResponse extends BaseResponse {
    public Data data;

    public static class Data {
        @SerializedName("duplication")
        public Boolean duplication;
    }
}
