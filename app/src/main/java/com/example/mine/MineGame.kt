package com.example.mine

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.widget.*
import android.widget.TableRow.LayoutParams
import java.util.*


class MineGame : Activity(){
    private lateinit var txtMineCount: TextView
    private lateinit var txtTimer: TextView
    private lateinit var btnSmile: ImageButton
    private lateinit var mineField: TableLayout
    private lateinit var blocks: Array<Array<Block>>
    private val blockDimension = 100
    private val blockPadding = 3

//    Adjust as you need
    private val numberOfRowsInMineField = 5
    private val numberOfColumnsInMineField = 5
    private val totalNumberOfMines = 3

    private val timer = Handler()
    private var secondsPassed = 0

    private var isTimerStarted = false
    private var areMinesSet = false
    private var isGameOver = false
    private var minesToFind = 0





    fun finishGame(currentRow: Int, currentColumn: Int) {
        isGameOver = true
        stopTimer()
        isTimerStarted = false
        btnSmile.setBackgroundResource(R.drawable.sad)
        for (row in 1..numberOfRowsInMineField) {
            for (column in 1..numberOfColumnsInMineField) {
                blocks[row][column].setBlockAsDisabled(false)
                if (blocks[row][column].hasMine() && !blocks[row][column].isFlagged()) {
                    blocks[row][column].setMineIcon(false)
                }
                if (!blocks[row][column].hasMine() && blocks[row][column].isFlagged()) {
                    blocks[row][column].setFlagIcon(false)
                }
                if (blocks[row][column].isFlagged()) {
                    blocks[row][column].isClickable = false
                }

            }
        }

        blocks[currentRow][currentColumn].triggerMine()

        showDialog("Sorry you lost, You lasted for ${secondsPassed} seconds!", 1000, false, false)
    }

    fun winGame() {
        stopTimer()
        isTimerStarted = false
        isGameOver = true
        minesToFind = 0
        // set icon to cool dude
        btnSmile.setBackgroundResource(R.drawable.cool)

        updateMineCountDisplay()

        for (row in 1..numberOfRowsInMineField) {
            for (column in 1..numberOfColumnsInMineField) {
                blocks[row][column].isClickable = false
                if (blocks[row][column].hasMine()) {
                    blocks[row][column].setBlockAsDisabled(false)
                    blocks[row][column].setFlagIcon(false)
                }
            }
        }

        showDialog("You won in ${secondsPassed} seconds!", 1000, false, true)
    }




    fun checkGameWin(): Boolean {
        for (row in 1..numberOfRowsInMineField) {
            for (column in 1..numberOfColumnsInMineField) {

                if (!blocks[row][column].hasMine() && blocks[row][column].isCovered()) {
                    return false
                }
            }
        }
        return true
    }






    fun showDialog(
        message: String, milliseconds: Int, useSmileImage: Boolean, useCoolImage: Boolean
    ) {

        // show message
        val dialog = Toast.makeText(
            applicationContext, message, Toast.LENGTH_LONG
        )

        // get the dialog view if it's not null
        val dialogView = dialog.view as? LinearLayout

        // if the dialog view is null, just show the Toast as-is
        if (dialogView == null) {
            dialog.duration = milliseconds
            dialog.show()
            return
        }

        val coolImage = ImageView(applicationContext)
        if (useSmileImage) {
            coolImage.setImageResource(R.drawable.smiley)
        } else if (useCoolImage) {
            coolImage.setImageResource(R.drawable.cool)
        } else {
            coolImage.setImageResource(R.drawable.sad)
        }
        dialogView.addView(coolImage, 0)
        dialog.duration = milliseconds
        dialog.show()
    }

    private fun createMineField() {
        blocks = Array(numberOfRowsInMineField + 2) { row ->
            Array(numberOfColumnsInMineField + 2) { column ->
                val block = Block(this)
                block.setDefaults()
                val currentRow = row
                val currentColumn = column
                block.setOnClickListener {
                    if (!isTimerStarted) {
                        startTimer()
                        isTimerStarted = true
                    }
                    if (!areMinesSet) {
                        areMinesSet = true
                        setMines(currentRow, currentColumn)
                    }


                    if (!blocks[currentRow][currentColumn].isFlagged()) {
                        rippleUncover(currentRow, currentColumn)
                        if (blocks[currentRow][currentColumn].hasMine()) {
                            finishGame(currentRow, currentColumn)
                        }
                        if (checkGameWin()) {
                            winGame()
                        }
                    }
                }
                block.setOnLongClickListener {
                    if (!blocks[currentRow][currentColumn].isCovered() && blocks[currentRow][currentColumn].getNumberOfMinesInSorrounding() > 0 && !isGameOver) {
                        var nearbyFlaggedBlocks = 0
                        for (previousRow in -1..1) {
                            for (previousColumn in -1..1) {
                                if (previousRow == 0 && previousColumn == 0) {
                                    continue
                                }
                                if (currentRow + previousRow < 0 || currentRow + previousRow >= numberOfRowsInMineField || currentColumn + previousColumn < 0 || currentColumn + previousColumn >= numberOfColumnsInMineField) {
                                    continue
                                }
                                if (blocks[currentRow + previousRow][currentColumn + previousColumn].isFlagged()) {
                                    nearbyFlaggedBlocks++
                                }
                            }
                        }
                        if (nearbyFlaggedBlocks == blocks[currentRow][currentColumn].getNumberOfMinesInSorrounding()) {
                            for (previousRow in -1..1) {
                                for (previousColumn in -1..1) {
                                    if (previousRow == 0 && previousColumn == 0) {
                                        continue
                                    }
                                    if (currentRow + previousRow < 0 || currentRow + previousRow >= numberOfRowsInMineField || currentColumn + previousColumn < 0 || currentColumn + previousColumn >= numberOfColumnsInMineField) {
                                        continue
                                    }
                                    if (!blocks[currentRow + previousRow][currentColumn + previousColumn].isFlagged()) {
                                        rippleUncover(currentRow + previousRow, currentColumn + previousColumn)
                                        if (blocks[currentRow + previousRow][currentColumn + previousColumn].hasMine()) {
                                            finishGame(currentRow + previousRow, currentColumn + previousColumn)
                                        }
                                        if (checkGameWin()) {
                                            winGame()
                                        }
                                    }
                                }
                            }
                            return@setOnLongClickListener true
                        }
                    }
                    if (blocks[currentRow][currentColumn].isClickable() && (blocks[currentRow][currentColumn].isEnabled || blocks[currentRow][currentColumn].isFlagged)) {
                        if (!blocks[currentRow][currentColumn].isFlagged && !blocks[currentRow][currentColumn].isQuestionMarked) {
                            blocks[currentRow][currentColumn].setBlockAsDisabled(false)
                            blocks[currentRow][currentColumn].setFlagIcon(true)
                            blocks[currentRow][currentColumn].setFlagged(true)
                            minesToFind--
                            updateMineCountDisplay()
                        } else if (!blocks[currentRow][currentColumn].isQuestionMarked) {
                            blocks[currentRow][currentColumn].setBlockAsDisabled(true)
                            blocks[currentRow][currentColumn].setQuestionMarkIcon(true)
                            blocks[currentRow][currentColumn].setFlagged(false)
                            blocks[currentRow][currentColumn].setQuestionMarked(true)
                            minesToFind++
                            updateMineCountDisplay()
                        } else {
                            blocks[currentRow][currentColumn].setBlockAsDisabled(true)
                            blocks[currentRow][currentColumn].clearAllIcons()
                            blocks[currentRow][currentColumn].setQuestionMarked(false)
                            if (blocks[currentRow][currentColumn].isFlagged) {
                                minesToFind++
                                updateMineCountDisplay()
                            } else {
                                blocks[currentRow][currentColumn].setFlagged(false)
                            }
                        }

                    }
                    return@setOnLongClickListener true
                }
                block
            }
        }
    }











    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        txtMineCount = findViewById(R.id.MineCount)
        txtTimer = findViewById(R.id.Timer)

        btnSmile = findViewById(R.id.Smiley)
        btnSmile.setOnClickListener {
            endExistingGame()
            startNewGame()
        }

        mineField = findViewById(R.id.MineField)

        showDialog("Click smiley to start New Game", 3000, true, false)
    }

    fun startNewGame() {
        // plant mines and do rest of the calculations
        createMineField()
        // display all blocks in UI
        showMineField()

        minesToFind = totalNumberOfMines
        isGameOver = false
        secondsPassed = 0
    }
    fun updateMineCountDisplay() {
        when {
            minesToFind < 0 -> txtMineCount.text = minesToFind.toString()
            minesToFind < 10 -> txtMineCount.text = "00$minesToFind"
            minesToFind < 100 -> txtMineCount.text = "0$minesToFind"
            else -> txtMineCount.text = minesToFind.toString()
        }
    }


    private fun showMineField() {
        // remember we will not show 0th and last Row and Columns
        // they are used for calculation purposes only
        for (row in 1..numberOfRowsInMineField) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = LayoutParams(
                (blockDimension + 2 * blockPadding) * numberOfColumnsInMineField,
                blockDimension + 2 * blockPadding
            )

            for (column in 1..numberOfColumnsInMineField) {
                blocks[row][column].layoutParams = LayoutParams(
                    blockDimension + 2 * blockPadding, blockDimension + 2 * blockPadding
                )
                blocks[row][column].setPadding(
                    blockPadding, blockPadding, blockPadding, blockPadding
                )
                tableRow.addView(blocks[row][column])
            }
            mineField.addView(
                tableRow, TableLayout.LayoutParams(
                    (blockDimension + 2 * blockPadding) * numberOfColumnsInMineField,
                    blockDimension + 2 * blockPadding
                )
            )
        }
    }

    private fun endExistingGame() {
        stopTimer() // stop if timer is running
        txtTimer.text = "0:00" // revert all text
        txtMineCount.text = "000" // revert mines count
        btnSmile.setBackgroundResource(R.drawable.smiley)

        // remove all rows from mineField TableLayout
        mineField.removeAllViews()

        // set all variables to support end of game
        isTimerStarted = false
        areMinesSet = false
        isGameOver = false
        minesToFind = 0
    }



    fun setMines(currentRow: Int, currentColumn: Int) {
        val rand = Random()
        var mineRow: Int
        var mineColumn: Int

        for (row in 0 until totalNumberOfMines) {
            mineRow = rand.nextInt(numberOfColumnsInMineField)
            mineColumn = rand.nextInt(numberOfRowsInMineField)

            if (mineRow + 1 != currentColumn || mineColumn + 1 != currentRow) {
                // If a mine is already there, don't repeat for the same block
                if (blocks[mineColumn + 1][mineRow + 1].hasMine()) {
                    continue
                } else {
                    // Plant mine at this location
                    blocks[mineColumn + 1][mineRow + 1].plantMine()
                }
            } else {
                continue
            }

        }

        var nearByMineCount: Int

        // count number of mines in surrounding blocks
        for (row in 0 until numberOfRowsInMineField + 2) {
            for (column in 0 until numberOfColumnsInMineField + 2) {
                // for each block find nearby mine count
                nearByMineCount = 0
                if (row != 0 && row != numberOfRowsInMineField + 1 && column != 0 && column != numberOfColumnsInMineField + 1) {
                    // check in all nearby blocks
                    for (previousRow in -1..1) {
                        for (previousColumn in -1..1) {
                            if (blocks[row + previousRow][column + previousColumn].hasMine()) {
                                // a mine was found so increment the counter
                                nearByMineCount++
                            }
                        }
                    }

                    blocks[row][column].setNumberOfMinesInSurrounding(nearByMineCount)
                }
                // for side rows (0th and last row/column)
                // set count as 9 and mark it as opened
                else {
                    blocks[row][column].setNumberOfMinesInSurrounding(9)
                    blocks[row][column].openBlock()
                }
            }
        }
    }
    fun rippleUncover(rowClicked: Int, columnClicked: Int) {
        // don't open flagged or mined rows
        if (blocks[rowClicked][columnClicked].hasMine() || blocks[rowClicked][columnClicked].isFlagged()) {
            return
        }

        // open clicked block
        blocks[rowClicked][columnClicked].openBlock()

        // if clicked block have nearby mines then don't open further
        if (blocks[rowClicked][columnClicked].getNumberOfMinesInSorrounding() != 0) {
            return
        }

        // open next 3 rows and 3 columns recursively
        for (row in 0..2) {
            for (column in 0..2) {
                // check all the above checked conditions
                // if met then open subsequent blocks
                if (blocks[rowClicked + row - 1][columnClicked + column - 1].isCovered() && (rowClicked + row - 1 > 0) && (columnClicked + column - 1 > 0) && (rowClicked + row - 1 < numberOfRowsInMineField + 1) && (columnClicked + column - 1 < numberOfColumnsInMineField + 1)) {
                    rippleUncover(rowClicked + row - 1, columnClicked + column - 1)
                }
            }
        }
    }
    val updateTimeElapsed: Runnable = object : Runnable {
        override fun run() {
            val currentMilliseconds = System.currentTimeMillis()
            secondsPassed++
            when {
                secondsPassed < 10 -> txtTimer.text = "0:0$secondsPassed"
                secondsPassed < 100 -> txtTimer.text = "0$secondsPassed"
                else -> txtTimer.text = secondsPassed.toString()
            }
            // add notification
            timer.postAtTime(this, currentMilliseconds)
            // notify to call back after 1 second
            // basically to remain in the timer loop
            timer.postDelayed(this, 1000)
        }
    }
    fun startTimer() {
        if (secondsPassed == 0) {
            timer.removeCallbacks(updateTimeElapsed)
            // tell timer to run call back after 1 second
            timer.postDelayed(updateTimeElapsed, 1000)
        }
    }
    fun stopTimer() {
// disable call backs
        timer.removeCallbacks(updateTimeElapsed)
    }








}