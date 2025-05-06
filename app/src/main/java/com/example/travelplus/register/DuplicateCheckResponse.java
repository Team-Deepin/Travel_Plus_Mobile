package com.example.travelplus.register;

import com.google.gson.annotations.SerializedName;

public class DuplicateCheckResponse {
    int resultCode;
    String resultMessage;
    public Data data;

    public static class Data {
        @SerializedName("duplication")
        public Boolean duplication;
    }
}
