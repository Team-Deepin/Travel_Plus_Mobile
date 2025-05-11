package com.example.travelplus.inquiry;

import java.util.List;

public class InquiryResponse {
    public int resultCode;
    public String resultMessage;
    public List<Inquiry> data;

    public static class Inquiry {
        public int inquireId;
        public String title;
        public String content;
        public String createDate;
        public boolean isAnswered;
        public String answer;
        public String answerDate;

    }
}
