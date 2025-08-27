package com.iries.alarm.presentation.screens.alarms

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.alarm.data.local.repository.AlarmsRepository
import com.iries.alarm.domain.usecases.AlarmUseCase
import com.iries.alarm.domain.models.Alarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val alarmsRepo: AlarmsRepository
) : ViewModel() {

    private val _allAlarms = MutableStateFlow<List<Alarm>>(emptyList())
    val allAlarms: StateFlow<List<Alarm>> = _allAlarms

    init {
        populateAlarms()
    }

    private fun populateAlarms() = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.getAllAlarms().collect { channels ->
            _allAlarms.value = channels
        }
    }

    private fun addAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.insert(alarm)
    }

    private fun updateAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.update(alarm)
    }

    fun removeAlarm(context: Context, alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.delete(alarm)
        cancelAlarms(context, alarm.days)
    }

    private fun activateAlarm(context: Context, alarm: Alarm) {
        alarm.isActive = true
        for (day in alarm.days) {
            AlarmUseCase.setRepeatingAlarm(
                context = context,
                hour = alarm.hour,
                minute = alarm.minute,
                dayId = day.key,
                requestCode = day.value
            )
        }
    }

    private fun cancelAlarms(context: Context, daysId: HashMap<Int, Int>) {
        daysId.values.forEach { requestCode ->
            AlarmUseCase.cancelIntent(requestCode, context)
        }
    }

    fun draftNewAlarm():Alarm{
        val currentTime: LocalTime = LocalTime.now()
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return Alarm(
            hour = currentTime.hour,
            minute = currentTime.minute,
            days = hashMapOf(dayOfWeek to 0)
        )
    }

    fun editAlarm(
        context: Context, selectedAlarm: Alarm,
        updatedTime: LocalTime, updatedDays: MutableSet<Int>
    ) {
        selectedAlarm.hour = updatedTime.hour
        selectedAlarm.minute = updatedTime.minute
        selectedAlarm.days.clear()
        updatedDays.forEach { dayId ->
            val requestCode = UUID.randomUUID().hashCode()
            selectedAlarm.days[dayId] = requestCode
        }

        if (allAlarms.value.contains(selectedAlarm)) {
            if (selectedAlarm.isActive) {
                cancelAlarms(context, selectedAlarm.days)
                activateAlarm(context, selectedAlarm)
            }
            updateAlarm(selectedAlarm)
        } else {
            activateAlarm(context, selectedAlarm)
            addAlarm(selectedAlarm)
        }
    }

    fun toggleAlarmActivity(context: Context, selectedAlarm: Alarm, isActive: Boolean) {
        if (isActive) {
            println("Set repeating alarm")
            activateAlarm(context, selectedAlarm)
        } else {
            println("Stop alarm alarm")
            cancelAlarms(context, selectedAlarm.days)
        }
        selectedAlarm.isActive = isActive
        updateAlarm(selectedAlarm)
    }

}