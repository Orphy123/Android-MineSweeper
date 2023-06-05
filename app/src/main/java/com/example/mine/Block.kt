package com.example.mine
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.Button

class Block : androidx.appcompat.widget.AppCompatButton {

    private var isCovered = true // is block covered yet

    private var isMined = false // does the block have a mine underneath
    internal var isFlagged = false // is block flagged as a potential mine
    internal var isQuestionMarked = false // is block question marked
    private var isClickable = true // can block accept click events
    private var numberOfMinesInSurrounding = 0 // number of mines in nearby blocks

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    // set default properties for the block
    fun setDefaults() {
        isCovered = true
        isMined = false
        isFlagged = false
        isQuestionMarked = false
        isClickable = true
        numberOfMinesInSurrounding = 0

        setBackgroundResource(R.drawable.hex2)
        setBoldFont()
    }

    // mark the block as disabled/opened
    // update the number of nearby mines
    fun setNumberOfSurroundingMines(number: Int) {
        setBackgroundResource(R.drawable.square_grey)

        updateNumber(number)
    }

    // set mine icon for block
    // set block as disabled/opened if false is passed
    fun setMineIcon(enabled: Boolean) {
        text = "M"

        if (!enabled) {
            setBackgroundResource(R.drawable.square_grey)
            setTextColor(Color.RED)
        } else {
            setTextColor(Color.BLACK)
        }
    }

    // set mine as flagged
    // set block as disabled/opened if false is passed
    fun setFlagIcon(enabled: Boolean) {
        text = "F"



        if (!enabled) {
            setBackgroundResource(R.drawable.square_grey)
            setTextColor(Color.RED)
        } else {
            setTextColor(Color.BLACK)
        }
    }

    // set mine as question mark
    // set block as disabled/opened if false is passed
    fun setQuestionMarkIcon(enabled: Boolean) {
        text = "?"

        if (!enabled) {
            setBackgroundResource(R.drawable.square_grey)
            setTextColor(Color.RED)
        } else {
            setTextColor(Color.BLACK)
        }
    }

    // set block as disabled/opened if false is passed
    // else enable/close it
    fun setBlockAsDisabled(enabled: Boolean) {
        if (!enabled) {
            setBackgroundResource(R.drawable.square_grey)
        } else {
            setBackgroundResource(R.drawable.square_blue)
        }
    }

    // clear all icons/text
    fun clearAllIcons() {
        text = ""
    }

    // set font as bold
    private fun setBoldFont() {
        setTypeface(null, Typeface.BOLD)
    }

    // uncover this block
    fun openBlock() {
        // cannot uncover a mine which is not covered
        if (!isCovered) return

        setBlockAsDisabled(false)
        isCovered = false

        // check if it has mine
        if (hasMine()) {
            setMineIcon(false)
        }
        // update with the nearby mine count
        else {
            setNumberOfSurroundingMines(numberOfMinesInSurrounding)
        }
    }// set text as nearby mine count
    fun updateNumber(text: Int) {
        if (text != 0) {
            setText(text.toString())

            // select different color for each number
            // we have already skipped 0 mine count
            when (text) {
                1 -> setTextColor(Color.BLUE)
                2 -> setTextColor(Color.rgb(0, 100, 0))
                3 -> setTextColor(Color.RED)
                4 -> setTextColor(Color.rgb(85, 26, 139))
                5 -> setTextColor(Color.rgb(139, 28, 98))
                6 -> setTextColor(Color.rgb(238, 173, 14))
                7 -> setTextColor(Color.rgb(47, 79, 79))
                8 -> setTextColor(Color.rgb(71, 71, 71))
                9 -> setTextColor(Color.rgb(205, 205, 0))
            }
        }
    }

    // set block as a mine underneath
    fun plantMine() {
        isMined = true
    }


    // mine was opened
// change the block icon and color
    fun triggerMine() {
        setMineIcon(true)
        setTextColor(Color.RED)
    }

    // is block still covered
    fun isCovered(): Boolean {
        return isCovered
    }

    // does the block have any mine underneath
    fun hasMine(): Boolean {
        return isMined
    }

    // set number of nearby mines
    fun setNumberOfMinesInSurrounding(number: Int) {
        numberOfMinesInSurrounding = number
    }

    // get number of nearby mines
    fun getNumberOfMinesInSorrounding(): Int {
        return numberOfMinesInSurrounding
    }

    // is block marked as flagged
    fun isFlagged(): Boolean {
        return isFlagged
    }

    // mark block as flagged
    fun setFlagged(flagged: Boolean) {
        isFlagged = flagged
    }

    // is block marked as a question mark
    fun isQuestionMarked(): Boolean {
        return isQuestionMarked
    }

    // set question mark for the block
    fun setQuestionMarked(questionMarked: Boolean) {
        isQuestionMarked = questionMarked
    }

    // can block receive click event
    override fun isClickable(): Boolean {
        return isClickable
    }

    // disable block for receive click events
    override fun setClickable(clickable: Boolean) {
        isClickable = clickable
    }
}
