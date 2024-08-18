package com.example.composition.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class Question(
    val sum: Int,
    val visibleNumber: Int,
    val option: List<Int>
){
    val rightAnswer:Int
        get() = sum - visibleNumber
}