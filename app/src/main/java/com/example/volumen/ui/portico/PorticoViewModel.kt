package com.example.volumen.ui.portico

import androidx.lifecycle.*
import com.example.volumen.api.portico.PorticoStatus
import com.example.volumen.data.repository.Repository
import com.example.volumen.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PorticoViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    private val porticoMutableLiveData = MutableLiveData<Resource<PorticoStatus>>()
    val porticoLiveData: LiveData<Resource<PorticoStatus>> = porticoMutableLiveData

    fun getPortico(url: String) =
        viewModelScope.launch {
            repository.porticoStatus(url).collect {
                porticoMutableLiveData.value = it
            }
        }

}