package com.iries.youtubealarm.presentation.screens.alarms

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.youtubealarm.data.entity.AlarmInfo
import com.iries.youtubealarm.data.repository.AlarmsRepository
import com.iries.youtubealarm.domain.AlarmManager
import com.iries.youtubealarm.domain.constants.DayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val alarmsRepo: AlarmsRepository
) : ViewModel() {

    private val _allAlarms = MutableStateFlow<List<AlarmInfo>>(emptyList())
    val allAlarms: StateFlow<List<AlarmInfo>> = _allAlarms

    init {
        viewModelScope.launch {
            alarmsRepo.getAllAlarms().collect { channels ->
                _allAlarms.value = channels
            }
        }
    }


    fun addAlarm(
        alarm: AlarmInfo
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarm.setAlarmId(alarmsRepo.insert(alarm))
    }

    fun updateAlarm(
        alarm: AlarmInfo
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.update(alarm)
    }

    fun removeAlarm(
        context: Context,
        alarm: AlarmInfo
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.delete(alarm)
        cancelAlarms(context, alarm.getDaysId())
    }

    fun activateAlarm(
        context: Context,
        alarm: AlarmInfo
    ) {
        alarm.setActive(true)
        alarm.getDaysId().keys.forEach {
            AlarmManager.setRepeatingAlarm(context, alarm, it)
        }
    }

    fun cancelAlarms(
        context: Context,
        daysId: HashMap<DayOfWeek, Int>
    ) {
        AlarmManager.stopCurrentAlarm(context)
        daysId.keys.forEach {
            AlarmManager.cancelIntent(it.getId(), context)
        }
    }

}