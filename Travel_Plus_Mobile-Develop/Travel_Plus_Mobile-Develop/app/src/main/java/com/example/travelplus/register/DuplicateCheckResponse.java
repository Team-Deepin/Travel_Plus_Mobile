package com.example.travelplus.register;

import com.google.gson.annotations.SerializedName;

public class DuplicateCheckResponse {
    public int resultCode;
    public String resultMessage;
    public Data data;

    public static class Data {
        @SerializedName("duplication")
        public Boolean duplication;
    }
}
