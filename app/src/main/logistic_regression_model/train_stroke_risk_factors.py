import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
import joblib

print("Loading dataset...")
# Make sure the filename perfectly matches the CSV you put in the folder
data = pd.read_csv('healthcare-dataset-stroke-data.csv')


data = data.drop(columns=['id'])


data['bmi'] = data['bmi'].fillna(data['bmi'].mean())


X = data.drop(columns=['stroke'])
y = data['stroke']


X_encoded = pd.get_dummies(X)

joblib.dump(list(X_encoded.columns), 'model_columns.pkl')


X_train, X_test, y_train, y_test = train_test_split(X_encoded, y, test_size=0.2, random_state=42)

print("Feeding data to the Logistic Regression algorithm (this might take a few seconds)...")
model = LogisticRegression(max_iter=1000)
model.fit(X_train, y_train)


accuracy = model.score(X_test, y_test)
print(f"Training Complete! Model Accuracy: {accuracy * 100:.2f}%")


joblib.dump(model, 'logistic_regression_stroke_risk.pkl')
print("Model saved as 'logistic_regression_model.pkl'")