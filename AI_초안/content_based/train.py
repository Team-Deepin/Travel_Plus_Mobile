import pandas as pd
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, TensorDataset
import sys
import os
sys.path.append(os.path.join('.', 'utils'))
from preprocessing import get_preprocessed_data
import numpy as np

class MLP(nn.Module):
    def __init__(self, input_dim, hidden_dim, output_dim):
        super(MLP, self).__init__()
        self.model = nn.Sequential(
            nn.Linear(input_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, hidden_dim),
            nn.ReLU(),
            nn.Linear(hidden_dim, output_dim)  # 출력층: 활성화 없음
        )

    def forward(self, x):
        return self.model(x)
    
def fixed_multi_hot_encode(df, column, valid_tags):
    """travel_type은 반드시 1~12까지 고정 멀티핫 인코딩"""
    for tag in valid_tags:
        df[f"{column}_{tag}"] = df[column].apply(
            lambda x: 1 if isinstance(x, str) and str(tag) in x.split(';') else 0
        )
    return df

def train():
    # 데이터 불러오기 및 전처리
    user_path = './data/사용자.csv'
    travel_path = './data/여행.csv'
    visit_path = './data/여행방문지.csv'
    poi_path = './data/여행지.csv'
    data = get_preprocessed_data(user_path, travel_path, visit_path, poi_path)

    # ✅ travel_type 1~12 고정 멀티핫 인코딩
    valid_travel_types = [str(i) for i in range(1, 13)]
    data = fixed_multi_hot_encode(data, 'travel_type', valid_travel_types)

    # 사용 features 정의
    features = ['gender', 'age', 'season', 'means_tp', 'person', 'is_outdoor'] + \
               [f'travel_type_{tag}' for tag in valid_travel_types]

    # is_outdoor NaN만 따로 1로 처리
    data['is_outdoor'] = data['is_outdoor'].fillna(1)
    # 나머지 NaN은 전부 -999로 채움
    data[features] = data[features].fillna(-999)

    ###############################
    # 학습 데이터 생성
    ###############################
    X = data[features].values.astype('float32')
    y = data['satisfaction'].astype('float32').values

    # ✅ 확인용 출력 코드 추가
    print(f"✅ X.shape: {X.shape}")  # (샘플 수, 특성 수)
    print(f"✅ input_dim (특성 수): {X.shape[1]}")
    
    print("\n✅ [디버그] X의 첫 5개 샘플:")
    print(X[:5])  # 첫 5개 샘플 출력

    print("\n✅ [디버그] 사용된 feature 목록:")
    print(features)

    # Tensor로 변환
    X_tensor = torch.tensor(X, dtype=torch.float32)
    y_tensor = torch.tensor(y, dtype=torch.float32).view(-1, 1)
    
    dataset = TensorDataset(X_tensor, y_tensor)
    dataloader = DataLoader(dataset, batch_size=64, shuffle=True)

    ###############################
    # 모델 정의 및 학습
    ###############################
    model = MLP(input_dim=X.shape[1], hidden_dim=128, output_dim=1)
    criterion = nn.MSELoss()
    optimizer = optim.Adam(model.parameters(), lr=0.001)

    for epoch in range(20):
        model.train()
        total_loss = 0
        for xb, yb in dataloader:
            optimizer.zero_grad()
            preds = model(xb)
            loss = criterion(preds, yb)
            loss.backward()
            optimizer.step()
            total_loss += loss.item()
        print(f"[{epoch+1}/20] Loss: {total_loss/len(dataloader):.4f}")

    torch.save(model.state_dict(), "mlp_model.pth")
    print("✅ 모델 학습 완료 및 저장됨: mlp_model.pth")

if __name__ == "__main__":
    train()
