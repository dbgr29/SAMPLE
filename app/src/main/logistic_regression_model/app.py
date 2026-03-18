from flask import Flask, request, jsonify
import pandas as pd
import joblib

app = Flask(__name__)


print("Starting server... Loading Logistic Regression model...")
lr_model = joblib.load('logistic_regression_model.pkl')
model_columns = joblib.load('model_columns.pkl')



@app.route('/predict_risk', methods=['POST'])
def predict_risk():
    try:
        # Get the JSON data sent from the Android app
        data = request.json
        print(f"Received patient data: {data}")

        # Convert the single JSON dictionary into a Pandas DataFrame
        df = pd.DataFrame([data])

        # Convert text answers (like "Male" or "Urban") into 1s and 0s
        df_encoded = pd.get_dummies(df)

        # Align the new live data with the columns the model was trained on!
        # This adds any missing columns and fills them with 0s.
        df_encoded = df_encoded.reindex(columns=model_columns, fill_value=0)

        # Predict probability (returns an array, we want the probability of class 1 / stroke)
        risk_probability = lr_model.predict_proba(df_encoded)[0][1]

        print(f"Calculated Risk Score: {risk_probability * 100:.2f}%")
        return jsonify({"success": True, "risk_score": float(risk_probability)})

    except Exception as e:
        print(f"Error: {str(e)}")
        return jsonify({"success": False, "error": str(e)})


# ==========================================
# 3. RUN THE SERVER
# ==========================================
if __name__ == '__main__':
    # host='0.0.0.0' allows your Android phone to connect over Wi-Fi
    app.run(host='0.0.0.0', port=5000, debug=True)