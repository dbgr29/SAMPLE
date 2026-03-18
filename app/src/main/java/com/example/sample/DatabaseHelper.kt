package com.example.sample

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DeTechStroke.db"
        private const val DATABASE_VERSION = 1

        // --- 1. USER TABLE (Hybrid: ERD + Auth) ---
        private const val TABLE_USER = "User"
        private const val COL_USER_ID = "user_id"
        private const val COL_USER_NAME = "user_name"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
        private const val COL_IMAGE_URI = "image_uri"
        private const val COL_AGE = "age"
        private const val COL_SEX = "sex"

        // --- 2. HEALTH RISK FACTOR PROFILE TABLE ---
        private const val TABLE_HEALTH_PROFILE = "HealthRiskFactorProfile"
        private const val COL_PROFILE_ID = "profile_id"
        private const val COL_HYPERTENSION = "hypertension"
        private const val COL_DIABETES = "diabetes"
        private const val COL_SMOKER = "smoker"
        private const val COL_CHOLESTEROL = "cholesterol_level"
        private const val COL_STROKE_HISTORY = "stroke_history"
        private const val COL_CARDIAC_DISEASE = "cardiac_disease"
        private const val COL_OBESE = "obese"
        private const val COL_UNHEALTHY_DIET = "unhealthy_diet"
        private const val COL_PHYSICAL_INABILITY = "physical_inability"
        private const val COL_ALCOHOLIC = "alcoholic"
        private const val COL_BMI = "bmi"

        // --- 3. EMERGENCY CONTACTS TABLE ---
        private const val TABLE_EMERGENCY_CONTACTS = "EmergencyContacts"
        private const val COL_CONTACT_ID = "contact_id"
        private const val COL_PHONE_NUMBER = "phone_number"

        // --- 4. FACIAL SCAN SCHEDULE TABLE ---
        private const val TABLE_SCAN_SCHEDULE = "FacialScanSchedule"
        private const val COL_SCHED_ID = "sched_id"
        private const val COL_SCHEDULE = "schedule"
        private const val COL_COMPLETED = "completed"

        // --- 5. FACIAL SCAN RESULT TABLE ---
        private const val TABLE_SCAN_RESULT = "FacialScanResult"
        private const val COL_SCAN_ID = "scan_id"
        private const val COL_ASYMMETRIC_DETECTED = "asymmetric_detected"
        private const val COL_CONFIDENCE = "confidence"
        private const val COL_TIMESTAMP = "timestamp"

        // --- 6. RISK ASSESSMENT RESULT TABLE ---
        private const val TABLE_RISK_ASSESSMENT = "RiskAssessmentResult"
        private const val COL_RISK_ID = "risk_id"
        private const val COL_LR_PREDICTION = "lr_prediction"
        private const val COL_RISK_LEVEL = "risk_level"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Create User Table (Now includes email & password for local auth)
        val createUserTable = ("CREATE TABLE $TABLE_USER ("
                + "$COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_USER_NAME TEXT,"
                + "$COL_EMAIL TEXT UNIQUE,"
                + "$COL_PASSWORD TEXT,"
                + "$COL_IMAGE_URI TEXT,"
                + "$COL_AGE INTEGER," // Can be updated later in the profile
                + "$COL_SEX TEXT)")   // Can be updated later in the profile
        db.execSQL(createUserTable)

        val createHealthProfileTable = ("CREATE TABLE $TABLE_HEALTH_PROFILE ("
                + "$COL_PROFILE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_USER_ID INTEGER UNIQUE,"
                + "$COL_HYPERTENSION INTEGER,"
                + "$COL_DIABETES INTEGER,"
                + "$COL_SMOKER INTEGER,"
                + "$COL_CHOLESTEROL REAL,"
                + "$COL_STROKE_HISTORY INTEGER,"
                + "$COL_CARDIAC_DISEASE INTEGER,"
                + "$COL_OBESE INTEGER,"
                + "$COL_UNHEALTHY_DIET INTEGER,"
                + "$COL_PHYSICAL_INABILITY INTEGER,"
                + "$COL_ALCOHOLIC INTEGER,"
                + "$COL_BMI REAL,"
                + "FOREIGN KEY($COL_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID) ON DELETE CASCADE)")
        db.execSQL(createHealthProfileTable)

        val createEmergencyContactsTable = ("CREATE TABLE $TABLE_EMERGENCY_CONTACTS ("
                + "$COL_CONTACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_USER_ID INTEGER,"
                + "$COL_PHONE_NUMBER TEXT,"
                + "FOREIGN KEY($COL_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID) ON DELETE CASCADE)")
        db.execSQL(createEmergencyContactsTable)

        val createScanScheduleTable = ("CREATE TABLE $TABLE_SCAN_SCHEDULE ("
                + "$COL_SCHED_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_USER_ID INTEGER,"
                + "$COL_SCHEDULE TEXT,"
                + "$COL_COMPLETED INTEGER DEFAULT 0,"
                + "FOREIGN KEY($COL_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID) ON DELETE CASCADE)")
        db.execSQL(createScanScheduleTable)

        val createScanResultTable = ("CREATE TABLE $TABLE_SCAN_RESULT ("
                + "$COL_SCAN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_USER_ID INTEGER,"
                + "$COL_ASYMMETRIC_DETECTED INTEGER,"
                + "$COL_CONFIDENCE REAL,"
                + "$COL_TIMESTAMP TEXT,"
                + "FOREIGN KEY($COL_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID) ON DELETE CASCADE)")
        db.execSQL(createScanResultTable)

        val createRiskAssessmentTable = ("CREATE TABLE $TABLE_RISK_ASSESSMENT ("
                + "$COL_RISK_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_USER_ID INTEGER,"
                + "$COL_LR_PREDICTION REAL,"
                + "$COL_RISK_LEVEL TEXT,"
                + "$COL_TIMESTAMP TEXT,"
                + "FOREIGN KEY($COL_USER_ID) REFERENCES $TABLE_USER($COL_USER_ID) ON DELETE CASCADE)")
        db.execSQL(createRiskAssessmentTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RISK_ASSESSMENT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCAN_RESULT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCAN_SCHEDULE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMERGENCY_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HEALTH_PROFILE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    // ==========================================
    // AUTHENTICATION & UI HELPERS
    // ==========================================

    fun registerUser(email: String, password: String, name: String, imageUri: String): Boolean {
        val db = this.writableDatabase

        // Check if email already exists
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USER WHERE $COL_EMAIL = ?", arrayOf(email))
        if (cursor.count > 0) {
            cursor.close()
            return false // Email exists
        }
        cursor.close()

        val values = ContentValues().apply {
            put(COL_USER_NAME, name)
            put(COL_EMAIL, email)
            put(COL_PASSWORD, password)
            put(COL_IMAGE_URI, imageUri)
        }
        val result = db.insert(TABLE_USER, null, values)
        db.close()
        return result != -1L
    }

    // Returns the user_id if successful, or -1 if failed
    fun authenticateUser(email: String, password: String): Long {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COL_USER_ID FROM $TABLE_USER WHERE $COL_EMAIL = ? AND $COL_PASSWORD = ?", arrayOf(email, password))

        var userId = -1L
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(0)
        }
        cursor.close()
        return userId
    }

    fun getUserData(userId: Long): Map<String, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COL_USER_NAME, $COL_IMAGE_URI FROM $TABLE_USER WHERE $COL_USER_ID = ?", arrayOf(userId.toString()))

        var userData: Map<String, String>? = null
        if (cursor.moveToFirst()) {
            userData = mapOf(
                "name" to (cursor.getString(0) ?: "User"),
                "image_uri" to (cursor.getString(1) ?: "")
            )
        }
        cursor.close()
        return userData
    }

    fun getUserHealthSummary(userId: Long): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HEALTH_PROFILE WHERE $COL_USER_ID = ?", arrayOf(userId.toString()))

        val summary = if (cursor.moveToFirst()) {
            "Health Profile Configured. Tap to view."
        } else {
            "No health data available. Please complete your checkup."
        }
        cursor.close()
        return summary
    }
}