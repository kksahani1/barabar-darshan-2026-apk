package com.example.dimascio.myapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv {
    directory = "/assets"
    filename = "env" // use filename env, instead of .env
} // <---- 1. Configure dotenv


val EXTRA_MESSAGE = "com.example.dimascio.myapp.MESSAGE"
class MainActivity : AppCompatActivity() {

    private val ev = dotenv["MY_EV"] // <---- 2. Get value from dotenv
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<EditText>(R.id.editText).apply {
            hint = "$ev" // <--- 3. Use dotenv value for hint (not a good idea :-D)
        }
    }

    fun sendMessage(view: View) {
        val editText = findViewById<EditText>(R.id.editText)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }
}
