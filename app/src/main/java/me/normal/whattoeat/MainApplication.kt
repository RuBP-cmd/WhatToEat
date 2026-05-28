package me.normal.whattoeat

import android.app.Application

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 这里是整个 App 刚启动、任何 Activity 还没创建时的最早起点。
    }
}