package com.gribansky.jobbooster.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gribansky.jobbooster.net.IhhApi
import kotlinx.coroutines.launch

class HomeViewModel (api: IhhApi) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    init {
        viewModelScope.launch {
            //api.boostResume()
        }
    }
    val text: LiveData<String> = _text
}