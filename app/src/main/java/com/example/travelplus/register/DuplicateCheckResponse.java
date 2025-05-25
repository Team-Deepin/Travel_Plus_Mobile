package com.example.travelplus.register;

import com.example.travelplus.BaseResponse;

public class DuplicateCheckResponse extends BaseResponse {
    public Data data;

    public static class Data {
        public Boolean duplication;
    }
}
