package com.example.sample

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "StrokeApp.db"
        // Version changed to 2 to trigger a table refresh for the new INTEGER data types
        private const val DATABASE_VERSION = 2
        private const val TABLE_RISK_FACTORS = "RiskFactors"

        // Column Names
        private const val COL_ID = "id"
        private const val COL_HYPERTENSION = "hypertension"
        private const val COL_DIABETES = "diabetes"
        private const val COL_SMOKING = "smoking"
        private const val COL_DYSLIPIDEMIA = "dyslipidemia"
        private const val COL_OBESITY = "obesity"
        private const val COL_PRIOR_STROKE = "prior_stroke"
        private const val COL_ATRIAL_FIB = "atrial_fibrillation"
        private const val COL_SEDENTARY = "sedentary_behavior"
        private const val COL_ALCOHOL = "alcohol_consumption"
        private const val COL_CKD = "chronic_kidney_disease"
        private const val COL_SYNCED = "is_synced_to_cloud"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // All answer columns are now strictly set to INTEGER (1 or 0)
        val createTable = ("CREATE TABLE $TABLE_RISK_FACTORS ("
                + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COL_HYPERTENSION INTEGER,"
                + "$COL_DIABETES INTEGER,"
                + "$COL_SMOKING INTEGER,"
                + "$COL_DYSLIPIDEMIA INTEGER,"
                + "$COL_OBESITY INTEGER,"
                + "$COL_PRIOR_STROKE INTEGER,"
                + "$COL_ATRIAL_FIB INTEGER,"
                + "$COL_SEDENTARY INTEGER,"
                + "$COL_ALCOHOL INTEGER,"
                + "$COL_CKD INTEGER,"
                + "$COL_SYNCED INTEGER DEFAULT 0)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // If the database version changes, drop the old table and build the new one
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RISK_FACTORS")
        onCreate(db)
    }

    // Function to save the form locally (Now accepts a Map of Integers!)
    fun insertRiskFactors(answers: Map<String, Int>): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_HYPERTENSION, answers["hypertension"])
        contentValues.put(COL_DIABETES, answers["diabetes"])
        contentValues.put(COL_SMOKING, answers["smoking"])
        contentValues.put(COL_DYSLIPIDEMIA, answers["dyslipidemia"])
        contentValues.put(COL_OBESITY, answers["obesity"])
        contentValues.put(COL_PRIOR_STROKE, answers["prior_stroke"])
        contentValues.put(COL_ATRIAL_FIB, answers["atrial_fibrillation"])
        contentValues.put(COL_SEDENTARY, answers["sedentary"])
        contentValues.put(COL_ALCOHOL, answers["alcohol"])
        contentValues.put(COL_CKD, answers["ckd"])

        // Initially set to 0 (Not synced to the Python server yet)
        contentValues.put(COL_SYNCED, 0)

        val result = db.insert(TABLE_RISK_FACTORS, null, contentValues)
        db.close()
        return result != -1L
    }
}