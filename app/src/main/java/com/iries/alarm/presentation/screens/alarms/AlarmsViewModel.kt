package com.iries.alarm.presentation.screens.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.alarm.domain.models.Alarm
import com.iries.alarm.domain.usecases.AlarmsUseCase
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
    private val alarmsUseCase: AlarmsUseCase
) : ViewModel() {

    private val _allAlarms = MutableStateFlow<List<Alarm>>(emptyList())
    val allAlarms: StateFlow<List<Alarm>> = _allAlarms

    init {
        populateAlarms()
    }

    private fun populateAlarms() = viewModelScope.launch(Dispatchers.IO) {
        alarmsUseCase.getAllAlarms().collect { channels ->
            _allAlarms.value = channels
        }
    }

    fun removeAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmsUseCase.removeAlarm(alarm)
    }

    fun draftNewAlarm(): Alarm {
        val currentTime: LocalTime = LocalTime.now()
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return Alarm(
            hour = currentTime.hour,
            minute = currentTime.minute,
            days = hashMapOf(dayOfWeek to 0)
        )
    }

    fun changeAlarmDateAndTime(
        alarm: Alarm, updatedTime: LocalTime,
        updatedDays: MutableSet<Int>
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarm.hour = updatedTime.hour
        alarm.minute = updatedTime.minute

        val prevDays = alarm.days.values
        // Clear and update days list
        alarm.days.clear()
        updatedDays.forEach { dayId ->
            val requestCode = UUID.randomUUID().hashCode()
            alarm.days[dayId] = requestCode
        }

        if(alarm.isActive){
            alarmsUseCase.cancelAlarm(alarm, prevDays)
            alarmsUseCase.activateAlarm(alarm)
        }
        alarmsUseCase.updateAlarm(alarm)

        if (allAlarms.value.contains(alarm)) {
            if (alarm.isActive) {
                alarmsUseCase.cancelAlarm(alarm, prevDays)
                alarmsUseCase.activateAlarm(alarm)
            }
            alarmsUseCase.updateAlarm(alarm)
        } else {
            alarmsUseCase.activateAlarm(alarm)
            alarmsUseCase.addAlarm(alarm)
        }
    }

    fun toggleAlarmActivity(alarm: Alarm, isActive: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if (isActive) {
                alarmsUseCase.activateAlarm(alarm)
            } else {
                alarmsUseCase.cancelAlarm(alarm)
            }
            alarmsUseCase.updateAlarm(alarm)
        }

}