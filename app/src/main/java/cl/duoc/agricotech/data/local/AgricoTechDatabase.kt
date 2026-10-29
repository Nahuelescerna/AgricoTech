package cl.duoc.agricotech.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UsuarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AgricoTechDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        // Una sola instancia para toda la app: abrir la base es caro
            @Volatile
        private var instancia: AgricoTechDatabase? = null

        fun obtener(contexto: Context): AgricoTechDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    AgricoTechDatabase::class.java,
                    "agricotech.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { instancia = it }
            }
    }
}
