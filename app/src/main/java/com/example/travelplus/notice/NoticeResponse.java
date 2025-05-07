package com.example.travelplus.notice;

import java.util.List;

public class NoticeResponse {
    public int resultCode;
    public String resultMessage;
    public Data data;

    public static class Data {
        public int totalCount;
        public List<Notice> notices;
    }
    public static class Notice {
        public int noticeId;
        public String title;
        public String date;
    }

}
