package com.erichschnell.testingapp.presentation.settings.screen

import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.fakes.FakeSettingRepository
import com.erichschnell.testingapp.presentation.settings.models.SettingUiAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.theories.suppliers.TestedOn

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun exampleTestCrash() = runTest {
        val viewmodel = SettingViewModel(FakeSettingRepository())

        viewmodel.onAction(SettingUiAction.SetInStockOnly(true))

        assertTrue(viewmodel.uiState.value.inStockOnly)

    }

    @Test
    fun secondExample() = runTest(mainDispatcherRule.scheduler) {
        val settingRepo = FakeSettingRepository().apply {
            setInStockOnly(true)
        }

        val viewmodel = SettingViewModel(settingRepo)
        advanceUntilIdle()


        assertEquals(true, viewmodel.uiState.value.inStockOnly)

    }

}