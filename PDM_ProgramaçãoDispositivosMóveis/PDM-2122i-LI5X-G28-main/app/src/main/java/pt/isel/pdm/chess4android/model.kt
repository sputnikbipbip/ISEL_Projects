package pt.isel.pdm.chess4android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Army {
    WHITE, BLACK
}

enum class Piece {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

@Parcelize
data class ChessPiece(val col: Int, val row: Int, val player: Army, val piece: Piece): Parcelable

@Parcelize
data class LastPlay(val lastPiece: ChessPiece, val toCol: Int, val toRow: Int): Parcelable

@Parcelize
data class Check(val army: Army, val isChecked: Boolean): Parcelable

@Parcelize
data class CheckMate(val army: Army, val isChecked: Boolean): Parcelable

@Parcelize
data class AttackingPiece(val chessPiece: ChessPiece, val route: MutableList<Pair<Int, Int>> ): Parcelable

