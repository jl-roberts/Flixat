package com.jlroberts.flixat.di

import androidx.databinding.DataBindingComponent
import com.jlroberts.flixat.di.scope.BindingScope
import com.jlroberts.flixat.utils.BindingAdapters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn

@EntryPoint
@BindingScope
@InstallIn(BindingComponent::class)
interface BindingEntryPoint : DataBindingComponent {

    @BindingScope
    override fun getBindingAdapters(): BindingAdapters
}