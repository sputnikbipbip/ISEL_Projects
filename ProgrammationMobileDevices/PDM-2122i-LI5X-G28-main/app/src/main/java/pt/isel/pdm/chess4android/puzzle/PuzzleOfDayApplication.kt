package pt.isel.pdm.chess4android.puzzle

import android.app.Application
import android.util.Log
import androidx.room.Room
import androidx.work.*
import pt.isel.pdm.chess4android.DailyPuzzleService
import pt.isel.pdm.chess4android.history.HistoryDatabase
import pt.isel.pdm.chess4android.history.WorkManagerAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val URL = "https://lichess.org/api/"

class PuzzleOfDayApplication: Application() {

    init {
        Log.v("Chess4Android", "Chess4Android.init for ${hashCode()}")
    }

    val dailyPuzzleService: DailyPuzzleService by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DailyPuzzleService::class.java)
    }

    /**
     * The database that contains the "games of day" fetched so far. ALL of them =)
     */
    val historyDB: HistoryDatabase by lazy {
        Room
            .databaseBuilder(this, HistoryDatabase::class.java, "history_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        val workRequest = PeriodicWorkRequestBuilder<WorkManagerAPI>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "DownloadDailyPuzzle",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}