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

    fun addAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.insert(alarm)
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.update(alarm)
    }

    fun removeAlarm(context: Context, alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.delete(alarm)
        cancelAlarms(context, alarm.days)
    }

    fun activateAlarm(context: Context, alarm: Alarm) {
        alarm.isActive = true
        for (day in alarm.days.keys) {
            val requestCode = UUID.randomUUID().hashCode()
            alarm.days[day] = requestCode
            AlarmUseCase.setRepeatingAlarm(
                context = context,
                hour = alarm.hour,
                minute = alarm.minute,
                dayId = day,
                requestCode = requestCode
            )
        }
    }

    fun cancelAlarms(context: Context, daysId: HashMap<Int, Int>) {
        daysId.values.forEach { requestCode ->
            AlarmUseCase.cancelIntent(requestCode, context)
        }
    }

}