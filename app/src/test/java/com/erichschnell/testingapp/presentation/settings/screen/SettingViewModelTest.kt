package com.erichschnell.testingapp.presentation.settings.screen

import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.fakes.FakeSettingRepository
import com.erichschnell.testingapp.presentation.settings.models.SettingUiAction
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.theories.suppliers.TestedOn

class SettingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun exampleTestCrash() = runTest {
        val viewmodel = SettingViewModel(FakeSettingRepository())

        viewmodel.onAction(SettingUiAction.SetInStockOnly(true))

        assertTrue(viewmodel.uiState.value.inStockOnly)

    }

}