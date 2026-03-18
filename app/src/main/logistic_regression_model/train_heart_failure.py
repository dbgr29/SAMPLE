import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, f1_score, roc_auc_score, confusion_matrix, classification_report
import joblib


def train_logistic_regression():

    print("Loading dataset...")
    data = pd.read_csv('heart.csv')


    X = data.drop('HeartDisease', axis=1)
    y = data['HeartDisease']

    categorical_cols = ['Sex', 'ChestPainType', 'RestingECG', 'ExerciseAngina', 'ST_Slope']
    numerical_cols = ['Age', 'RestingBP', 'Cholesterol', 'FastingBS', 'MaxHR', 'Oldpeak']


    preprocessor = ColumnTransformer(
        transformers=[
            ('num', StandardScaler(), numerical_cols),
            ('cat', OneHotEncoder(drop='first'), categorical_cols)
        ])


    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)


    X_train_processed = preprocessor.fit_transform(X_train)
    X_test_processed = preprocessor.transform(X_test)


    print("Training Logistic Regression model...")
    model = LogisticRegression(max_iter=1000)


    model.fit(X_train_processed, y_train)


    print("\n--- Model Evaluation ---")


    y_pred = model.predict(X_test_processed)


    y_prob = model.predict_proba(X_test_processed)[:, 1]


    accuracy = accuracy_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred)
    auc = roc_auc_score(y_test, y_prob)

    print(f"Accuracy: {accuracy * 100:.2f}%")
    print(f"F1-Score: {f1:.4f}")
    print(f"AUC Score: {auc:.4f}\n")

    print("Detailed Classification Report:")
    print(classification_report(y_test, y_pred))

    print("Saving model to disk...")
    joblib.dump(model, 'logistic_regression_heart_condition.pkl')
    joblib.dump(preprocessor, 'data_preprocessor.pkl')
    print("Done! The model is ready to be loaded into the Python server.")

if __name__ == "__main__":
    train_logistic_regression()