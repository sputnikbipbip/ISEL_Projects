package pt.isel.pdm.chess4android.history

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import pt.isel.pdm.chess4android.APP_TAG
import pt.isel.pdm.chess4android.puzzle.PuzzleOfDayApplication

/**
 * Definition of the background job that fetches the daily game and stores it in the history DB.
 */
class WorkManagerAPI(appContext: Context, workerParams: WorkerParameters)
    : ListenableWorker(appContext, workerParams) {

    override fun startWork(): ListenableFuture<Result> {
        val app : PuzzleOfDayApplication = applicationContext as PuzzleOfDayApplication
        val repo = GameOfDayRepository(app.dailyPuzzleService, app.historyDB.getHistoryPuzzleDao())

        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Starting DownloadDailyPuzzleWorker")

        return CallbackToFutureAdapter.getFuture { completer ->
            repo.fetchGameOfDay (mustSaveToDB = true) { result ->
                result
                    .onSuccess {
                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: DownloadDailyPuzzleWorker succeeded")
                        completer.set(Result.success())
                    }
                    .onFailure {
                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: DownloadDailyPuzzleWorker failed")
                        completer.setException(it)
                    }
            }
        }
    }
}
