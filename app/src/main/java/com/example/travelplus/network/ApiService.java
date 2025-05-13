package com.example.travelplus.network;

import com.example.travelplus.course.CoursePastResponse;
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
import com.example.travelplus.inquiry.InquireResponse;
import com.example.travelplus.inquiry.InquiryResponse;
import com.example.travelplus.login.KakaoLoginActivity;
import com.example.travelplus.login.KakaoResponse;
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
import com.example.travelplus.survey.SurveyRequest;
import com.example.travelplus.survey.SurveyResponse;
import com.example.travelplus.survey.SurveySaveRequest;
import com.example.travelplus.survey.SurveySaveResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/auth/register")
    Call<RegisterResponse> register(@Header("Authorization") String authorization, @Body RegisterRequest request);
    @POST("/auth/onboarding")
    Call<OnboardingResponse> onboarding(@Header("Authorization") String authorization, @Body OnboardingRequest request);
    @POST("/auth/check")
    Call<DuplicateCheckResponse> duplicateCheck(@Header("Authorization") String authorization, @Body DuplicateCheckRequest request);
    @POST("/rating")
    Call<CourseRatingResponse> rate(@Header("Authorization") String authorization, @Body CourseRatingRequest request);
    @POST("/edit/inquire/submit")
    Call<InquireResponse> inquire(@Header("Authorization") String authorization, @Body InquireRequest request);
    @POST("/course/recommend")
    Call<AIRecommendResponse> recommend(@Header("Authorization") String authorization, @Body AIRecommendRequest request);
    @POST("/course/save")
    Call<AISaveResponse> aiSave(@Header("Authorization") String authorization, @Body AISaveRequest request);
    @POST("/course/save")
    Call<SurveySaveResponse> surveySave(@Header("Authorization") String authorization, @Body SurveySaveRequest request);
    @POST("/course/survey")
    Call<SurveyResponse> survey(@Header("Authorization") String authorization, @Body SurveyRequest request);
    @GET("/auth/kakao")
    Call<KakaoResponse> kakao(@Query("code") String code);
    @GET("/auth/logout")
    Call<LogoutResponse> logout();
    @GET("/auth/home")
    Call<HomeResponse> home(@Header("Authorization") String authorization);
    @GET("/course")
    Call<CourseResponse> course(@Header("Authorization") String authorization);
    @GET("/course/past")
    Call<CoursePastResponse> coursePast(@Header("Authorization") String authorization);
    @GET("/course/detail/car")
    Call<CourseDetailCarResponse> detailCar(@Header("Authorization") String authorization, @Query("courseId") int courseId);
    @GET("/course/detail/transit")
    Call<CourseDetailTransitResponse> detailTransit(@Header("Authorization") String authorization, @Query("courseId") int courseId);
    @GET("/edit/inquire")
    Call<InquiryResponse> inquiry(@Header("Authorization") String authorization);
    @GET("/edit/notice")
    Call<NoticeResponse> getNotices(@Header("Authorization") String authorization, @Query("page") int page, @Query("size") int size);
    @GET("/edit/notice/{noticeId}")
    Call<NoticeDetailResponse> getNoticeDetail(@Header("Authorization") String authorization, @Path("noticeId") int noticeId);
    @PUT("/edit/userModify")
    Call<ChangeThemeResponse> change(@Header("Authorization") String authorization, @Body ChangeThemeRequest request);
    @DELETE("/course/delete/{courseId}")
    Call<CourseDeleteResponse> deleteCourse(@Header("Authorization") String authorization, @Path("courseId") int courseId);
    @DELETE("/edit/delete")
    Call<WithdrawResponse> withdraw();

}