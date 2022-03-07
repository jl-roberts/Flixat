package com.jlroberts.flixat.di

import com.jlroberts.flixat.di.scope.BindingScope
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

@BindingScope
@DefineComponent(parent = SingletonComponent::class)
interface BindingComponent