package com.wwxd.utils

import com.google.gson.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger

/**
 * user：LuHao
 * time：2019/11/18 13:47
 * describe：json解析
 */
object GsonUtil {

    //添加序列化忽略策略
    @get:Synchronized
    private var gson: Gson? = null
        get() {
            if (field == null) {
                val gsonBuilder = GsonBuilder()
                //添加序列化忽略策略
                gsonBuilder.addSerializationExclusionStrategy(IgnoreStrategy())
                field = gsonBuilder.create()
            }
            return field
        }

    /**
     * 复制对象
     *
     * @param obj   内容
     * @param clazz 解析对象
     */
    fun <T> copy(obj: Any?, clazz: Class<T>?): T? {
        if (obj == null || clazz == null) return null
        val json = toJson(obj)
        return fromJson(json, clazz)
    }

    /**
     * 忽略策略,继承gson的排除策略
     */
    private class IgnoreStrategy : ExclusionStrategy {
        override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
            //注解
            val annotations = fieldAttributes.annotations
            if (annotations.size > 0) {
                for (annotation in annotations) {
                    //如果注解类型属于自定义的JsonIgnore
                    if (annotation.annotationClass == JsonIgnore::class) {
                        return true
                    }
                }
            }
            return false
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return false
        }
    }

    //对象转化成json
    fun toJson(obj: Any?): String {
        return gson!!.toJson(obj)
    }

    //json转化成对象
    fun <T> fromJson(json: String?, type: Type?): T? {
        try {
            return gson!!.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //json转化成对象
    fun <T> fromJson(json: String?, tClass: Class<T>?): T? {
        return try {
            gson!!.fromJson(json, tClass)
        } catch (e: Exception) {
            null
        }
    }

    //jsonObject转化成对象
    fun <T> fromJsonObject(jsonObject: JsonObject?, tClass: Class<T>?): T? {
        return try {
            val json = gson!!.toJson(jsonObject)
            gson!!.fromJson(json, tClass)
        } catch (e: Exception) {
            null
        }
    }

    fun getJsonObject(json: String, key: String): JsonObject? {
        val jsonObject = JsonParser.parseString(json).asJsonObject
        if (jsonObject != null && jsonObject.has(key) && jsonObject[key].isJsonObject)
            return jsonObject[key].asJsonObject
        return null
    }

    fun getJsonArray(json: String, key: String): JsonArray? {
        val jsonObject = JsonParser.parseString(json).asJsonObject
        if (jsonObject != null && jsonObject.has(key) && jsonObject[key].isJsonArray)
            return jsonObject[key].asJsonArray
        return null
    }
}