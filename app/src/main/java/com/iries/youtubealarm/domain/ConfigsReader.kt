package com.iries.youtubealarm.domain

import com.google.gson.Gson
import com.iries.youtubealarm.data.UserConfigs
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException

object ConfigsReader {
    private const val CONFIGS_PATH
            : String = "data/data/com.iries.youtubealarm/files/app_data/settings.json"

    fun load(): UserConfigs {
        val configs: UserConfigs
        var json = ""
        try {
            val f = createFile()
            val `is` = FileInputStream(f)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val gson = Gson()

        configs = if (json.isEmpty()) UserConfigs()
        else gson.fromJson(json, UserConfigs::class.java)
        return configs
    }

    fun save(configs: UserConfigs?) {
        try {
            createFile()
            val fileWriter = FileWriter(CONFIGS_PATH)
            fileWriter.write(Gson().toJson(configs))
            fileWriter.flush()
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        val file = File(CONFIGS_PATH)
        file.parentFile?.mkdirs()
        file.createNewFile()
        return file
    }
}