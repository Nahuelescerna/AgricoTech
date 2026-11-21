package cl.duoc.agricotech.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente HTTP único de la app.
 *
 * `ignoreUnknownKeys` permite que Xano agregue columnas sin romper el parseo,
 * y `explicitNulls = false` evita enviar campos nulos al hacer POST.
 */
object RetrofitCliente {

    // URL del API group `agricotech` (rama Canary de Xano)
    private const val URL_BASE = "https://x8ki-letl-twmt.n7.xano.io/api:lgIQKxKC/"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val clienteHttp = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val api: AgricoTechApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AgricoTechApi::class.java)
    }
}
