package pt.algarit.taskflow

import android.app.Application
import android.content.Context

/* Contexto de aplicacao, usado para ir buscar strings as mensagens de erro. */
class TaskFlowApp : Application() {

    companion object {
        lateinit var contexto: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        contexto = applicationContext
    }
}
