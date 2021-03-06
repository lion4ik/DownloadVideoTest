package com.lion4ik.github

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lion4ik.github.main.MainFragment

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment())
                    .commitNow()
        }
    }
}
