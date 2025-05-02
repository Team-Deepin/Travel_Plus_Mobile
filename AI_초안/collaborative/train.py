import pandas as pd
from lightfm import LightFM
from lightfm.data import Dataset
import joblib
import sys
import os

sys.path.append(os.path.join('.', 'utils'))
from preprocessing import get_preprocessed_data

def train_lightfm_model(user_data, travel_data, visit_data_processed, poi_data):
    print("\n=== travel_data 컬럼 목록 ===")
    print(travel_data.columns.tolist())

    print("\n=== visit_data_processed 컬럼 목록 ===")
    print(visit_data_processed.columns.tolist())

    # user_id - contentid - satisfaction 매핑 만들기 위해 merge
    merged = pd.merge(visit_data_processed, travel_data[['course_id', 'user_id', 'satisfaction']], on='course_id', how='left')

    # 체크: merged 데이터가 충분한지 확인
    if merged.empty:
        print("경고: merged 데이터가 비어있습니다!")
        return None, None

    # NaN 처리: satisfaction 값이 NaN인 경우 0으로 대체
    merged['satisfaction_y'] = merged['satisfaction_y'].fillna(0)

    # satisfaction 점수 정규화 (0~1)
    merged['satisfaction_normalized'] = merged['satisfaction_y'] / 5.0

    # Dataset 정의
    dataset = Dataset()
    dataset.fit(users=merged['user_id_x'].unique(),
                items=merged['contentid'].unique())

    # interactions 리스트 생성
    interactions = list(zip(merged['user_id_x'], merged['contentid'], merged['satisfaction_normalized']))

    # Interaction matrix 빌드
    interaction_matrix, _ = dataset.build_interactions(interactions)

    # 모델 정의 및 전체 학습
    model = LightFM(loss='bpr')

    try:
        print("\n모델 훈련 시작... (전체 matrix 방식)")
        model.fit(interaction_matrix, epochs=20, num_threads=4)
        print("모델 훈련이 완료되었습니다.")
    except Exception as e:
        print(f"모델 훈련 중 오류 발생: {e}")
        return None, None

    return model, dataset

if __name__ == "__main__":
    # 파일 경로
    user_path = './data/사용자.csv'
    travel_path = './data/여행.csv'
    visit_path = './data/여행방문지.csv'
    poi_path = './data/여행지.csv'

    # 데이터 읽기
    print("\n데이터 파일 읽기 중...")
    try:
        user_data = pd.read_csv(user_path, encoding='utf-8-sig')
        travel_data = pd.read_csv(travel_path, encoding='utf-8-sig')
        poi_data = pd.read_csv(poi_path, encoding='utf-8-sig')
        print("데이터 파일 읽기 완료.")
    except Exception as e:
        print(f"데이터 파일 읽기 오류: {e}")
        sys.exit(1)

    # 전처리
    print("\n전처리 시작...")
    try:
        visit_data_processed = get_preprocessed_data(user_path, travel_path, visit_path, poi_path)
        print("전처리 완료.")
    except Exception as e:
        print(f"전처리 중 오류 발생: {e}")
        sys.exit(1)

    # 디버깅: 전처리된 데이터 확인
    print("\n=== visit_data_processed 샘플 ===")
    print(visit_data_processed.head())

    # 모델 훈련
    model, dataset = train_lightfm_model(user_data, travel_data, visit_data_processed, poi_data)

    # 디버깅: 모델이 None인 경우 확인
    if model is not None:
        print("모델 훈련 완료")
        # 모델 저장
        try:
            joblib.dump(model, 'lightfm_model.pkl')
            joblib.dump(dataset, 'lightfm_dataset.pkl')
            print("모델 훈련 완료 및 저장되었습니다.")
        except Exception as e:
            print(f"모델 저장 중 오류 발생: {e}")
    else:
        print("모델 훈련에 실패했습니다.")
