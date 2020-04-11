package com.alfanse.feedmycity.ui.donor

import androidx.lifecycle.ViewModel
import com.alfanse.feedmycity.utils.Utils
import javax.inject.Inject

class DonorHomeViewModel @Inject constructor(
    private val utils: Utils
) : ViewModel() {

    fun logoutUser() = utils.logoutUser()
}