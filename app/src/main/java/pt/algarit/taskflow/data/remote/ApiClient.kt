package pt.algarit.taskflow.data.remote

import okhttp3.OkHttpClient
import pt.algarit.taskflow.data.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/* Cliente unico da API: Retrofit + Gson, com timeouts de rede. */
object ApiClient {

    /* Producao. Para testar contra um servidor local do TaskFlow web,
       trocar por "http://10.0.2.2:3000/" (o emulador ve o PC por este IP). */
    private const val BASE_URL = "https://taskflow.algarit.pt/"

    val api: ApiService by lazy {
        val cliente = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
