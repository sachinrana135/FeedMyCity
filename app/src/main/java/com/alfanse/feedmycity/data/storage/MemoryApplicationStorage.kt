package com.alfanse.feedmycity.data.storage

import android.util.Log

class MemoryApplicationStorage :
    ApplicationStorage {

    private var values = HashMap<String, Any?>()
    override fun putBoolean(key: String, value: Boolean) {
        values[key] = value
    }

    override fun putInteger(key: String, value: Int) {
        values[key] = value
    }

    override fun putFloat(key: String, value: Float) {
        values[key] = value
    }

    override fun putLong(key: String, value: Long) {
        values[key] = value
    }

    override fun putString(key: String, value: String?) {
        values[key] = value
    }

    override fun putObject(key: String, value: Any?) {
        Log.e(
            TAG,
            "Not possible to put object into preferences"
        )
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return getValue(key, defaultValue) as Boolean
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return getValue(key, defaultValue) as String?
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return getValue(key, defaultValue) as Long
    }

    override fun getInteger(key: String, defaultValue: Int): Int {
        return getValue(key, defaultValue) as Int
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return getValue(key, defaultValue) as Float
    }

    override fun getObject(key: String): Any? {
        Log.e(
            TAG,
            "Not possible to get object into preferences"
        )
        return null
    }

    override fun clearValue(key: String) {
        if (key != null && values.containsKey(key)) {
            values.remove(key)
        }
    }

    private operator fun getValue(key: String, defaultValue: Any?): Any? {
        var result = defaultValue
        if (values.containsKey(key) && values[key] != null) {
            result = values[key]
        }
        return result
    }

    companion object {
        private const val TAG = "MemoryAppStorage"
    }

}