package com.alfanse.feedmycity.data.storage

interface ApplicationStorage {
    fun putBoolean(key: String, value: Boolean)
    fun putInteger(key: String, value: Int)
    fun putFloat(key: String, value: Float)
    fun putLong(key: String, value: Long)
    fun putString(key: String, value: String?)
    fun putObject(key: String, value: Any?)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun getInteger(key: String, defaultValue: Int): Int
    fun getFloat(key: String, defaultValue: Float): Float
    fun getString(key: String, defaultValue: String?): String?
    fun getLong(key: String, defaultValue: Long): Long
    fun getObject(key: String): Any?
    fun clearValue(key: String)
}
