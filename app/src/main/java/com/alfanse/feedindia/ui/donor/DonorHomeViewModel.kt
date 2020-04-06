package com.alfanse.feedindia.ui.donor

import androidx.lifecycle.ViewModel
import com.alfanse.feedindia.utils.Utils
import javax.inject.Inject

class DonorHomeViewModel @Inject constructor(
    private val utils: Utils
) : ViewModel() {

    fun logoutUser() = utils.logoutUser()
}