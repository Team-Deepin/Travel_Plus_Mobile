import torch
import torch.nn as nn
import numpy as np
import pandas as pd
from datetime import datetime
import sys
import os
sys.path.append(os.path.join('.', 'utils'))
from formatter import format_prediction_output

# ✅ 지역명 → areacode 매핑
area_mapping = {
    '서울': 1, '인천': 2, '대전': 3, '대구': 4, '광주': 5, '부산': 6, '울산': 7,
    '세종': 8, '경기': 31, '강원': 32, '충북': 33, '충남': 34, '경북': 35,
    '경남': 36, '전북': 37, '전남': 38, '제주': 39
}

class MLP(nn.Module):
    def __init__(self, input_dim, hidden_dim, output_dim):
        super(MLP, self).__init__()
        self.model = nn.Sequential(
            nn.Linear(input_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, output_dim)
        )

    def forward(self, x):
        return self.model(x)

def get_season_from_date(date_str):
    month = pd.to_datetime(date_str).month
    if 3 <= month <= 5:
        return 0  # 봄
    elif 6 <= month <= 8:
        return 1  # 여름
    elif 9 <= month <= 11:
        return 2  # 가을
    else:
        return 3  # 겨울

def get_days_between(start_date, end_date):
    start = pd.to_datetime(start_date)
    end = pd.to_datetime(end_date)
    return (end - start).days + 1

def load_poi_data():
    poi = pd.read_csv('data/여행지.csv', encoding='utf-8-sig')
    return poi

def filter_poi_by_place(poi_data, place_name):
    area_code = area_mapping.get(place_name)
    if area_code is None:
        raise ValueError(f"입력한 지역명 '{place_name}'은 지원되지 않습니다.")
    filtered = poi_data[poi_data['areacode'] == area_code].reset_index(drop=True)
    return filtered

def predict_satisfaction_and_recommend(user_input):
    poi_data = load_poi_data()
    filtered_poi = filter_poi_by_place(poi_data, user_input['place'])

    # ✅ 태그 1~12 모두 멀티핫 인코딩
    all_tags = list(range(1, 13))

    selected_tags = list(map(int, user_input['travel_type'].split(';')))
    travel_type_encoded = [1 if tag in selected_tags else 0 for tag in all_tags]

    is_outdoor = 1.0 - user_input['rain_rate']
    season = get_season_from_date(user_input['start_date'])

    feature_list = [
        user_input['gender'],
        user_input['age'],
        season,
        user_input['means_tp'],
        user_input['person'],
        is_outdoor
    ] + travel_type_encoded

    feature_array = np.array(feature_list, dtype='float32')
    feature_array = np.nan_to_num(feature_array, nan=-999.0)

    X_tensor = torch.tensor(feature_array, dtype=torch.float32).unsqueeze(0)

    model = MLP(input_dim=X_tensor.shape[1], hidden_dim=128, output_dim=1)
    model.load_state_dict(torch.load("mlp_model.pth", weights_only=True))
    model.eval()

    num_days = get_days_between(user_input['start_date'], user_input['end_date'])
    dates = pd.date_range(user_input['start_date'], user_input['end_date']).strftime('%Y-%m-%d').tolist()

    # ✅ 관광지 vs 음식/카페 분리 (tag가 '5'인 경우 음식/카페) 및 tag '8' 제외
    filtered_poi['tag'] = filtered_poi['tag'].fillna('').astype(str)
    tourist_poi = filtered_poi[(filtered_poi['tag'] != '8') & (filtered_poi['tag'] != '5')]
    food_poi = filtered_poi[(filtered_poi['tag'] == '8')]

    # ✅ 3개 코스 생성
    courses = []
    for course_idx in range(3):
        schedule = {}
        for date in dates:
            selected_titles = []
            
            # 1. 관광지 3개 랜덤 샘플
            tourist_samples = tourist_poi.sample(n=min(3, len(tourist_poi)), replace=True)
            selected_titles.extend(tourist_samples['title'].tolist())

            # 2. 음식/카페 2개 랜덤 샘플
            food_samples = food_poi.sample(n=min(2, len(food_poi)), replace=True)
            food_titles = food_samples['title'].tolist()

            # ✅ 위치 룰 적용 (2번째, 5번째)
            day_titles = [selected_titles[0], food_titles[0], selected_titles[1], selected_titles[2], food_titles[1]]
            schedule[date] = day_titles

        course = {
            'area_name': f"{user_input['place']}",
            'schedule': schedule
        }
        courses.append(course)

    return courses

if __name__ == "__main__":
    user_input = {
        'gender': 0,  # 0=남자, 1=여자
        'age': 30,
        'place': '서울',
        'start_date': '2025-05-05',
        'end_date': '2025-05-07',
        'means_tp': 1,
        'person': 2,
        'travel_type': '1;3;6',  # ✅ 태그 1~12 중 선택
        'rain_rate': 0.3
    }

    result = predict_satisfaction_and_recommend(user_input)
    print(format_prediction_output(result))