# train_rain_model.py

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.metrics import classification_report
from sklearn.preprocessing import LabelEncoder
from sklearn.ensemble import GradientBoostingClassifier
import joblib

# 데이터 로드
df = pd.read_csv('./data/기상데이터_forai.csv')

# 전처리
df.dropna(subset=['일강수량(mm)'], inplace=True)
df['일시'] = pd.to_datetime(df['일시'])
df['강수여부'] = df['일강수량(mm)'].apply(lambda x: 1 if x > 1 else 0)

# 날짜 정보 추가
df['month'] = df['일시'].dt.month
df['day'] = df['일시'].dt.day
df['weekday'] = df['일시'].dt.weekday
df['is_weekend'] = df['weekday'].apply(lambda x: 1 if x >= 5 else 0)

# 지점명 인코딩
label_encoder = LabelEncoder()
df['지점명_encoded'] = label_encoder.fit_transform(df['지점명'])

# 특성과 타깃 설정
X = df[['지점명_encoded', 'month', 'day', 'weekday', 'is_weekend']]
y = df['강수여부']

# 학습/검증 분할
X_train, X_test, y_train, y_test = train_test_split(X, y, stratify=y, test_size=0.2, random_state=42)

# 모델 및 하이퍼파라미터 튜닝
param_grid = {
    'n_estimators': [100, 200],
    'learning_rate': [0.05, 0.1, 0.2],
    'max_depth': [3, 5, 7]
}

model = GradientBoostingClassifier()
grid_search = GridSearchCV(model, param_grid, cv=3, scoring='f1', n_jobs=-1)
grid_search.fit(X_train, y_train)

print("최적의 하이퍼파라미터:", grid_search.best_params_)
best_model = grid_search.best_estimator_

# 평가
y_pred = best_model.predict(X_test)
print("\n[분류 평가 결과]\n", classification_report(y_test, y_pred))

# 모델 및 인코더 저장
joblib.dump(best_model, 'rain_model.pkl')
joblib.dump(label_encoder, 'label_encoder.pkl')
