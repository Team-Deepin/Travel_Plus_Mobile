package com.example.travelplus.inquiry;

import com.example.travelplus.BaseResponse;

import java.util.List;

public class InquiryResponse extends BaseResponse {
    public List<Inquiry> data;

    public static class Inquiry {
        public int id;
        public String title;
        public String content;
        public String createdDate;
        public boolean answered;
        public String answer;
        public String answerDate;

    }
}
