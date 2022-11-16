package pt.isel.pdm.chess4android.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.*

private const val GAME_ACTIVITY_VIEW_STATE = "GameActivity.ViewState"
private const val GAME_PLAYS_STATE = "GamePlays.ViewState"
private const val GAME_CLICKED_STATE = "GameClick.ViewState"
private const val GAME_LAST_PLAY = "GameLastPlay.ViewState"
private const val GAME_CHECK = "GAME_CHECK.ViewState"
private const val GAME_CHECK_MATE = "GAME_CHECK_MATE.ViewState"
private const val GAME_ATTACKING_PIECE = "GAME_ATTACKING_PIECE.ViewState"
private const val GAME_CASTLING = "GAME_CASTLING.ViewState"
private const val GAME_QUIT = "GAME_QUIT.ViewState"
private const val GAME_DRAW = "GAME_DRAW.ViewState"
private const val GAME_WIN = "GAME_WIN.ViewState"

class GameActivityViewModel (application: Application,
                             private val state: SavedStateHandle,
) : AndroidViewModel(application) {

    val pieces: LiveData<MutableList<ChessPiece>> = state.getLiveData(GAME_ACTIVITY_VIEW_STATE)
    val currentPiece: LiveData<ChessPiece?> = state.getLiveData(GAME_CLICKED_STATE)
    val nplays: LiveData<Int> = state.getLiveData(GAME_PLAYS_STATE)
    val lastPlay: LiveData<LastPlay> = state.getLiveData(GAME_LAST_PLAY)
    val check: LiveData<Check> = state.getLiveData(GAME_CHECK)
    val checkMate: LiveData<CheckMate> = state.getLiveData(GAME_CHECK_MATE)
    val attackingPiece: LiveData<AttackingPiece> = state.getLiveData(GAME_ATTACKING_PIECE)
    val castlingValidation: LiveData<ChessPiece> = state.getLiveData(GAME_CASTLING)
    val quit: LiveData<Boolean> = state.getLiveData(GAME_QUIT)
    val draw: LiveData<Boolean> = state.getLiveData(GAME_DRAW)
    val win: LiveData<Army> = state.getLiveData(GAME_WIN)

    fun draw () {
        state.set(GAME_DRAW, true)
    }

    fun quit () {
        state.set(GAME_QUIT, true)
    }

    fun createBoard(){
        state.set(GAME_CHECK, Check(Army.WHITE, false))
        state.set(GAME_CHECK_MATE, CheckMate(Army.WHITE, false))
        val playerOrientation = Army.WHITE

        val pieces: MutableList<ChessPiece> = mutableListOf()

        for (i in 0..1){
            pieces.add(ChessPiece(0 + i * 7, 0, Army.BLACK, Piece.ROOK))
            pieces.add(ChessPiece(0 + i * 7, 7, Army.WHITE, Piece.ROOK))

            pieces.add(ChessPiece(1 + i * 5, 0, Army.BLACK, Piece.KNIGHT))
            pieces.add(ChessPiece(1 + i * 5, 7, Army.WHITE, Piece.KNIGHT))

            pieces.add(ChessPiece(2 + i * 3, 0, Army.BLACK, Piece.BISHOP))
            pieces.add(ChessPiece(2 + i * 3, 7, Army.WHITE, Piece.BISHOP))
        }

        for (i in 0..7) {
            pieces.add(ChessPiece(i, 1, Army.BLACK, Piece.PAWN))
            pieces.add(ChessPiece(i, 6, Army.WHITE, Piece.PAWN))
        }

        val colKing: Int
        val colQueen: Int
        if(playerOrientation == Army.WHITE){
            colKing = 4
            colQueen = 3
        }
        else {
            colKing = 3
            colQueen = 4
        }
        pieces.add(ChessPiece(colQueen, 0, Army.BLACK, Piece.QUEEN))
        pieces.add(ChessPiece(colQueen, 7, Army.WHITE, Piece.QUEEN))
        pieces.add(ChessPiece(colKing, 0, Army.BLACK, Piece.KING))
        pieces.add(ChessPiece(colKing, 7, Army.WHITE, Piece.KING))
        state.set(GAME_ACTIVITY_VIEW_STATE, pieces)
    }

    fun move(curr: Pair<Int, Int>, prev: Pair<Int, Int>?, nplays: Int){
        val lastSelection: ChessPiece = pieces.value!!.find { it.col == prev!!.first && it.row == prev.second }!!
        var currPlayer = if (nplays % 2 == 0)
            Army.WHITE
        else
            Army.BLACK

        if (check.value?.isChecked!! && check.value?.army == currPlayer) {
            /* blocks piece ? eats piece making check ? its a king move ? If not, stop there! */
            val routeCollision = attackingPiece.value?.route!!.find { it.first == curr.first && it.second == curr.second}
            val chessPiece = attackingPiece.value?.chessPiece
            if (routeCollision == null
                && curr.first == chessPiece?.col
                && lastSelection.piece != Piece.KING
                && curr.second == chessPiece.row) {
                return
            }
        }
        if (movePiece(lastSelection, curr.first, curr.second, currPlayer)) {
            //invalidation of check status
            if (check.value!!.isChecked  && check.value?.army == currPlayer)
                state.set(GAME_CHECK, Check(Army.WHITE, false))
            state.set(GAME_PLAYS_STATE, nplays + 1)
            state.set(GAME_LAST_PLAY, LastPlay(lastSelection, curr.first, curr.second))
        }
    }

    private fun findPieces(piece: ChessPiece): List<ChessPiece>{
        val lastSelection: MutableList<ChessPiece>? = pieces.value
        if (piece.col > 0){
            return lastSelection!!.filter {
                it.col == piece.col && it.player == piece.player && it.piece == piece.piece
            }
        }
        else if (piece.row > 0) {
            return lastSelection!!.filter {
                it.row == piece.row && it.player == piece.player && it.piece == piece.piece
            }
        }
        else if (piece.col > 0 && piece.row > 0){
            return lastSelection!!.filter {
                it.row == piece.row && it.col == piece.col && it.player == piece.player && it.piece == piece.piece
            }
        }
        return lastSelection!!.filter {it.piece == piece.piece && it.player == piece.player}

    }

    private fun eatPieceAt(col: Int, row: Int, army: Army) : Boolean {
        val l: MutableList<ChessPiece>? = pieces.value?.toMutableList()
        val res: Boolean = l?.removeIf{it.col ==col && it.row == row && it.player != army}!!
        state.set(GAME_ACTIVITY_VIEW_STATE, l)
        return res
    }

    private fun pieceAt(col: Int, row: Int) : ChessPiece?{
        val lastSelection: MutableList<ChessPiece>? = pieces.value
        return lastSelection?.find { it.col == col && it.row == row}
    }

    private fun movePiece(piece: ChessPiece, toCol: Int, toRow: Int, currPlayer: Army): Boolean{
        var moved = canMove(piece, toCol, toRow)
        if (moved) {
            val res = eatPieceAt(toCol, toRow, currPlayer)
            val deltaCol = kotlin.math.abs(piece.col - toCol)
            val deltaRow = kotlin.math.abs(piece.row - toRow)
            /* enpassant move, piece to remove isn't in the position we want to go*/
            if (!res && piece.piece == Piece.PAWN &&
                    deltaCol == 1 && deltaRow == 1) {
                eatPieceAt(toCol, piece.row, currPlayer)
            }
            val lastSelection: MutableList<ChessPiece>? = this.pieces.value
            lastSelection!!.remove(piece)
            val newPiece = ChessPiece(toCol, toRow, piece.player, piece.piece)
            lastSelection.add(newPiece)
            //check all adversary pieces position (current player check)
            if (isCheck(lastSelection, currPlayer)) {
                return false
            }
            state.set(GAME_ACTIVITY_VIEW_STATE, lastSelection)
            val adversaryArmy = if (currPlayer == Army.BLACK) {
                Army.WHITE
            } else {
                Army.BLACK
            }
            val king = findPieces(ChessPiece(-1, -1, adversaryArmy, Piece.KING))[0]
            if (canMove(newPiece, king.col, king.row)) {
                state.set(GAME_CHECK, Check(adversaryArmy, true))
                if (checkMate(king, adversaryArmy)) {
                    state.set(GAME_CHECK_MATE, CheckMate(adversaryArmy, true))
                    state.set(GAME_WIN, currPlayer)
                }
            }
        }
        return moved
    }

    private fun checkMate(piece : ChessPiece, currPlayer: Army) : Boolean {
        val lastSelection: MutableList<ChessPiece> = pieces.value!!
        val maxValue = 7
        val minValue = 0
        var col : Int
        var row : Int
        for (i in -1 .. 1) {
            for (j in -1 .. 1) {
                col = piece.col + i
                row = piece.row + j
                if (col in minValue..maxValue) {
                    if (row in minValue..maxValue) {
                        if (canKingMove(piece, col, row)) {
                            lastSelection.remove(piece)
                            val newPiece = ChessPiece(col, row, piece.player, piece.piece)
                            lastSelection.add(newPiece)
                            if (!isCheck(lastSelection, currPlayer)) {
                                lastSelection.remove(newPiece)
                                lastSelection.add(piece)
                                return false
                            }
                            lastSelection.remove(newPiece)
                            lastSelection.add(piece)
                        }
                    }
                }
            }
        }

        var possibleEaters : List<ChessPiece> = pieces.value!!.filter { it.player == currPlayer}
        possibleEaters.forEach {
            if (canMove(it, attackingPiece.value!!.chessPiece.col, attackingPiece.value!!.chessPiece.row))
                return false
        }
        val route: MutableList<Pair<Int, Int>> = attackingPiece.value?.route!!
        possibleEaters.forEach { piece ->
            route.forEach { position ->
                if (canMove(piece, position.first, position.second)) {
                    lastSelection.remove(piece)
                    val newPiece = ChessPiece(position.first, position.second, piece.player, piece.piece)
                    lastSelection.add(newPiece)
                    if (!isCheck(lastSelection, currPlayer)) {
                        lastSelection.remove(newPiece)
                        lastSelection.add(piece)
                        return false
                    }
                    lastSelection.remove(newPiece)
                    lastSelection.add(piece)
                }
            }
        }
        return true
    }

    /**
     * check current player king, if its safe to make the move
     */
    private fun isCheck(pieces: MutableList<ChessPiece>, currPlayer: Army) : Boolean {
        val adversaryArmy = if (currPlayer == Army.BLACK) {
            Army.WHITE
        } else {
            Army.BLACK
        }
        val king = findPieces(ChessPiece(-1, -1, currPlayer, Piece.KING))[0]
        val adversaryPieces : List<ChessPiece> = pieces.filter { it.player == adversaryArmy}
        adversaryPieces.forEach{ it
            if (canMove(it, king.col, king.row))
                return true
        }
        return false
    }

    private fun canMove(piece : ChessPiece, col: Int, row: Int) : Boolean {
        return when(piece.piece){
            Piece.ROOK -> canTowerMove(piece, col, row)
            Piece.BISHOP -> canBishopMove(piece,  col, row)
            Piece.KNIGHT -> canKnightMove( piece,  col, row)
            Piece.KING -> canKingMove(piece,  col, row)
            Piece.QUEEN -> canQueenMove(piece,  col, row)
            Piece.PAWN -> canPawnMove(piece,  col, row)
        }
    }

    private fun canKingMove(piece: ChessPiece, col: Int, row: Int): Boolean {
        if (canQueenMove(piece, col, row)) {
            val deltaCol = kotlin.math.abs(piece.col - col)
            val deltaRow = kotlin.math.abs(piece.row - row)
            val isTherePiece = pieceAt(col, row)
            if (deltaCol == 0 && deltaRow == 0) return false
            if ((deltaCol == 1 && deltaRow == 1) || (piece.col + 1 == col || piece.col - 1 == col) || (piece.row + 1 == row || piece.row - 1 == row)) {
                if ((isTherePiece != null && isTherePiece.player != piece.player) || isTherePiece == null) {
                    return true
                }
            }
        }
        return false
    }

    private fun canKnightMove(piece: ChessPiece, col: Int, row: Int): Boolean {
        val pieceAt = pieceAt(col, row)
        if (pieceAt?.player == piece.player) {
            return false
        }
        return kotlin.math.abs(piece.col - col) == 2 && kotlin.math.abs(piece.row - row) == 1 ||
                kotlin.math.abs(piece.col - col) == 1 && kotlin.math.abs(piece.row - row) == 2
    }

    private fun canBishopMove(piece: ChessPiece, col: Int, row: Int): Boolean {
        if (kotlin.math.abs(piece.col - col) == kotlin.math.abs(piece.row - row)) {
            return isClearDiagonally(piece, Pair(col,row))
        }
        return false
    }

    private fun isClearDiagonally(piece: ChessPiece, to: Pair<Int, Int>): Boolean {
        var route : MutableList<Pair<Int, Int>> =  mutableListOf()
        if (kotlin.math.abs(piece.col - to.first) != kotlin.math.abs(piece.row - to.second)) return false
        val gap = kotlin.math.abs(piece.col - to.first)
        for (i in 1..gap) {
            val nextCol = if (to.first > piece.col) piece.col + i else piece.col - i
            val nextRow = if (to.second > piece.row) piece.row + i else piece.row - i
            val pieceAt = pieceAt(nextCol , nextRow)
            if ((pieceAt != null && nextCol != to.first && nextRow != to.second) ||
                (pieceAt != null && pieceAt.player == piece.player)) {
                return false
            }
            route.add(Pair(nextCol, nextRow))
        }
        if (!check.value!!.isChecked)
            state.set(GAME_ATTACKING_PIECE, AttackingPiece(piece, route))
        return true
    }


    private fun canPawnMove(piece: ChessPiece, toCol: Int, toRow: Int): Boolean {
        val deltaCol = kotlin.math.abs(piece.col - toCol)
        val deltaRow = kotlin.math.abs(piece.row - toRow)
        val pieceToEat = pieceAt(toCol, toRow)
        if (piece.row == 1 && piece.col == toCol) {
            if (toRow == 3 && pieceAt(toCol, 2) == null && pieceAt(toCol, 3) == null)
                return true
        }
        if (piece.row == 6 && piece.col == toCol){
            if (toRow == 4 && pieceAt(toCol, 4) == null && pieceAt(toCol, 5) == null)
                return true
        }
        if (piece.row - 1 == toRow && piece.player == Army.WHITE && piece.col == toCol && pieceToEat == null)
            return true
        else if (piece.row + 1 == toRow && piece.player == Army.BLACK && piece.col == toCol && pieceToEat == null)
            return true
        if ((deltaCol == 1 && deltaRow == 1) && pieceToEat != null && pieceToEat.player != piece.player)
            return true
        else if ((deltaCol == 1 && deltaRow == 1) &&
                lastPlay.value != null && kotlin.math.abs(lastPlay.value!!.toRow - lastPlay.value!!.lastPiece.row) == 2 &&
                lastPlay.value!!.toCol == toCol &&
                lastPlay.value!!.lastPiece.player != piece.player &&
                lastPlay.value!!.lastPiece.piece == Piece.PAWN) {
                return true
        }
        return false
    }

    private fun canTowerMove(piece: ChessPiece, toCol: Int, toRow: Int): Boolean {
        if (piece.col == toCol && isClearVerticallyBetween(piece, Pair(toCol, toRow)) ||
            piece.row == toRow && isClearHorizontallyBetween(piece, Pair(toCol, toRow))) {
            return true
        }
        return false
    }

    private fun isClearHorizontallyBetween(piece: ChessPiece, to: Pair<Int,Int>): Boolean {
        var route : MutableList<Pair<Int, Int>> =  mutableListOf()
        if (piece.row != to.second) return false
        val gap = kotlin.math.abs(piece.col- to.first)
        for (i in 1..gap) {
            val nextCol  = if (to.first > piece.col) piece.col + i else piece.col - i
            val pieceAt = pieceAt(nextCol , piece.row)
            if ((pieceAt != null && nextCol != to.first) ||
                (pieceAt != null && pieceAt.player == piece.player)) {
                return false
            }
            route.add(Pair(nextCol, piece.row))
        }
        if (!check.value!!.isChecked)
            state.set(GAME_ATTACKING_PIECE, AttackingPiece(piece, route))
        return true
    }

    private fun isClearVerticallyBetween(piece: ChessPiece, to: Pair<Int,Int>): Boolean {
        var route : MutableList<Pair<Int, Int>> =  mutableListOf()
        if (piece.col != to.first) return false
        val gap = kotlin.math.abs(piece.row - to.second)
        for (i in 1..gap) {
            val nextRow = if (to.second > piece.row) piece.row + i else piece.row - i
            val pieceAt = pieceAt(piece.col, nextRow)
            if ((pieceAt != null && nextRow != to.second) ||
                (pieceAt != null && pieceAt.player == piece.player)) {
                return false
            }
            route.add(Pair(piece.col, nextRow))
        }
        if (!check.value!!.isChecked)
            state.set(GAME_ATTACKING_PIECE, AttackingPiece(piece, route))
        return true
    }

    private fun canQueenMove(piece: ChessPiece, col:Int, row: Int): Boolean {
        return canTowerMove(piece, col,row) || canBishopMove(piece, col, row)
    }


    fun setCurrPiece(piece: ChessPiece?){
        state.set(GAME_CLICKED_STATE, piece)
    }
}