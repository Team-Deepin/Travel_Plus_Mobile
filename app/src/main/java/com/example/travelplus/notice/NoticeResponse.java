package com.example.travelplus.notice;

import com.example.travelplus.BaseResponse;

import java.util.List;

public class NoticeResponse extends BaseResponse {
    public Data data;

    public static class Data {
        public int totalElements;
        public List<Notice> content;
    }
    public static class Notice {
        public int noticeId;
        public String title;
    }

}
