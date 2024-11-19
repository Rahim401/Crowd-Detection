package com.crowd.client.application

import android.content.SharedPreferences
import com.crowd.client.utils.android.AppPreferences
import com.crowd.client.utils.android.getObject
import com.crowd.client.utils.android.put

data class User(val name: String, val email: String)
data class Query(val place: String, val time: Long)

interface BaseAppData: MainApplication.AppCompanion {
    var user: User?
    val lastQuery: Query?
    val isLoggedIn get() = user != null
}
object DefAppData: BaseAppData {
    override var user: User? = null
    override val lastQuery: Query? = null
}
object AppData: AppPreferences(), BaseAppData {
    override val prefName: String = "AppData"
    private val allPrefKeys = listOf("user", "lastQuery")
    override fun isPrefComplete(pref: SharedPreferences) =
        allPrefKeys.all { pref.contains(it) }
    override fun initializePref(pref: SharedPreferences) {
        pref.put("user", DefAppData.user)
        pref.put("lastQuery", DefAppData.lastQuery)
    }

    override var user: User?
        get() = pref.getObject("user")
        set(value) { pref.put("user", value) }
    override var lastQuery: Query?
        get() = pref.getObject("lastQuery")
        set(value) { pref.put("lastQuery", value) }
}