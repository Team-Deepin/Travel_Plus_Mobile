import pandas as pd
import numpy as np
import joblib
from datetime import datetime, timedelta
from lightfm import LightFM
import os

# 시즌 변환 함수
def get_season(month):
    if month in [3, 4, 5]:
        return 0  # 봄
    elif month in [6, 7, 8]:
        return 1  # 여름
    elif month in [9, 10, 11]:
        return 2  # 가을
    else:
        return 3  # 겨울

# 날짜 리스트 생성 함수
def generate_schedule(start_date, end_date):
    date_list = pd.date_range(start=start_date, end=end_date).strftime('%Y-%m-%d').tolist()
    return date_list

# 예측 함수
def recommend(user_id, start_date, end_date, means_tp, rain_rate, 
              model_path='lightfm_model.pkl', dataset_path='lightfm_dataset.pkl',
              user_path='./data/사용자.csv', travel_path='./data/여행.csv', poi_path='./data/여행지.csv'):

    # 모델 로드
    model = joblib.load(model_path)
    dataset = joblib.load(dataset_path)

    # 데이터 로드
    user_df = pd.read_csv(user_path, encoding='utf-8-sig')
    travel_df = pd.read_csv(travel_path, encoding='utf-8-sig')
    poi_df = pd.read_csv(poi_path, encoding='utf-8-sig')

    # ① user_id의 과거 여행 기록
    user_travel = travel_df[travel_df['user_id'] == user_id]

    # ② user_id로 gender, age 추출
    user_info = user_df[user_df['user_id'] == user_id].iloc[0]
    gender, age = user_info['gender'], user_info['age']

    # 비슷한 조건의 사용자 필터
    similar_users = user_df[
        (user_df['gender'] == gender) & 
        (abs(user_df['age'] - age) <= 5)
    ]['user_id'].tolist()

    # ③ 시즌 추출
    season = get_season(pd.to_datetime(start_date).month)

    # 비슷한 시즌의 여행 필터
    season_travel = travel_df[
        (travel_df['season'] == season) & 
        (travel_df['user_id'].isin(similar_users)) &
        (travel_df['means_tp'] == means_tp)
    ]

    # 추천 대상 POI
    all_pois = poi_df['contentid'].unique()

    # user_id와 모든 poi 조합에 대해 점수 예측
    user_idx = dataset.mapping()[0].get(user_id)
    if user_idx is None:
        print(f"Error: user_id {user_id} not found in dataset.")
        return []

    poi_scores = []
    for poi_id in all_pois:
        item_idx = dataset.mapping()[2].get(poi_id)
        if item_idx is not None:
            score = model.predict(user_ids=np.array([user_idx]),
                                  item_ids=np.array([item_idx]))[0]
            poi_scores.append((poi_id, score))

    # 점수 높은 순 정렬
    poi_scores = sorted(poi_scores, key=lambda x: -x[1])

    # ⑤ rain_rate 기반 is_outdoor 가중치 적용
    is_rainy = rain_rate > 50  # 강수확률 50% 이상이면 우천 가중치
    filtered_pois = []
    for poi_id, score in poi_scores:
        row = poi_df[poi_df['contentid'] == poi_id]
        if row.empty:
            continue

        is_outdoor = row.iloc[0]['is_outdoor']
        if pd.isna(is_outdoor):
            is_outdoor = 0  # 기본값: 실내라고 가정
        else:
            is_outdoor = int(is_outdoor)

        if is_rainy and is_outdoor:
            score *= 0.5  # 실외 점수 하향

        filtered_pois.append((poi_id, score))

    # ④ 일정 생성
    date_list = generate_schedule(start_date, end_date)

    # 상위 N개 추천
    top_pois = sorted(filtered_pois, key=lambda x: -x[1])[:len(date_list) * 5]  # 날짜수 * 5개씩 추천

    # 날짜별 추천 코스 포맷팅
    result = {}
    idx = 0
    for date in date_list:
        places = []
        for _ in range(5):
            poi_id, _ = top_pois[idx]
            title = poi_df[poi_df['contentid'] == poi_id].iloc[0]['title']
            tag = poi_df[poi_df['contentid'] == poi_id].iloc[0]['tag']
            if tag == "숙박":
                continue  # 숙박 제외 규칙
            places.append(title)
            idx += 1
        result[date] = places

    return result

if __name__ == "__main__":
    reco = recommend(
        user_id='e000005',
        start_date='2025-05-10',
        end_date='2025-05-12',
        means_tp='1',
        rain_rate=60  # 60% 강수확률
    )

    for date, places in reco.items():
        print(f"{date}: {' - '.join(places)}")
