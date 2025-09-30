package com.iries.alarm.presentation.screens.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.alarm.domain.models.Alarm
import com.iries.alarm.domain.usecases.AlarmsUseCase
import com.iries.alarm.domain.usecases.SoundCloudApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val alarmsUseCase: AlarmsUseCase,
    private val soundCloudApiUseCase: SoundCloudApiUseCase
) : ViewModel() {

    private val _allAlarms = MutableStateFlow<List<Alarm>>(emptyList())
    val allAlarms: StateFlow<List<Alarm>> = _allAlarms

    init {
        onInit()
    }

    private fun onInit() = viewModelScope.launch(Dispatchers.IO) {
        alarmsUseCase.getAllAlarms().collect { channels ->
            _allAlarms.value = channels
        }
    }

    suspend fun checkAvailableArtists() : Boolean {
        return soundCloudApiUseCase.getAllArtists().first().isEmpty()
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
            days = hashMapOf(dayOfWeek to 0),
            isActive = true
        )
    }

    fun editAlarm(alarm: Alarm, updatedTime: LocalTime, updatedDays: MutableSet<Int>) =
        viewModelScope.launch(Dispatchers.IO) {
            alarm.hour = updatedTime.hour
            alarm.minute = updatedTime.minute

            val currentDays: HashMap<Int, Int> =
                updatedDays.associateWithTo(HashMap()) { UUID.randomUUID().hashCode() }
            if (alarm.isActive) {
                alarmsUseCase.cancelAlarm(alarm)
                for (dayCode in currentDays) {
                    alarmsUseCase.setAlarm(
                        alarm.hour, alarm.minute, dayCode.key, dayCode.value
                    )
                }
            }
            alarm.days = currentDays

            if (allAlarms.value.contains(alarm))
                alarmsUseCase.updateAlarm(alarm)
            else
                alarmsUseCase.addAlarm(alarm)
        }

    fun toggleAlarmActivity(alarm: Alarm, isActive: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            alarm.isActive = isActive
            if (isActive) {
                for (dayCode in alarm.days) {
                    alarmsUseCase.setAlarm(
                        alarm.hour, alarm.minute, dayCode.key, dayCode.value
                    )
                }
            } else {
                alarmsUseCase.cancelAlarm(alarm)
            }
            alarmsUseCase.updateAlarm(alarm)
        }

}