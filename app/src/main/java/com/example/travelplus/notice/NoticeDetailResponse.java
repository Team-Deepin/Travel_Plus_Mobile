package com.example.travelplus.notice;

import com.example.travelplus.util.BaseResponse;

public class NoticeDetailResponse extends BaseResponse {
    public Data data;

    public static class Data {
        public String title;
        public String content;
        public String date;
    }
}

