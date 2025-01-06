package com.example.gf5.viewModels

import androidx.compose.ui.tooling.data.EmptyGroup.data

class MainViewModel<MainActivity> {

    class NavigationEvent {
        class NavigateToRegistration {

        }

        class NavigateToLogin {

        }

        class NavigateToDriverHome {

        }

        class NavigateToRiderHome {

        }

        class ShowError {

        }


    }

    val navigationEvent: Any
        get() {
            TODO()
        }

    fun determineNavigation(mainActivity: MainActivity) {

    }

    fun fetchData() {
        viewModelScope.launch {
            val response = repository.fetchData()
            data.postValue(response.body())
        }

    }
}