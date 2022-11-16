package pt.isel.pdm.chess4android.history

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@Entity
data class Puzzle(
    @PrimaryKey val puzzleId: String,
    val initialPly: Int,
    @field:TypeConverters(StringTypeConverter::class)
    val solution: List<String>
)

@Entity
data class Game (
    @PrimaryKey val gameId: String,
    val pgn: String,
)

@Entity(tableName = "puzzle_details")
data class PuzzleEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    val game: Game,
    @Embedded
    val puzzle: Puzzle,
    @ColumnInfo(name = "finished")
    val isFinished: Boolean,
    val timestamp: Date = Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))
        ) {
    fun isTodayGame(): Boolean =
        timestamp.toInstant().compareTo(Instant.now().truncatedTo(ChronoUnit.DAYS)) == 0
}

/**
 * Contains converters used by the ROOM ORM to map between Kotlin types and MySQL types
 */
class StringTypeConverter {
    @TypeConverter
    fun saveString(list: List<String>) : String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun getString(string: String) : List<String> {
        return Gson().fromJson(
            string,
            object : TypeToken<List<String>>() {}.type
        )
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long) = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date) = date.time
}


@Dao
interface HistoryGameDao {
    @Insert
    fun insert(quote: PuzzleEntity)

    @Delete
    fun delete(quote: PuzzleEntity)

    @Query("SELECT * FROM puzzle_details ORDER BY id DESC LIMIT 100")
    fun getAll(): List<PuzzleEntity>

    @Query("SELECT * FROM puzzle_details ORDER BY id DESC LIMIT :count")
    fun getLast(count: Int): List<PuzzleEntity>

    @Query("UPDATE puzzle_details SET finished = 1 WHERE puzzleId = :id ")
    fun finishGame(id: String)

}

/**
 * The abstraction that represents the DB itself. It is also used as a DAO factory: one factory
 * method per DAO.
 */
@Database(entities = [PuzzleEntity::class], version = 4)
@TypeConverters(Converters::class)
abstract class HistoryDatabase: RoomDatabase() {
    abstract fun getHistoryPuzzleDao(): HistoryGameDao
}

