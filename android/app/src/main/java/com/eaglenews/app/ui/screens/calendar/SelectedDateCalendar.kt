package com.eaglenews.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eaglenews.app.ui.theme.DarkNavy
import java.util.Calendar
import java.util.Date


@Composable
fun SelectedDateCalendar(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = remember {
        Calendar.getInstance()
    }
    val currentMonth = remember {
        calendar.get(Calendar.MONTH)
    }
    val currentYear = remember {
        calendar.get(Calendar.YEAR)
    }
    val daysInMonth = remember {
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    val firstDay = remember {
        val temp = Calendar.getInstance()
        temp.set(
            currentYear,
            currentMonth,
            1
        )
        temp.get(Calendar.DAY_OF_WEEK) - 1
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text = "${calendar.getDisplayName(
                Calendar.MONTH,
                Calendar.LONG,
                java.util.Locale.getDefault()
            )} $currentYear",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            listOf(
                "S",
                "M",
                "T",
                "W",
                "T",
                "F",
                "S"
            ).forEach {

                Text(
                    text = it,
                    color = Color.Gray
                )
            }
        }


        Spacer(
            Modifier.height(10.dp)
        )


        val totalBoxes = firstDay + daysInMonth


        Column {

            for(row in 0 until ((totalBoxes + 6) / 7)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {


                    for(column in 0..6) {

                        val index = row * 7 + column


                        if(index < firstDay ||
                            index >= totalBoxes
                        ) {

                            Spacer(
                                Modifier.size(40.dp)
                            )

                        } else {


                            val day = index - firstDay + 1


                            val date = Calendar.getInstance().apply {

                                set(
                                    currentYear,
                                    currentMonth,
                                    day
                                )

                                set(
                                    Calendar.HOUR_OF_DAY,
                                    0
                                )

                                set(
                                    Calendar.MINUTE,
                                    0
                                )

                                set(
                                    Calendar.SECOND,
                                    0
                                )

                            }.time


                            val isSelected =
                                isSameDay(
                                    date,
                                    selectedDate
                                )


                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if(isSelected)
                                            DarkNavy
                                        else
                                            Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable {

                                        onDateSelected(date)

                                    },
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = day.toString(),
                                    color =
                                        if(isSelected)
                                            Color.White
                                        else
                                            Color.Black
                                )
                            }
                        }
                    }
                }


                Spacer(
                    Modifier.height(8.dp)
                )
            }
        }
    }
}



private fun isSameDay(
    first: Date,
    second: Date
): Boolean {

    val firstCal = Calendar.getInstance()
    firstCal.time = first

    val secondCal = Calendar.getInstance()
    secondCal.time = second


    return firstCal.get(Calendar.YEAR) ==
            secondCal.get(Calendar.YEAR)
            &&
            firstCal.get(Calendar.DAY_OF_YEAR) ==
            secondCal.get(Calendar.DAY_OF_YEAR)
}