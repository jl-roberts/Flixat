package com.jlroberts.flixat.ui.popular

import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(application= HiltTestApplication::class)
@MediumTest
class PopularFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}