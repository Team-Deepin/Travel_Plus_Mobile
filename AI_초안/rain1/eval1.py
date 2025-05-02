# evaluate_rain_model.py

import pandas as pd
import joblib
from sklearn.metrics import classification_report

# 데이터 로드 및 전처리
df = pd.read_csv('./data/기상데이터_forai.csv')
df.dropna(subset=['일강수량(mm)'], inplace=True)
df['일시'] = pd.to_datetime(df['일시'])
df['강수여부'] = df['일강수량(mm)'].apply(lambda x: 1 if x > 1 else 0)
df['month'] = df['일시'].dt.month
df['day'] = df['일시'].dt.day
df['weekday'] = df['일시'].dt.weekday
df['is_weekend'] = df['weekday'].apply(lambda x: 1 if x >= 5 else 0)

label_encoder = joblib.load('label_encoder.pkl')
df['지점명_encoded'] = label_encoder.transform(df['지점명'])

X = df[['지점명_encoded', 'month', 'day', 'weekday', 'is_weekend']]
y = df['강수여부']

model = joblib.load('rain_model.pkl')
y_pred = model.predict(X)

print("\n[전체 데이터 평가 결과]\n", classification_report(y, y_pred))
