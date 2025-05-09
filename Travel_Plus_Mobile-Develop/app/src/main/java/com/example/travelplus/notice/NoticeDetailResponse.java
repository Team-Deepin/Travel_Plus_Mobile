package com.example.travelplus.notice;

public class NoticeDetailResponse {
    public int resultCode;
    public String resultMessage;
    public Data data;

    public static class Data {
        public String title;
        public String content;
        public String date;
    }
}

