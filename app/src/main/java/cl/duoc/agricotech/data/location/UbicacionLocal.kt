package cl.duoc.agricotech.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Ubicación del teléfono con LocationManager, sin Google Play Services.
 *
 * Primero intenta la última ubicación conocida (responde al instante) y, si no
 * hay ninguna, pide una lectura nueva con un tiempo máximo para no dejar la
 * pantalla esperando. Sin permiso devuelve null y la app sigue con las
 * distancias que vienen de la API.
 */
object UbicacionLocal {

    private const val ESPERA_MAXIMA_MS = 12_000L

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    suspend fun actual(contexto: Context): Location? = withContext(Dispatchers.IO) {
        val gestor = contexto.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null

        val proveedores = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        val conocida = proveedores
            .mapNotNull { proveedor -> runCatching { gestor.getLastKnownLocation(proveedor) }.getOrNull() }
            .maxByOrNull { it.time }

        if (conocida != null) return@withContext conocida

        val proveedorActivo = proveedores.firstOrNull { gestor.isProviderEnabled(it) }
            ?: return@withContext null

        withTimeoutOrNull(ESPERA_MAXIMA_MS) {
            suspendCancellableCoroutine<Location?> { continuacion ->
                val escucha = object : LocationListener {
                    override fun onLocationChanged(ubicacion: Location) {
                        gestor.removeUpdates(this)
                        if (continuacion.isActive) continuacion.resume(ubicacion)
                    }

                    override fun onProviderEnabled(proveedor: String) = Unit

                    override fun onProviderDisabled(proveedor: String) = Unit

                    override fun onStatusChanged(proveedor: String?, estado: Int, extras: Bundle?) = Unit
                }

                val solicitado = runCatching {
                    gestor.requestLocationUpdates(
                        proveedorActivo,
                        0L,
                        0f,
                        escucha,
                        Looper.getMainLooper()
                    )
                }.isSuccess

                if (!solicitado && continuacion.isActive) continuacion.resume(null)

                continuacion.invokeOnCancellation { gestor.removeUpdates(escucha) }
            }
        }
    }
}
