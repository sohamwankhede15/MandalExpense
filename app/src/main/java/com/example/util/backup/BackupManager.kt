package com.example.util.backup

import android.content.Context
import com.example.data.model.BackupData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object BackupManager {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(BackupData::class.java)

    fun toJson(backupData: BackupData): String {
        return adapter.toJson(backupData)
    }

    fun exportBackup(context: Context, backupData: BackupData): File {
        val fileName = "Mandal_Backup_${System.currentTimeMillis()}.json"
        val file = File(context.cacheDir, fileName)
        val json = adapter.toJson(backupData)
        FileWriter(file).use { it.write(json) }
        return file
    }

    fun saveBackupFile(context: Context, jsonString: String): File? {
        return try {
            val fileName = "Mandal_Backup_${System.currentTimeMillis()}.json"
            val file = File(context.cacheDir, fileName)
            FileWriter(file).use { it.write(jsonString) }
            file
        } catch (e: Exception) {
            null
        }
    }

    fun shareBackupFile(context: Context, file: File) {
        com.example.util.ShareHelper.shareFile(
            context = context,
            file = file,
            mimeType = "application/json",
            title = "मंडळ डेटा बॅकअप / Mandal Backup"
        )
    }

    fun parseBackup(file: File): BackupData? {
        return try {
            val json = FileReader(file).use { it.readText() }
            adapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun parseBackupJson(jsonString: String): BackupData? {
        return try {
            adapter.fromJson(jsonString)
        } catch (e: Exception) {
            null
        }
    }
}
