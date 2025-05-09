package com.example.travelplus.inquiry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplus.R;

public class InquiryAnswerFragment extends Fragment {
    int id;
    String title, content, answer;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = (int) getArguments().get("inquiryId");
            title = getArguments().getString("title");
            content = getArguments().getString("content");
            if (getArguments().getString("answer") != null){
                answer = getArguments().getString("answer");
            }
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inquiry_answer, container, false);
        TextView inquiryTitle = view.findViewById(R.id.inquiry_title);
        TextView inquiryDetail = view.findViewById(R.id.inquiry_detail);
        TextView inquiryAnswer = view.findViewById(R.id.answer_content);

        inquiryTitle.setText(title);
        inquiryDetail.setText(content);
        if(answer != null && !answer.isEmpty()){
            inquiryAnswer.setText(answer);
        }
        return view;
    }
}
