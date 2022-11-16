package pt.isel.pdm.chess4android.history

import android.util.Log
import pt.isel.pdm.chess4android.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Extension function of [PuzzleEntity] to conveniently convert it to a [PuzzleInfo] instance.
 * Only relevant for this activity.
 */

/**
 * TODO: implement game is finished (its hardcoded)
 */
fun PuzzleEntity.toPuzzleInfo() = PuzzleInfo(
    game = Game(
        id = this.game.gameId,
        pgn = this.game.pgn,
    ),
    puzzle = Puzzle(
        id = this.puzzle.puzzleId,
        initialPly = this.puzzle.initialPly,
        solution = this.puzzle.solution,
    )
)

fun PuzzleInfo.toPuzzleEntity() = PuzzleEntity(
    game = Game(
        gameId = this.game.id,
        pgn = this.game.pgn
    ),
    puzzle = Puzzle(
        puzzleId = this.puzzle.id,
        initialPly = this.puzzle.initialPly,
        solution = this.puzzle.solution,
    ),
    isFinished = false
)



class GameOfDayRepository(
    private val gameOfDayService: DailyPuzzleService,
    private val historyGameDao: HistoryGameDao
) {

    /**
     * Asynchronously gets the daily puzzle from the local DB, if available.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncMaybeGetTodayPuzzleFromDB(callback: (Result<PuzzleEntity?>) -> Unit) {
        callbackAfterAsync(callback) {
            historyGameDao.getLast(1).firstOrNull()
        }
    }

    /**
     * Asynchronously gets the daily quote from the remote API.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */

    /**
     * TODO: where is verified if the puzzle is from today (method: isTodayGame from class: historyDataAccess)
     * */
    private fun asyncGetTodayPuzzleFromAPI(callback: (Result<PuzzleInfo>) -> Unit) {
        gameOfDayService.getPuzzle().enqueue(
            object: Callback<PuzzleInfo> {
                override fun onResponse(call: Call<PuzzleInfo>, response: Response<PuzzleInfo>) {
                    Log.v("APP_TAG", "Thread ${Thread.currentThread().name}: onResponse ")
                    val dailyPuzzle: PuzzleInfo? = response.body()
                    val result =
                        if (dailyPuzzle != null && response.isSuccessful)
                            Result.success(dailyPuzzle)
                        else
                            Result.failure(ServiceUnavailable())
                    callback(result)
                }

                override fun onFailure(call: Call<PuzzleInfo>, error: Throwable) {
                    Log.v("APP_TAG", "Thread ${Thread.currentThread().name}: onFailure ")
                    callback(Result.failure(ServiceUnavailable(cause = error)))
                }
            })
    }

    /**
     * Asynchronously saves the daily puzzle to the local DB.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncSaveToDB(puzzle: PuzzleInfo, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            historyGameDao.insert(
                puzzle.toPuzzleEntity()
            )
        }
    }

    /**
     * Asynchronously gets the puzzle of day, either from the local DB, if available, or from
     * the remote API.
     *
     * @param mustSaveToDB  indicates if the operation is only considered successful if all its
     * steps, including saving to the local DB, succeed. If false, the operation is considered
     * successful regardless of the success of saving the quote in the local DB (the last step).
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD
     *
     * Using a boolean to distinguish between both options is a questionable design decision.
     */
    fun fetchGameOfDay(mustSaveToDB: Boolean = true, callback: (Result<PuzzleInfo>) -> Unit) {
        asyncMaybeGetTodayPuzzleFromDB { maybeEntity ->
            val maybeGame = maybeEntity.getOrNull()
            if (maybeGame != null && maybeGame.isTodayGame()) {
                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Got daily puzzle from local DB")
                callback(Result.success(maybeGame.toPuzzleInfo()))
            }
            else {
                asyncGetTodayPuzzleFromAPI { apiResult ->
                    apiResult.onSuccess { todayPuzzle ->
                        Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Got daily puzzle from API")
                        asyncSaveToDB(todayPuzzle) { saveToDBResult ->
                            saveToDBResult.onSuccess {
                                Log.v(APP_TAG, "Thread ${Thread.currentThread().name}: Saved daily puzzle to local DB")
                                callback(Result.success(todayPuzzle))
                            }
                                .onFailure {
                                    Log.e(APP_TAG, "Thread ${Thread.currentThread().name}: Failed to save daily puzzle to local DB", it)
                                    callback(if(mustSaveToDB) Result.failure(it) else Result.success(todayPuzzle))
                                }
                        }
                    }
                    callback(apiResult)
                }
            }
        }
    }
}