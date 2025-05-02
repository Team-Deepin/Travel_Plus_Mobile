import pandas as pd
from datetime import timedelta

def preprocess_visit_data_for_ai(visit_data, poi_data):
    """
    여행 방문지 데이터를 AI 학습에 적합하게 전처리하는 함수
    """
    # 날짜 형식으로 변환
    visit_data['day'] = pd.to_datetime(visit_data['day'], errors='coerce')

    # 여행 방문지의 각 여행에 대한 기간 계산 (최초 방문일과 마지막 방문일 차이)
    visit_data["duration"] = visit_data.groupby('course_id')['day'].transform('max') - visit_data.groupby('course_id')['day'].transform('min')

    # 여행 방문지의 순서대로 정렬
    visit_data = visit_data.sort_values(by=['course_id', 'sequence'])

    # 방문지 정보(poi_data)와 결합하여 태그 정보 추가
    visit_data = pd.merge(visit_data, poi_data[['contentid', 'tag']], on='contentid', how='left')

    # 방문지 순서 정보를 숫자형으로 변환하여 feature로 추가
    visit_data['sequence'] = visit_data['sequence'].astype(int)

    return visit_data

def preprocess_all_data_for_ai(user_data, travel_data, visit_data_processed, poi_data):
    # 데이터 병합
    data = pd.merge(user_data, travel_data, on="user_id", how="inner")
    data = pd.merge(data, visit_data_processed, on="course_id", how="inner")
    data = pd.merge(data, poi_data, on="contentid", how="inner")
    
    # tag_x와 tag_y를 합쳐서 tag 컬럼 생성
    data['tag'] = data['tag_x'].fillna(data['tag_y'])
    
    # 불필요한 tag_x, tag_y 컬럼 제거
    data = data.drop(['tag_x', 'tag_y'], axis=1)
    
    # selected_columns에서 tag 컬럼을 포함하도록 수정
    selected_columns = ['course_id', 'user_id', 'satisfaction', 'travel_type', 'person', 'season', 'means_tp', 'areacode', 
                        'gender', 'age', 'course_dt_id', 'sequence', 'place_name', 'day', 'contentid', 'duration', 'tag', 'is_outdoor']
    data = data[selected_columns]
    
    return data


# Example usage for AI learning and prediction:
def get_preprocessed_data(user_path, travel_path, visit_path, poi_path):
    """
    전체 데이터를 불러와 전처리하여 반환하는 함수
    """
    # 각 파일을 불러오기
    user_data = pd.read_csv(user_path)
    travel_data = pd.read_csv(travel_path)
    visit_data = pd.read_csv(visit_path)
    poi_data = pd.read_csv(poi_path)


    # 방문지 데이터 전처리
    visit_data_processed = preprocess_visit_data_for_ai(visit_data, poi_data)

    # 모든 데이터 전처리 및 병합
    processed_data = preprocess_all_data_for_ai(user_data, travel_data, visit_data_processed, poi_data)

    return processed_data
