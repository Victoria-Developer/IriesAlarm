package com.iries.alarm.presentation.screens.alarms

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.alarm.data.local.entity.AlarmEntity
import com.iries.alarm.data.local.repository.AlarmsRepository
import com.iries.alarm.domain.usecases.AlarmUseCase
import com.iries.alarm.domain.constants.Day
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

    private val _allAlarms = MutableStateFlow<List<AlarmEntity>>(emptyList())
    val allAlarms: StateFlow<List<AlarmEntity>> = _allAlarms

    init {
        viewModelScope.launch {
            alarmsRepo.getAllAlarms().collect { channels ->
                _allAlarms.value = channels
            }
        }
    }


    fun addAlarm(
        alarm: AlarmEntity
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarm.setAlarmId(alarmsRepo.insert(alarm))
    }

    fun updateAlarm(
        alarm: AlarmEntity
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.update(alarm)
    }

    fun removeAlarm(
        context: Context,
        alarm: AlarmEntity
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.delete(alarm)
        cancelAlarms(context, alarm.getDaysId())
    }

    fun activateAlarm(
        context: Context,
        alarm: AlarmEntity
    ) {
        alarm.setActive(true)
        alarm.getDaysId().keys.forEach {
            AlarmUseCase.setRepeatingAlarm(context, alarm, it)
        }
    }

    fun cancelAlarms(
        context: Context,
        daysId: HashMap<Day, Int>
    ) {
        //AlarmManager.stopCurrentAlarm(context)
        daysId.keys.forEach {
            AlarmUseCase.cancelIntent(it.id, context)
        }
    }

}