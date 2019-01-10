package me.jiahuan.gradle_plugin_sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.Main) {
            async(Dispatchers.IO) {
                hello()
            }.await()
            async(Dispatchers.IO) {
                hello2()
            }.await()
        }
    }

    private fun hello() {
        Thread.sleep(100)
    }

    private fun hello2() {
        Thread.sleep(1000)
    }
}
