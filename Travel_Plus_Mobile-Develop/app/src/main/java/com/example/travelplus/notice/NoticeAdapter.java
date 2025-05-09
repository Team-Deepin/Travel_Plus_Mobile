package com.example.travelplus.notice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplus.R;

import java.util.ArrayList;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    private final List<NoticeResponse.Notice> noticeList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NoticeResponse.Notice notice);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<NoticeResponse.Notice> items) {
        noticeList.clear();
        noticeList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notice_list, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeResponse.Notice notice = noticeList.get(position);
        String rawDate = notice.date;
        String formatted = rawDate.split("T")[0].replace("-", ".");
        holder.noticeDate.setText(formatted);
        holder.noticeTitle.setText(notice.title);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(notice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView noticeTitle;
        TextView noticeDate;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            noticeTitle = itemView.findViewById(R.id.notice_title);
            noticeDate = itemView.findViewById(R.id.notice_date);
        }
    }
}



