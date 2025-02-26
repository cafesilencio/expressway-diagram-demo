package com.politefish.expresswaymapdemo.pointmapping

sealed class ExpBearing {

    override fun toString(): String {
        return javaClass.simpleName
    }

    open fun isInUpRange(bearing: Int): Boolean {
        val rangeA = IntRange(270, 360)
        val rangeB = IntRange(0, 90)
        return rangeA.contains(bearing) or rangeB.contains(bearing)
    }

    open fun isInDownRange(bearing: Int): Boolean {
        return IntRange(90, 270).contains(bearing)
    }

    open fun isInRightRange(bearing: Int): Boolean {
        return IntRange(0, 180).contains(bearing)
    }

    open fun isInLeftRange(bearing: Int): Boolean {
        return IntRange(180, 360).contains(bearing)
    }

    abstract fun getDirectionMask(): Int

    object Vertical : ExpBearing() {
        override fun getDirectionMask(): Int = up or down
    }

    object VerticalRotateCCW45 : ExpBearing() {
        override fun getDirectionMask(): Int = up or down

        override fun isInUpRange(bearing: Int): Boolean {
            val rangeA = IntRange(0, 45)
            val rangeB = IntRange(225, 360)
            return rangeA.contains(bearing) or rangeB.contains(bearing)
        }

        override fun isInDownRange(bearing: Int): Boolean {
            return IntRange(45, 225).contains(bearing)
        }
    }

    object VerticalRotateCW45 : ExpBearing() {
        override fun getDirectionMask(): Int = up or down

        override fun isInUpRange(bearing: Int): Boolean {
            val rangeA = IntRange(0, 135)
            val rangeB = IntRange(315, 360)
            return rangeA.contains(bearing) or rangeB.contains(bearing)
        }

        override fun isInDownRange(bearing: Int): Boolean {
            return IntRange(135, 315).contains(bearing)
        }
    }

    object Horizontal : ExpBearing() {
        override fun getDirectionMask(): Int = left or right
    }

    object HorizontalRotateCCW45 : ExpBearing() {
        override fun getDirectionMask(): Int = left or right

        override fun isInRightRange(bearing: Int): Boolean {
            val rangeA = IntRange(0, 45)
            val rangeB = IntRange(225, 360)
            return rangeA.contains(bearing) or rangeB.contains(bearing)
        }

        override fun isInLeftRange(bearing: Int): Boolean {
            return IntRange(45, 225).contains(bearing)
        }
    }

    object HorizontalRotateCCW90 : ExpBearing() {
        override fun getDirectionMask(): Int = left or right

        override fun isInRightRange(bearing: Int): Boolean {
            val rangeA = IntRange(0, 90)
            val rangeB = IntRange(270, 360)
            return rangeA.contains(bearing) or rangeB.contains(bearing)
        }

        override fun isInLeftRange(bearing: Int): Boolean {
            return IntRange(90, 270).contains(bearing)
        }
    }

    object UpRightDownLeft : ExpBearing() {
        override fun getDirectionMask(): Int = upRight or downLeft
    }

    object UpRightDownLeftRotateCW45 : ExpBearing() {
        override fun getDirectionMask(): Int = upRight or downLeft
        override fun isInUpRange(bearing: Int): Boolean {
            return IntRange(45, 225).contains(bearing)
        }

        override fun isInDownRange(bearing: Int): Boolean {
            val rangeA = IntRange(225, 360)
            val rangeB = IntRange(0, 45)
            return rangeA.contains(bearing) or rangeB.contains(bearing)
        }
    }

    object UpLeftDownRight : ExpBearing() {
        override fun getDirectionMask(): Int = upLeft or downRight
    }

    object UpLeftDownRightRotateCCW45 : ExpBearing() {
        override fun getDirectionMask(): Int = upLeft or downRight

        override fun isInUpRange(bearing: Int): Boolean {
            val rangeA = IntRange(0, 45)
            val rangeB = IntRange(225, 360)
            return rangeA.contains(bearing) or rangeB.contains(bearing)
        }

        override fun isInDownRange(bearing: Int): Boolean {
            return IntRange(45, 225).contains(bearing)
        }
    }

    object NoOp : ExpBearing() {
        override fun getDirectionMask(): Int = 0
    }

    fun translateBearing(bearing: Float): Float {
        val upRightDownLeft = upRight or downLeft
        val upLeftDownRight = upLeft or downRight
        val upDown = up or down
        val rightLeft = right or left

        val bearingAsInt = bearing.toInt()
        return when (getDirectionMask()) {
            upLeftDownRight -> {
                val isUp = isInUpRange(bearingAsInt)
                val isLeft = isInLeftRange(bearingAsInt)
                when {
                    isUp and isLeft -> 315f
                    !isUp and !isLeft -> 135f
                    !isUp and isLeft -> 135f
                    else -> 315f
                }
            }
            upRightDownLeft -> {
                val isUp = isInUpRange(bearingAsInt)
                val isRight = isInRightRange(bearingAsInt)

                when {
                    isUp and isRight -> 45f
                    !isUp and !isRight -> 225f
                    !isUp and isRight -> 225f
                    else -> 45f
                }
            }
            upDown -> {
                if (isInUpRange(bearingAsInt)) {
                    0f
                } else {
                    180f
                }
            }
            rightLeft -> {
                if (isInRightRange(bearingAsInt)) {
                    90f
                } else {
                    270f
                }
            }
            else -> bearing
        }
    }

    private companion object {
        private const val up = 1
        private const val down = 2
        private const val left = 4
        private const val right = 8
        private const val upRight = 16
        private const val downRight = 32
        private const val upLeft = 64
        private const val downLeft = 128
    }
}