package com.iries.alarm.presentation.activities

import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _loginCode = MutableStateFlow<String?>(null)
    val loginCode: StateFlow<String?> = _loginCode

    fun handleIntent(intent: Intent?) {
        _loginCode.value = intent?.data?.getQueryParameter("code")
    }
}