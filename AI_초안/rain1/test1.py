# rain_predict.py

import sys
import pandas as pd
import datetime
import joblib

# 콘솔 입력 받기
if len(sys.argv) != 3:
    print("사용법: python rain_predict.py <YYYY-MM-DD> <지역명>")
    sys.exit(1)

date_str = sys.argv[1]
region_name = sys.argv[2]

# 날짜 파싱
try:
    date = pd.to_datetime(date_str)
except ValueError:
    print("날짜 형식 오류: YYYY-MM-DD 형식으로 입력해주세요.")
    sys.exit(1)

# 피처 생성
month = date.month
day = date.day
weekday = date.weekday()
is_weekend = 1 if weekday >= 5 else 0

# 인코더, 모델 로드
label_encoder = joblib.load('label_encoder.pkl')
model = joblib.load('rain_model.pkl')

# 지점명 인코딩
try:
    region_encoded = label_encoder.transform([region_name])[0]
except ValueError:
    print(f"[오류] '{region_name}' 지역은 학습 데이터에 존재하지 않습니다.")
    sys.exit(1)

# 입력 피처 생성
input_data = pd.DataFrame([[region_encoded, month, day, weekday, is_weekend]],
                          columns=['지점명_encoded', 'month', 'day', 'weekday', 'is_weekend'])

# 예측
prob = model.predict_proba(input_data)[0][1]
pred = model.predict(input_data)[0]

print(f"\n🗓️ 날짜: {date_str}, 📍 지역: {region_name}")
print(f"🌧️ 강수 확률: {prob * 100:.2f}%")
print("📌 예측 결과:", "비가 올 것으로 예상됩니다." if pred == 1 else "비가 오지 않을 것으로 예상됩니다.")
