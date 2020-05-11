package app.roaim.dtbazar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.roaim.dtbazar.repository.InfoRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: InfoRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    val ipInfo = repository.getIpInfo()
}