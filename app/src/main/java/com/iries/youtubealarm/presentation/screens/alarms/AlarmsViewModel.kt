package com.iries.youtubealarm.presentation.screens.alarms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.youtubealarm.data.entity.AlarmInfo
import com.iries.youtubealarm.data.repository.AlarmsRepository
import com.iries.youtubealarm.domain.AlarmManager
import com.iries.youtubealarm.domain.constants.DayOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val alarmsRepo: AlarmsRepository
) : ViewModel() {
    private var allAlarms: LiveData<List<AlarmInfo>> = alarmsRepo.getAllAlarms()

    fun getAllAlarms(): LiveData<List<AlarmInfo>> {
        return allAlarms
    }

    fun insert(
        context: Context,
        alarm: AlarmInfo
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarm.setAlarmId(alarmsRepo.insert(alarm))
        setRepeatingAlarm(context, alarm)
    }

    fun update(
        alarm: AlarmInfo
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.update(alarm)
    }

    fun remove(
        context: Context,
        alarm: AlarmInfo
    ) = viewModelScope.launch(Dispatchers.IO) {
        alarmsRepo.delete(alarm)
        stopAlarms(context, alarm.getDaysId())

    }

    fun setRepeatingAlarm(
        context: Context,
        alarm: AlarmInfo
    ) {
        alarm.getDaysId().keys.forEach {
            AlarmManager.setRepeatingAlarm(context, alarm, it)
        }
    }

    fun stopAlarms(
        context: Context,
        daysId: HashMap<DayOfWeek, Int>
    ) {
        daysId.keys.forEach {
            AlarmManager.cancelIntent(it.getId(), context)
        }
    }

}