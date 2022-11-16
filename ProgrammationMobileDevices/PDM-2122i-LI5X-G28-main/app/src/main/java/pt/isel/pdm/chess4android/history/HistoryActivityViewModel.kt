package pt.isel.pdm.chess4android.history;


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.Game
import pt.isel.pdm.chess4android.Puzzle
import pt.isel.pdm.chess4android.puzzle.PuzzleOfDayApplication

/**
 * The actual execution host behind the puzzle history screen (i.e. the [HistoryActivity]).
 */
class HistoryActivityViewModel(application: Application): AndroidViewModel(application) {

        /**
         * Holds a [LiveData] with the list of puzzles, or null if it has not yet been requested by
         * the [HistoryActivity] through a call to [loadHistory]
         */
        var history: LiveData<List<PuzzleInfoDTO>>? = null
            private set

        private val historyDao : HistoryGameDao by lazy {
            getApplication<PuzzleOfDayApplication>().historyDB.getHistoryPuzzleDao()
        }

        /**
         * Gets the puzzles list (history) from the DB.
         */
        fun loadHistory(): LiveData<List<PuzzleInfoDTO>> {
        val publish = MutableLiveData<List<PuzzleInfoDTO>>()
        history = publish
        callbackAfterAsync(
            asyncAction = {
                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Getting history from local DB")
                historyDao.getAll().map { puzzleEntity ->
                    PuzzleInfoDTO(
                        isFinished = puzzleEntity.isFinished,
                        date = puzzleEntity.timestamp,
                        puzzle = PuzzleInfo(
                            game = Game(
                                id = puzzleEntity.game.gameId,
                                pgn = puzzleEntity.game.pgn,
                            ),
                            puzzle = Puzzle(
                                id = puzzleEntity.puzzle.puzzleId,
                                initialPly = puzzleEntity.puzzle.initialPly,
                                solution = puzzleEntity.puzzle.solution,
                            )
                        )
                    )
                }
            },
            callback = { result ->
                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: mapping results")
                result.onSuccess { publish.value = it }
                result.onFailure { publish.value = emptyList() }
            }
        )
            return publish
        }
}
