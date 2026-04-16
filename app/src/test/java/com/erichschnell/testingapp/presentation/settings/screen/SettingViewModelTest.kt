package com.erichschnell.testingapp.presentation.settings.screen

import android.R.style.Theme
import app.cash.turbine.test
import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.fakes.FakeSettingRepository
import com.erichschnell.testingapp.presentation.settings.models.SettingUiAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `given repository with values when viewmodel is initialized then ui state is updated`() =
        runTest(mainDispatcherRule.scheduler) {
        //given
        val settingRepo = FakeSettingRepository().apply {
            setInStockOnly(true)
        }

        //when
        val viewmodel = SettingViewModel(settingRepo)

        //then
        viewmodel.uiState.test {
            val state = awaitItem()
            assertTrue(state.inStockOnly)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given viewmodel when theme mode changed then ui state and repository is updated`() =
        runTest(mainDispatcherRule.scheduler) {
            //given
            val settingRepo = FakeSettingRepository().apply { setThemeMode(ThemeMode.DARK) }
            val viewmodel = SettingViewModel(settingRepo)

            //then
            viewmodel.uiState.test {
                assertEquals(ThemeMode.DARK.id,awaitItem().themeMode.id)

                //when
                viewmodel.onAction(SettingUiAction.SetThemeMode(ThemeMode.LIGHT))

                val newState = awaitItem()
                assertEquals(ThemeMode.LIGHT.id,newState.themeMode.id)
                assertEquals(ThemeMode.LIGHT.id,settingRepo.themeMode.first().id)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given viewmodel when in stock only changed then ui state and repository is updated`() =
        runTest(mainDispatcherRule.scheduler) {
            //given
            val settingRepo = FakeSettingRepository().apply { setInStockOnly(true) }
            val viewmodel = SettingViewModel(settingRepo)

            //then
            viewmodel.uiState.test {
                assertEquals(true,awaitItem().inStockOnly)

                //when
                viewmodel.onAction(SettingUiAction.SetInStockOnly(false))

                val newState = awaitItem()
                assertEquals(false,newState.inStockOnly)
                assertEquals(false,settingRepo.inStockOnly.first())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given viewmodel when show taxes changed then ui state and repository is updated`() =
        runTest(mainDispatcherRule.scheduler) {
            //given
            val settingRepo = FakeSettingRepository().apply { setShowTaxes(true) }
            val viewmodel = SettingViewModel(settingRepo)

            //then
            viewmodel.uiState.test {
                assertEquals(true,awaitItem().showTaxes)

                //when
                viewmodel.onAction(SettingUiAction.SetShowTaxes(false))

                val newState = awaitItem()
                assertEquals(false,newState.showTaxes)
                assertEquals(false,settingRepo.showTaxes.first())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given viemodel when repository changed externally then ui state is updated autumatically`() =
        runTest(mainDispatcherRule.scheduler) {
            //given
            val settingRepo = FakeSettingRepository().apply { setInStockOnly(true) }
            val viewmodel = SettingViewModel(settingRepo)

            //then
            viewmodel.uiState.test {
                assertEquals(true,awaitItem().inStockOnly)

                //when
                settingRepo.setInStockOnly(false)

                val newState = awaitItem()
                assertEquals(false,newState.inStockOnly)

                cancelAndIgnoreRemainingEvents()
            }
        }
}