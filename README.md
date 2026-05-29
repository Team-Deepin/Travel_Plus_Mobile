# ✈️ 여행+ (Travel+) - Mobile

**Deepin팀의 맞춤형 AI 여행 코스 추천 안드로이드 애플리케이션**

## 📖 프로젝트 소개
'여행+'는 사용자의 취향과 설문조사 결과를 바탕으로 최적의 여행 코스를 AI로 추천해주고, 대중교통 및 자가용 경로를 상세히 안내해 주는 스마트 여행 가이드 앱입니다. 가천대학교 프로젝트 서버와 통신하여 작동합니다.

## ✨ 주요 기능
- **회원 및 인증 시스템**: 자체 이메일 회원가입/로그인 및 **카카오 소셜 로그인** 지원 (JWT 토큰 기반)
- **온보딩 및 성향 분석**: 초기 온보딩 및 설문조사(`Survey`)를 통한 사용자 맞춤형 여행 스타일 파악
- **AI 여행 추천 (`recommend`)**: 사용자의 취향을 반영한 AI 기반 여행 코스 맞춤 추천 및 결과 저장
- **코스 안내 및 기록 (`course`)**: 
  - 대중교통 및 자가용 기반 상세 경로(Transit/Car) 안내
  - 다녀온 여행 코스 히스토리 조회 및 코스 평가(Rating/별점) 시스템
- **고객 지원 및 부가 기능**:
  - 공지사항(`notice`) 및 1:1 문의(`inquiry`) 기능
  - 앱 내 테마 변경(`change_theme`) 기능 지원

## 🛠 기술 스택
- **Language**: Java
- **UI & Navigation**: Fragment, ViewPager2, TabLayout
- **Network**: Retrofit2, OkHttp3, Gson (REST API 통신)
- **Authentication**: Kakao SDK, SharedPreferences (토큰 로컬 저장)
- **IDE**: Android Studio

## 📂 프로젝트 주요 패키지 구조
- `login` / `register`: 회원가입, 일반/카카오 로그인 처리
- `main` / `home` / `more`: 탭 기반 메인 화면 및 마이페이지 처리
- `course`: 추천받은 코스 리스트, 이동 수단별 상세 경로 안내, 지난 코스 내역
- `recommend`: AI 맞춤 여행 추천 요청 및 결과 렌더링
- `survey`: 사용자 여행 취향 파악을 위한 설문조사
- `inquiry` / `notice`: 고객센터(1:1 문의) 및 공지사항 게시판
- `network`: Retrofit 클라이언트(`RetrofitClient`) 및 API 엔드포인트 인터페이스(`ApiService`)
- `util`: 응답 포맷(`BaseResponse`) 및 커스텀 뷰

## 🚀 실행 방법
1. Repository를 로컬로 클론합니다.
2. Android Studio에서 프로젝트를 엽니다. (자동으로 Gradle Sync가 진행됩니다.)
3. 카카오 소셜 로그인을 위해 `AndroidManifest.xml` 내의 카카오 네이티브 앱 키 설정 확인 및 로컬 환경 셋팅을 진행합니다.
4. 에뮬레이터 또는 Android 실기기(Target API 31)에서 앱을 빌드하고 실행합니다.
