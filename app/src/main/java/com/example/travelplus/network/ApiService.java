package com.example.travelplus.network;

import com.example.travelplus.BaseResponse;
import com.example.travelplus.course.CourseHistoryResponse;
import com.example.travelplus.home.HomeResponse;
import com.example.travelplus.change.ChangeThemeRequest;
import com.example.travelplus.change.ChangeThemeResponse;
import com.example.travelplus.course.CourseDeleteResponse;
import com.example.travelplus.course.CourseDetailCarResponse;
import com.example.travelplus.course.CourseDetailTransitResponse;
import com.example.travelplus.course.CourseRatingRequest;
import com.example.travelplus.course.CourseRatingResponse;
import com.example.travelplus.course.CourseResponse;
import com.example.travelplus.inquiry.InquireRequest;
import com.example.travelplus.inquiry.InquiryResponse;
import com.example.travelplus.login.KakaoLoginRequest;
import com.example.travelplus.login.LoginRequest;
import com.example.travelplus.notice.NoticeDetailResponse;
import com.example.travelplus.notice.NoticeResponse;
import com.example.travelplus.onboarding.OnboardingRequest;
import com.example.travelplus.recommend.AIRecommendRequest;
import com.example.travelplus.recommend.AIRecommendResponse;
import com.example.travelplus.recommend.AISaveRequest;
import com.example.travelplus.register.DuplicateCheckRequest;
import com.example.travelplus.register.DuplicateCheckResponse;
import com.example.travelplus.register.RegisterRequest;
import com.example.travelplus.survey.SurveyRequest;
import com.example.travelplus.survey.SurveyResponse;
import com.example.travelplus.survey.SurveySaveRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/auth/login")
    Call<BaseResponse> login(@Body LoginRequest request);
    @POST("/auth/kakaoLogin")
    Call<BaseResponse> kakao(@Body KakaoLoginRequest request);
    @POST("/auth/register")
    Call<BaseResponse> register(@Body RegisterRequest request);
    @POST("/auth/onboarding")
    Call<BaseResponse> onboarding(@Body OnboardingRequest request);
    @POST("/auth/check")
    Call<DuplicateCheckResponse> duplicateCheck(@Body DuplicateCheckRequest request);
    @POST("/rating")
    Call<BaseResponse> rate(@Body CourseRatingRequest request);
    @POST("/edit/inquire/submit")
    Call<BaseResponse> inquire(@Body InquireRequest request);
    @POST("/course/recommend")
    Call<AIRecommendResponse> recommend(@Body AIRecommendRequest request);
    @POST("/course/save")
    Call<BaseResponse> aiSave(@Body AISaveRequest request);
    @POST("/course/save")
    Call<BaseResponse> surveySave(@Body SurveySaveRequest request);
    @POST("/course/survey")
    Call<SurveyResponse> survey(@Body SurveyRequest request);
    @GET("/auth/logout")
    Call<BaseResponse> logout();
    @GET("/auth/home")
    Call<HomeResponse> home();
    @GET("/course")
    Call<CourseResponse> course();
    @GET("/course/history")
    Call<CourseHistoryResponse> courseHistory();
    @GET("/course/detail/car")
    Call<CourseDetailCarResponse> detailCar(@Query("courseId") int courseId);
    @GET("/course/detail/transit")
    Call<CourseDetailTransitResponse> detailTransit(@Query("courseId") int courseId);
    @GET("/edit/inquire")
    Call<InquiryResponse> inquiry();
    @GET("/edit/notice")
    Call<NoticeResponse> getNotices(@Query("page") int page, @Query("size") int size);
    @GET("/edit/notice/{noticeId}")
    Call<NoticeDetailResponse> getNoticeDetail(@Path("noticeId") int noticeId);
    @PUT("/edit/modifyUser")
    Call<BaseResponse> change(@Body ChangeThemeRequest request);
    @DELETE("/course/delete/{courseId}")
    Call<BaseResponse> deleteCourse(@Path("courseId") int courseId);
    @DELETE("/edit/delete")
    Call<BaseResponse> withdraw();

}