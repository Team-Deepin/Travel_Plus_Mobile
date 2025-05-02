import pandas as pd

# utils/formatter.py
def format_prediction_output(predictions):
    output = []
    for i, course in enumerate(predictions, 1):
        area_name = course['area_name']
        output.append(f"코스{i}({area_name}):")
        for date, places in course['schedule'].items():
            formatted_date = pd.to_datetime(date).strftime('%Y-%m-%d')
            day_line = f"{formatted_date}: " + " - ".join(places)
            output.append(day_line)
    return "\n".join(output)

# predictions 예시 포맷:
# predictions = [
#     {
#         'area_name': '서울 강남구',
#         'schedule': {
#             '2025-05-01': ['관광지1', '식당1', '관광지2', '관광지3', '카페1'],
#             '2025-05-02': ['관광지4', '식당2', '관광지5', '관광지6', '카페2']
#         }
#     },
#     ... 총 3개 코스
# ]
