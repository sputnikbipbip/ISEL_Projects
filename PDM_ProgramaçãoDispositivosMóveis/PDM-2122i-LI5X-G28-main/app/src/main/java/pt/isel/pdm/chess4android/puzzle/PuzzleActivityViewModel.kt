package pt.isel.pdm.chess4android.puzzle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.history.callbackAfterAsync
import pt.isel.pdm.chess4android.Army
import pt.isel.pdm.chess4android.ChessPiece
import pt.isel.pdm.chess4android.Piece

private const val PUZZLE_ACTIVITY_VIEW_STATE = "PuzzleActivity.ViewState"
private const val PUZZLE_SOLUTION_STATE = "PuzzleSolution.ViewState"
private const val PUZZLE_CLICKED_STATE = "PuzzleClick.ViewState"

class PuzzleActivityViewModel (application: Application,
                               private val state: SavedStateHandle,
                               ) : AndroidViewModel(application) {

    val pieces: LiveData<MutableList<ChessPiece>> = state.getLiveData(PUZZLE_ACTIVITY_VIEW_STATE)
    val currentPiece: LiveData<ChessPiece?> = state.getLiveData(PUZZLE_CLICKED_STATE)
    val index: LiveData<Int> = state.getLiveData(PUZZLE_SOLUTION_STATE)

    var playerOrientation: Army = Army.WHITE

    enum class Solution {
        WRONG_PREV_COL, WRONG_PREV_ROW, WRONG_CURR_COL, WRONG_CURR_ROW, CORRECT
    }

    fun gameCompleted(puzzleId : String) {
        val historyGameDao = getApplication<PuzzleOfDayApplication>().historyDB.getHistoryPuzzleDao()
        callbackAfterAsync(callback = {}) {
            historyGameDao.finishGame(puzzleId)
        }
    }

    fun createBoard(player: Int){
        var playerArmy = Army.WHITE
        var AIArmy = Army.BLACK
        if (player % 2 == 0) {
            playerArmy = Army.BLACK
            AIArmy = Army.WHITE
        }

        playerOrientation = playerArmy

        val pieces: MutableList<ChessPiece> = mutableListOf()

        for (i in 0..1){
            pieces.add(ChessPiece(0 + i * 7, 0, AIArmy, Piece.ROOK))
            pieces.add(ChessPiece(0 + i * 7, 7, playerArmy, Piece.ROOK))

            pieces.add(ChessPiece(1 + i * 5, 0, AIArmy, Piece.KNIGHT))
            pieces.add(ChessPiece(1 + i * 5, 7, playerArmy, Piece.KNIGHT))

            pieces.add(ChessPiece(2 + i * 3, 0, AIArmy, Piece.BISHOP))
            pieces.add(ChessPiece(2 + i * 3, 7, playerArmy, Piece.BISHOP))
        }

        for (i in 0..7) {
            pieces.add(ChessPiece(i, 1, AIArmy, Piece.PAWN))
            pieces.add(ChessPiece(i, 6, playerArmy, Piece.PAWN))
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
        pieces.add(ChessPiece(colQueen, 0, AIArmy, Piece.QUEEN))
        pieces.add(ChessPiece(colQueen, 7, playerArmy, Piece.QUEEN))
        pieces.add(ChessPiece(colKing, 0, AIArmy, Piece.KING))
        pieces.add(ChessPiece(colKing, 7, playerArmy, Piece.KING))
        state.set(PUZZLE_ACTIVITY_VIEW_STATE, pieces)
    }

    fun move(curr: Pair<Int, Int>, prev: Pair<Int, Int>?, solution: List<String>, index: Int): Solution{
        if (prev!!.first != get(solution[index][0])){
            return Solution.WRONG_PREV_COL
        }
        if (prev.second != get(solution[index][1])){
            return Solution.WRONG_PREV_ROW
        }
        if (curr.first != get(solution[index][2])){
            return Solution.WRONG_CURR_COL
        }
        if (curr.second != get(solution[index][3])){
            return Solution.WRONG_CURR_ROW
        }
        val lastSelection: ChessPiece = pieces.value!!.find { it.col == prev.first && it.row == prev.second }!!
        /**En passant move */
        val deltaCol = kotlin.math.abs(prev.first - curr.first)
        val deltaRow = kotlin.math.abs(prev.second - curr.second)
        val bool = eatPieceAt(curr.first, curr.second)
        movePiece(listOf(lastSelection), curr.first, curr.second)
        if (!bool && lastSelection.piece == Piece.PAWN
            && deltaCol == 1 && deltaRow == 1) {
            eatPieceAt(curr.first, prev.second)
        }
        state.set(PUZZLE_SOLUTION_STATE, index + 1)
        return Solution.CORRECT
    }

    private fun moveBoard(strNotation: String, playerTurn: Army){
        val kingSide = true
        val queenSide = false
        val piece: Piece = when {
            strNotation[0] == 'N' -> Piece.KNIGHT
            strNotation[0] == 'B' -> Piece.BISHOP
            strNotation[0] == 'R' -> Piece.ROOK
            strNotation[0] == 'Q' -> Piece.QUEEN
            strNotation[0] == 'K' -> Piece.KING
            else -> Piece.PAWN
        }
        when(strNotation.length){
            2 -> {
                val col = get(strNotation[0])
                val row = get(strNotation[1])
                movePawn(col, col, row, playerTurn)
            }
            3  ->{
                val col = get(strNotation[1])
                val row = get(strNotation[2])
                //move knight
                if (strNotation.contains('+'))
                    movePawn(get(strNotation[0]), get(strNotation[0]), get(strNotation[1]), playerTurn)
                //special move 'castling'
                if (strNotation[0] == 'O')
                    moveCastling(kingSide, playerTurn)
                else {
                    movePiece(findPieces(ChessPiece(-1, -1, playerTurn, piece)), col, row)
                }
            }
            4 -> {
                val col = get(strNotation[2])
                val row = get(strNotation[3])
                if (strNotation.contains('x')){
                    if (piece == Piece.PAWN) {
                        movePawn(get(strNotation[0]), col, row, playerTurn)
                    }
                    else {
                        eatPieceAt(col, row)
                        movePiece(findPieces(ChessPiece(-1, -1, playerTurn, piece)), col, row)
                    }
                }
                else if (strNotation.contains('+'))
                    moveBoard(strNotation.substring(0,3), playerTurn)
                else if (strNotation.contains('='))
                    moveBoard(strNotation.substring(0,2), playerTurn)
                else {
                    eatPieceAt(col, row)
                    val c = strNotation[1]
                    when {
                        (c in 'a'..'h') -> movePiece(findPieces(ChessPiece(get(c),-1, playerTurn, piece )), col, row)
                        else -> movePiece(findPieces(ChessPiece(-1,get(c), playerTurn, piece )), col, row)
                    }
                }
            }
            5 -> {
                val col = get(strNotation[3])
                val row = get(strNotation[4])
                when {
                    strNotation.contains('+') -> {
                        moveBoard(strNotation.substring(0, 4), playerTurn)
                    }
                    strNotation.contains('x') -> {
                        eatPieceAt(col, row)
                        val c = strNotation[1]
                        when {
                            (c in 'a'..'h') -> movePiece(findPieces(ChessPiece(get(c),-1, playerTurn, piece )), col, row)
                            else -> movePiece(findPieces(ChessPiece(-1,get(c), playerTurn, piece )), col, row)
                        }
                    }
                    strNotation[0] == 'O' -> {
                        moveCastling(queenSide, playerTurn)
                    }
                    //if more than one piece of that type can move to that position, then move
                    else -> {
                        movePiece(findPieces(ChessPiece(get(strNotation[1]), get(strNotation[2]), playerTurn, piece)), col, row)
                    }
                }
            }
            6 -> {
                val col = get(strNotation[3])
                val row = get(strNotation[4])
                if (strNotation.contains('x')) {
                    eatPieceAt(col, row)
                    movePiece(findPieces(ChessPiece(get(strNotation[1]), get(strNotation[2]), playerTurn, piece)), col, row)
                }
                if (strNotation.contains('+'))
                    moveBoard(strNotation.substring(0,5),playerTurn)
            }
            7 ->{
                if (strNotation.contains('+')) {
                    moveBoard(strNotation.substring(0, 6), playerTurn)
                }
            }
            else -> {}
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

    fun createPuzzle(pgn: String ) {
        val pgnList: List<String> = pgn.split(" ")
        var playerTurn: Army
        var turn = 0
        pgnList.forEach {
            playerTurn = if (turn++ %2 == 0) Army.WHITE else Army.BLACK
            moveBoard(it, playerTurn)
        }
    }

    private fun eatPieceAt(col: Int, row: Int) : Boolean {
        val l: MutableList<ChessPiece>? = pieces.value?.toMutableList()
        val res: Boolean = l?.removeIf{it.col ==col && it.row == row}!!
        state.set(PUZZLE_ACTIVITY_VIEW_STATE, l)
        return res
    }

    private fun moveCastling(kingSide: Boolean, player: Army) {
        val lastSelection: MutableList<ChessPiece>? = this.pieces.value
        val king: ChessPiece = findPieces(ChessPiece(-1,-1,player, Piece.KING))[0]
        val fromCol: Int
        val toColKing: Int
        val toColRook: Int
        if (playerOrientation == Army.WHITE) {
            if(kingSide) {
                fromCol = 7
                toColKing = 6
                toColRook = 5
            }
            else {
                fromCol = 0
                toColKing = 2
                toColRook = 3
            }
        }
        else {
            if (kingSide) {
                fromCol = 0
                toColKing = 1
                toColRook = 2
            }
            else{
                fromCol = 7
                toColKing = 5
                toColRook = 4
            }
        }
        val rook: ChessPiece = findPieces(ChessPiece(fromCol, -1, player, Piece.ROOK))[0]
        movePiece(listOf(ChessPiece(rook.col, rook.row, player, rook.piece)), toColRook, rook.row)
        lastSelection!!.remove(ChessPiece(king.col, king.row,player, king.piece))
        lastSelection.add(ChessPiece( toColKing, king.row, player, king.piece))
    }

    private fun pieceAt(col: Int, row: Int) : ChessPiece?{
        val lastSelection: MutableList<ChessPiece>? = pieces.value
        return lastSelection?.find { it.col == col && it.row == row }
    }

    private fun movePawn(fromCol: Int, toCol: Int, toRow: Int, player: Army) {
        val lastSelection: MutableList<ChessPiece>? = pieces.value
        val pawnList : List<ChessPiece> = findPieces(ChessPiece(fromCol, -1, player, Piece.PAWN))
        var piece: ChessPiece
        for (i in pawnList.indices) {
            piece = pawnList[i]
            if(canPawnMove(piece, toCol, toRow)){
                lastSelection?.remove(pieceAt(toCol, toRow))
                lastSelection?.remove(piece)
                if (toRow == 0 || toRow == 7)
                    lastSelection?.add(ChessPiece(toCol, toRow, piece.player, Piece.QUEEN))
                else
                    lastSelection?.add(ChessPiece(toCol, toRow, piece.player, piece.piece))
                break
            }

        }
        state.set(PUZZLE_ACTIVITY_VIEW_STATE, lastSelection)
    }

    private fun movePiece(piecesOfType: List<ChessPiece>, toCol: Int, toRow: Int){
        val lastSelection: MutableList<ChessPiece>? = this.pieces.value
        var piece: ChessPiece
        for (i in piecesOfType.indices) {
            piece = piecesOfType[i]
            when(piece.piece){
                Piece.ROOK -> if(canTowerMove(piece, toCol, toRow)){
                    lastSelection!!.remove(piece)
                    lastSelection.add(ChessPiece( toCol, toRow, piece.player, piece.piece))
                    break
                }
                Piece.BISHOP -> if(canBishopMove(piece,  toCol, toRow)){
                    lastSelection!!.remove(piece)
                    lastSelection.add(ChessPiece( toCol, toRow, piece.player, piece.piece))
                    break
                }
                Piece.KNIGHT -> if(canKnightMove( piece,  toCol, toRow)){
                    lastSelection!!.remove(piece)
                    lastSelection.add(ChessPiece( toCol, toRow, piece.player, piece.piece))
                    break
                }
                Piece.KING -> if (canKingMove(piece,  toCol, toRow)){
                    lastSelection!!.remove(piece)
                    lastSelection.add(ChessPiece( toCol, toRow, piece.player, piece.piece))
                    break
                }
                Piece.QUEEN -> if (canQueenMove(piece,  toCol, toRow)){
                    lastSelection!!.remove(piece)
                    lastSelection.add(ChessPiece( toCol, toRow, piece.player, piece.piece))
                    break
                }
                Piece.PAWN -> if (canPawnMove(piece,  toCol, toRow)){
                    lastSelection!!.remove(piece)
                    lastSelection.add(ChessPiece( toCol, toRow, piece.player, piece.piece))
                    break
                }
            }
        }
        state.set(PUZZLE_ACTIVITY_VIEW_STATE, lastSelection)
    }

    private fun canKingMove(piece: ChessPiece, col: Int, row: Int): Boolean {
        if (canQueenMove(piece, col, row)) {
            val deltaCol = kotlin.math.abs(piece.col - col)
            val deltaRow = kotlin.math.abs(piece.row - row)
            return deltaCol == 1 || deltaRow == 1 || deltaCol + deltaRow == 2
        }
        return false
    }

    private fun canKnightMove(piece: ChessPiece, col: Int, row: Int): Boolean {
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
        if (kotlin.math.abs(piece.col - to.first) != kotlin.math.abs(piece.row - to.second)) return false
        val gap = kotlin.math.abs(piece.col - to.first) - 1
        for (i in 1..gap) {
            val nextCol = if (to.first > piece.col) piece.col + i else piece.col - i
            val nextRow = if (to.second > piece.row) piece.row + i else piece.row - i
            if (pieceAt(nextCol, nextRow) != null) {
                return false
            }
        }
        return true
    }

    private fun canPawnMove(piece: ChessPiece, toCol: Int, toRow: Int): Boolean {
        val deltaCol = kotlin.math.abs(piece.col - toCol)
        val deltaRow = kotlin.math.abs(piece.row - toRow)
        val pieceToEat = pieceAt(toCol, toRow)
        val enpassant = pieceAt(toCol, piece.row)
        if (piece.row == 1 && piece.col == toCol) {
            if (toRow == 2 || toRow == 3)
                return true
        }
        if (piece.row == 6 && piece.col == toCol){
            if (toRow == 5 || toRow == 4)
                return true
        }
        if (playerOrientation == Army.WHITE) {
            if (piece.row - 1 == toRow && piece.player == Army.WHITE && piece.col == toCol)
                return true
            else if (piece.row + 1 == toRow && piece.player == Army.BLACK && piece.col == toCol)
                return true
        }
        if (playerOrientation == Army.BLACK) {
            if (piece.row + 1 == toRow && piece.player == Army.WHITE && piece.col == toCol)
                return true
            else if (piece.row - 1 == toRow && piece.player == Army.BLACK && piece.col == toCol)
                return true
        }
        if (deltaCol + deltaRow == 2 && pieceToEat != null && pieceToEat.player != piece.player)
                return true
        else if (deltaCol + deltaRow == 2 && enpassant != null && enpassant.player != piece.player
            && enpassant.piece == Piece.PAWN)
                return true
        return false
    }

    private fun canTowerMove(piece: ChessPiece, toCol: Int, toRow: Int): Boolean {
        if (piece.col == toCol && isClearVerticallyBetween(Pair(piece.col, piece.row), Pair(toCol, toRow)) ||
            piece.row == toRow && isClearHorizontallyBetween(Pair(piece.col, piece.row), Pair(toCol, toRow))) {
            return true
        }
        return false
    }

    private fun isClearHorizontallyBetween(from: Pair<Int, Int>, to: Pair<Int,Int>): Boolean {
        if (from.second != to.second) return false
        val gap = kotlin.math.abs(from.first - to.first) - 1
        if (gap == 0 ) return true
        for (i in 1..gap) {
            val nextCol  = if (to.first > from.first) from.first + i else from.first - i
            if (pieceAt(nextCol , from.second) != null) {
                return false
            }
        }
        return true    }

    private fun isClearVerticallyBetween(from: Pair<Int, Int>, to: Pair<Int,Int>): Boolean {
        if (from.first != to.first) return false
        val gap = kotlin.math.abs(from.second - to.second) - 1
        if (gap == 0 ) return true
        for (i in 1..gap) {
            val nextRow = if (to.second > from.second) from.second + i else from.second - i
            if (pieceAt(from.first, nextRow) != null) {
                return false
            }
        }
        return true
    }

    private fun canQueenMove(piece: ChessPiece, col:Int, row: Int): Boolean {
        return canTowerMove(piece, col,row) || canBishopMove(piece, col, row)
    }

    fun get(char: Char): Int{
        return when(char){
            'a', '8' ->  if (playerOrientation == Army.WHITE)  0 else 7
            'b', '7' ->  if (playerOrientation == Army.WHITE)  1 else 6
            'c', '6' ->  if (playerOrientation == Army.WHITE)  2 else 5
            'd', '5' ->  if (playerOrientation == Army.WHITE)  3 else 4
            'e', '4' ->  if (playerOrientation == Army.WHITE)  4 else 3
            'f', '3' ->  if (playerOrientation == Army.WHITE)  5 else 2
            'g', '2' ->  if (playerOrientation == Army.WHITE)  6 else 1
            else ->  if (playerOrientation == Army.WHITE)  7 else 0
        }
    }

    fun setCurrPiece(piece: ChessPiece?){
        state.set(PUZZLE_CLICKED_STATE, piece)
    }
}