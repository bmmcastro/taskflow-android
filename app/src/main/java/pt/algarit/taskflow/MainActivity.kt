package pt.algarit.taskflow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import pt.algarit.taskflow.databinding.ActivityMainBinding
import pt.algarit.taskflow.ui.feedback.FeedbackActivity

/* Activity unica: todos os ecra sao Fragments dentro do NavHost.
   Tambem recebe o resultado da FeedbackActivity (atividade pratica). */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /* Recebe o feedback enviado pela FeedbackActivity */
    private val feedbackLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val mensagem = resultado.data?.getStringExtra(FeedbackActivity.EXTRA_MENSAGEM) ?: ""
            Toast.makeText(this, "Feedback recebido: $mensagem", Toast.LENGTH_LONG).show()
            Log.d("CICLO_PRINCIPAL", "Recebi o feedback: $mensagem")
        } else {
            Log.d("CICLO_PRINCIPAL", "O feedback foi cancelado")
        }
    }

    fun abrirFeedback() {
        feedbackLauncher.launch(Intent(this, FeedbackActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.d("CICLO_PRINCIPAL", "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar ligada ao NavController: titulo por destino + seta de voltar
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHost.navController
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.loginFragment).build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onStart() { super.onStart(); Log.d("CICLO_PRINCIPAL", "onStart") }
    override fun onResume() { super.onResume(); Log.d("CICLO_PRINCIPAL", "onResume") }
    override fun onPause() { super.onPause(); Log.d("CICLO_PRINCIPAL", "onPause") }
    override fun onStop() { super.onStop(); Log.d("CICLO_PRINCIPAL", "onStop") }
    override fun onRestart() { super.onRestart(); Log.d("CICLO_PRINCIPAL", "onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d("CICLO_PRINCIPAL", "onDestroy") }
}
