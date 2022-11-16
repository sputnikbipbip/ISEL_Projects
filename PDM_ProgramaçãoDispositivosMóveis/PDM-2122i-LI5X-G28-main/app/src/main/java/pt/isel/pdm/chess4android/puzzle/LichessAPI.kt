package pt.isel.pdm.chess4android

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.http.GET
import java.util.*


const val APP_TAG = "PuzzleOfTheDay"

@Parcelize
data class PuzzleInfoDTO(val isFinished: Boolean, val date: Date, val puzzle : PuzzleInfo) : Parcelable

@Parcelize
data class PuzzleInfo(val game: Game, val puzzle: Puzzle) : Parcelable

@Parcelize
data class Game(
    @SerializedName("id")
    val id: String,
    @SerializedName("pgn")
    val pgn: String,
    ):Parcelable

@Parcelize
data class Puzzle(
    @SerializedName("id")
    val id: String,
    @SerializedName("initialPly")
    val initialPly: Int,
    @SerializedName("solution")
    val solution: List<String>,
    ):Parcelable

interface DailyPuzzleService {
    @GET("puzzle/daily")
    fun getPuzzle(): Call<PuzzleInfo>
}

class ServiceUnavailable(message: String = "", cause: Throwable? = null) : Exception(message, cause)