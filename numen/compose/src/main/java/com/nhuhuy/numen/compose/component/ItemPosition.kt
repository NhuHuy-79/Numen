package com.nhuhuy.numen.compose.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
sealed interface ItemPosition {
    data object Single : ItemPosition
    enum class Vertical : ItemPosition {
        TOP, MIDDLE, BOTTOM
    }

    enum class Horizontal : ItemPosition {
        START, MIDDLE, END
    }
}


fun Int.verticalPosition(listSize: Int): ItemPosition {
    require(listSize > 0) { "listSize must be greater than 0" }
    if (listSize == 1) return ItemPosition.Single
    return when (this) {
        0 -> ItemPosition.Vertical.TOP
        listSize - 1 -> ItemPosition.Vertical.BOTTOM
        else -> ItemPosition.Vertical.MIDDLE
    }
}

fun Int.horizontalPosition(listSize: Int): ItemPosition {
    require(listSize > 0) { "listSize must be greater than 0" }
    if (listSize == 1) return ItemPosition.Single
    return when (this) {
        0 -> ItemPosition.Horizontal.START
        listSize - 1 -> ItemPosition.Horizontal.END
        else -> ItemPosition.Horizontal.MIDDLE
    }
}

fun ItemPosition.toShape(small: Dp = 8.dp, large: Dp = 24.dp): RoundedCornerShape {
    return when (this) {
        ItemPosition.Single -> RoundedCornerShape(large)
        is ItemPosition.Vertical -> toShape(small, large)
        is ItemPosition.Horizontal -> toShape(small, large)
    }
}


fun ItemPosition.Vertical.toShape(small: Dp = 8.dp, large: Dp = 24.dp): RoundedCornerShape {
    return when (this) {
        ItemPosition.Vertical.TOP -> getVerticalRoundedCornerShape(top = large, bottom = small)
        ItemPosition.Vertical.MIDDLE -> RoundedCornerShape(small)
        ItemPosition.Vertical.BOTTOM -> getVerticalRoundedCornerShape(top = small, bottom = large)
    }
}

fun ItemPosition.Horizontal.toShape(small: Dp = 8.dp, large: Dp = 24.dp): RoundedCornerShape {
    return when (this) {
        ItemPosition.Horizontal.START -> getHorizontalRoundedCornerShape(start = large, end = small)
        ItemPosition.Horizontal.MIDDLE -> RoundedCornerShape(small)
        ItemPosition.Horizontal.END -> getHorizontalRoundedCornerShape(start = small, end = large)
    }
}

private fun getVerticalRoundedCornerShape(top: Dp, bottom: Dp): RoundedCornerShape {
    return RoundedCornerShape(
        topEnd = top,
        topStart = top,
        bottomEnd = bottom,
        bottomStart = bottom
    )
}

private fun getHorizontalRoundedCornerShape(start: Dp, end: Dp): RoundedCornerShape {
    return RoundedCornerShape(
        topEnd = end,
        topStart = start,
        bottomEnd = end,
        bottomStart = start
    )
}