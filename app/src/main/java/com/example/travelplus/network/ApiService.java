package com.example.travelplus.network;

import com.example.travelplus.IsFirstResponse;
import com.example.travelplus.course.CourseDeleteResponse;
import com.example.travelplus.course.CourseRatingRequest;
import com.example.travelplus.course.CourseRatingResponse;
import com.example.travelplus.course.CourseResponse;
import com.example.travelplus.inquiry.InquireRequest;
import com.example.travelplus.inquiry.InquireResponse;
import com.example.travelplus.inquiry.InquiryResponse;
import com.example.travelplus.login.LoginRequest;
import com.example.travelplus.login.LoginResponse;
import com.example.travelplus.login.LogoutResponse;
import com.example.travelplus.login.WithdrawResponse;
import com.example.travelplus.notice.NoticeDetailResponse;
import com.example.travelplus.notice.NoticeResponse;
import com.example.travelplus.onboarding.OnboardingRequest;
import com.example.travelplus.onboarding.OnboardingResponse;
import com.example.travelplus.recommend.AIRecommendRequest;
import com.example.travelplus.recommend.AIRecommendResponse;
import com.example.travelplus.recommend.AISaveRequest;
import com.example.travelplus.recommend.AISaveResponse;
import com.example.travelplus.register.DuplicateCheckRequest;
import com.example.travelplus.register.DuplicateCheckResponse;
import com.example.travelplus.register.RegisterRequest;
import com.example.travelplus.register.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    @POST("/auth/onboarding")
    Call<OnboardingResponse> onboarding(@Body OnboardingRequest request);
    @POST("/auth/check")
    Call<DuplicateCheckResponse> duplicateCheck(@Body DuplicateCheckRequest request);
    @POST("/rating")
    Call<CourseRatingResponse> rate(@Body CourseRatingRequest request);
    @POST("/edit/inquire/submit")
    Call<InquireResponse> inquire(@Body InquireRequest request);
    @POST("/course/recommend")
    Call<AIRecommendResponse> recommend(@Body AIRecommendRequest request);
    @POST("/course/save")
    Call<AISaveResponse> save(@Body AISaveRequest request);
    @GET("/auth/logout")
    Call<LogoutResponse> logout();
    @GET("/home")
    Call<IsFirstResponse> getIsFirst();
    @GET("/course")
    Call<CourseResponse> course();
    @GET("/edit/inquire")
    Call<InquiryResponse> inquiry();

    @GET("/edit/notice")
    Call<NoticeResponse> getNotices(@Query("page") int page, @Query("size") int size);
    @GET("/edit/notice/{noticeId}")
    Call<NoticeDetailResponse> getNoticeDetail(@Path("noticeId") int noticeId);
    @DELETE("/course/delete/{courseId}")
    Call<CourseDeleteResponse> deleteCourse(@Path("courseId") int courseId);
    @DELETE("/edit/delete")
    Call<WithdrawResponse> withdraw();

}