package pt.algarit.taskflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/* Activity unica: todos os ecra sao Fragments dentro do NavHost. */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
