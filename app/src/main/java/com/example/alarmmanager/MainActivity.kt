// FINAL
package com.example.alarmmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alarmmanager.ui.theme.AlarmmanagerTheme
import java.time.LocalTime
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material3.*

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("water_tracking", Context.MODE_PRIVATE)
        var glassesDrankCounter by mutableStateOf(sharedPreferences.getInt("glassesDrankCounter", 0))
        var glassesDrank by mutableStateOf(sharedPreferences.getInt("glassesDrank", 0))
        var goal by mutableStateOf(sharedPreferences.getString("goal", "") ?: "")
        var weight by mutableStateOf(sharedPreferences.getString("weight", "") ?: "")

        val scheduler = AndroidAlarmScheduler(this)
        var alarmItem: AlarmItem? = null
        var isDefaultSchedule by mutableStateOf(true)
        var isWaterTrackingVisible by mutableStateOf(false)

        var isReminderActive by mutableStateOf(sharedPreferences.getBoolean("isReminderActive", false))

        enableEdgeToEdge()
        setContent {
            AlarmmanagerTheme {
                var startTime by remember { mutableStateOf("") }
                var endTime by remember { mutableStateOf("") }
                var intervalMinutes by remember { mutableStateOf("") }
                var message by remember { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isDefaultSchedule) {
                        Text(
                            text = "HYDRATION REMINDER APP",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Text(
                        text = when {
                            isWaterTrackingVisible -> "TRACK WATER CONSUMPTION HERE"
                            isDefaultSchedule -> "SET DEFAULT NOTIFICATIONS HERE"
                            else -> "SET CUSTOM NOTIFICATIONS HERE"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )


                    if (!isWaterTrackingVisible) {
                        if (!isDefaultSchedule) {
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Start Time (HH:MM)") }
                            )
                            OutlinedTextField(
                                value = endTime,
                                onValueChange = { endTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("End Time (HH:MM)") }
                            )
                            OutlinedTextField(
                                value = intervalMinutes,
                                onValueChange = { intervalMinutes = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Reminder Interval (minutes)") }
                            )
                        }

                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Message") }
                        )

                        Button(onClick = {
                            isDefaultSchedule = !isDefaultSchedule
                        }) {
                            Text(text = if (isDefaultSchedule) "Use Customizable Schedule" else "Use Default Schedule")
                        }

                        Button(onClick = {
                            if (isDefaultSchedule) {
                                startTime = "08:00"
                                endTime = "20:00"
                                intervalMinutes = "120"
//                                startTime = "16:00" // To Debug
//                                endTime = "17:00"
//                                intervalMinutes = "10"
                            }
                            alarmItem = AlarmItem(
                                startTime = LocalTime.parse(startTime),
                                endTime = LocalTime.parse(endTime),
                                intervalMinutes = intervalMinutes.toInt(),
                                message = message
                            )
                            if (isDefaultSchedule) {
                                alarmItem?.let(scheduler::scheduler)
                            } else {
                                alarmItem?.let(scheduler::scheduler)
                            }

                            sharedPreferences.edit().putBoolean("isReminderActive", true).apply()
                            isReminderActive = true

                        }) {
                            Text(text = if (isDefaultSchedule) "Schedule Default" else "Schedule Custom")
                        }

                        Button(onClick = {
                            alarmItem?.let(scheduler::cancel)
                        }) {
                            Text(text = "Cancel")
                        }
                        Spacer(modifier = Modifier.height(150.dp))

                    }

                    Button(onClick = {
                        isWaterTrackingVisible = !isWaterTrackingVisible
                    }) {
                        Text(text = if (isWaterTrackingVisible) "← Back" else "Show Water Tracking")
                    }
                    Spacer(modifier = Modifier.height(80.dp))


                    if (isWaterTrackingVisible) {
                        WaterIntakeSection(
                            weight = weight,
                            glassesDrank = glassesDrank,
                            glassesDrankCounter = glassesDrankCounter,
                            goal = goal,
                            onWeightChange = { updatedWeight ->
                                weight = updatedWeight
                                sharedPreferences.edit().putString("weight", updatedWeight).apply()
                            },
                            onGlassesDrankChange = { updatedGlassesDrank ->
                                glassesDrank = updatedGlassesDrank
                                sharedPreferences.edit().putInt("glassesDrank", updatedGlassesDrank).apply()
                            },
                            onGlassesDrankCounterChange = { updatedCount ->
                                glassesDrankCounter = updatedCount
                                sharedPreferences.edit().putInt("glassesDrankCounter", updatedCount).apply()
                            },
                            onGoalChange = { updatedGoal ->
                                goal = updatedGoal
                                sharedPreferences.edit().putString("goal", updatedGoal).apply()
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WaterIntakeSection(
    weight: String,
    glassesDrank: Int,
    glassesDrankCounter: Int,
    goal: String,
    onWeightChange: (String) -> Unit,
    onGlassesDrankChange: (Int) -> Unit,
    onGlassesDrankCounterChange: (Int) -> Unit,
    onGoalChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = weight,
            onValueChange = { newWeight ->
                onWeightChange(newWeight)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Weight (in kilograms)") }
        )

        Button(onClick = {
            val weightInKg = weight.toFloatOrNull()
            if (weightInKg != null) {
                val recommendedIntake = (weightInKg * 0.03 * 4).toInt()
                onGlassesDrankChange(recommendedIntake)
            }
        }) {
            Text("Calculate the number of glasses you should drink")
        }

        Text("Number of glasses of water you should drink: $glassesDrank")

        Spacer(modifier = Modifier.height(75.dp))

        OutlinedTextField(
            value = goal,
            onValueChange = { updatedGoal ->
                onGoalChange(updatedGoal)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Set Your Water Intake Goal (in glasses)") }
        )

        Text("Your goal is to drink $goal glasses of water.")

        Spacer(modifier = Modifier.height(75.dp))


        Button(onClick = {
            onGlassesDrankCounterChange(glassesDrankCounter + 1)
        }) {
            Text("I drank a glass of water")
        }

        Text("Number of glasses of water you drank: $glassesDrankCounter")
    }
}