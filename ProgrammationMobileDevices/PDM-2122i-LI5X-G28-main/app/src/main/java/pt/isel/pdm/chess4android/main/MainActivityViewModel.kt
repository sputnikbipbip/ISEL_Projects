package pt.isel.pdm.chess4android.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.APP_TAG
import pt.isel.pdm.chess4android.PuzzleInfo
import pt.isel.pdm.chess4android.history.GameOfDayRepository
import pt.isel.pdm.chess4android.puzzle.PuzzleOfDayApplication

private const val VIEW_STATE = "MainActivity.ViewState"

/**
 * The actual execution host behind the application's Main screen (i.e. the [MainActivity]).
 */
class MainActivityViewModel(
    application: Application,
    private val savedState: SavedStateHandle
) : AndroidViewModel(application) {

    init {
        Log.v("APP_TAG", "MainActivityViewModel.init()")
    }

    /**
     * The [LiveData] instance used to publish the result of [fetchPuzzleOfDay].
     */
    val puzzleOfDay: LiveData<PuzzleInfo> = savedState.getLiveData(VIEW_STATE)

    /**
     * The [LiveData] instance used to publish errors that occur while fetching the quote of day
     */
    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    /**
     * Asynchronous operation to fetch the puzzle of the day from the remote server. The operation
     * result (if successful) is published to the associated [LiveData] instance, [puzzleOfDay].
     */
    fun fetchPuzzleOfDay() {
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Fetching ...")
        val app = getApplication<PuzzleOfDayApplication>()
        val repo = GameOfDayRepository(app.dailyPuzzleService, app.historyDB.getHistoryPuzzleDao())
        repo.fetchGameOfDay { result ->
            result
                .onSuccess { savedState.set(VIEW_STATE, result.getOrThrow()) }
                .onFailure { _error.value = it }
        }
        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Returned from fetchPuzzleOfDay")
    }
}