package com.jlroberts.flixat.ui.detail

import androidx.lifecycle.ViewModel
import com.jlroberts.flixat.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {


}