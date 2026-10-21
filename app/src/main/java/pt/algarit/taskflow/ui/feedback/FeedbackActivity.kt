package pt.algarit.taskflow.ui.feedback

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.algarit.taskflow.R
import pt.algarit.taskflow.databinding.ActivityFeedbackBinding

/* Atividade pratica (UC00609): Activity nova com ViewBinding e validacao
   que devolve o feedback a MainActivity como resultado. */
class FeedbackActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MENSAGEM = "mensagem_feedback"
    }

    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CICLO_FEEDBACK", "onCreate")
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnviarFeedback.setOnClickListener { enviar() }
        binding.btnCancelarFeedback.setOnClickListener {
            Log.d("CICLO_FEEDBACK", "Cancelado pelo utilizador")
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun enviar() {
        val nome = binding.edNomeFeedback.text?.toString()?.trim() ?: ""
        val mensagem = binding.edMensagemFeedback.text?.toString()?.trim() ?: ""

        // Validacao: os dois campos sao obrigatorios
        when {
            nome.isBlank() -> {
                binding.tiNomeFeedback.error = getString(R.string.erro_campos_vazios)
                return
            }
            mensagem.isBlank() -> {
                binding.tiMensagemFeedback.error = getString(R.string.erro_mensagem)
                return
            }
        }
        binding.tiNomeFeedback.error = null
        binding.tiMensagemFeedback.error = null

        Log.d("CICLO_FEEDBACK", "Feedback valido de $nome. Vou devolver a MainActivity.")
        val resultado = Intent().apply { putExtra(EXTRA_MENSAGEM, "$nome: $mensagem") }
        setResult(Activity.RESULT_OK, resultado)
        finish()
    }

    override fun onStart() { super.onStart(); Log.d("CICLO_FEEDBACK", "onStart") }
    override fun onResume() { super.onResume(); Log.d("CICLO_FEEDBACK", "onResume") }
    override fun onPause() { super.onPause(); Log.d("CICLO_FEEDBACK", "onPause") }
    override fun onStop() { super.onStop(); Log.d("CICLO_FEEDBACK", "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("CICLO_FEEDBACK", "onDestroy") }
}
