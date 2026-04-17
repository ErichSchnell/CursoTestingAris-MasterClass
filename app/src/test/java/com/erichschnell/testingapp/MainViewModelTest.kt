package com.erichschnell.testingapp

import app.cash.turbine.test
import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import com.erichschnell.testingapp.fakes.FakeSettingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        fakeSettingRepo: SettingsRepository = FakeSettingRepository()
    ): MainViewModel {
        return MainViewModel(
            settingsRepository = fakeSettingRepo
        )
    }

    /*
    ------------------------------------- HECHO --------------------------------
    given theme mode when initialized then emits success state
    given theme mode when theme mode changed then update theme mode in repo and update ui state
     */

    @Test
    fun `given theme mode when initialized then emits success state`() =
        runTest( mainDispatcherRule.scheduler ){
            val fakeSettingRepo = FakeSettingRepository().apply { setThemeMode(ThemeMode.DARK) }
            val viewModel = createViewModel(fakeSettingRepo)

            viewModel.themeMode.test {
                val state = awaitItem()
                assertEquals(ThemeMode.DARK, state)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given theme mode when theme mode changed then update theme mode in repo and update ui state`() =
        runTest( mainDispatcherRule.scheduler ){
            val fakeSettingRepo = FakeSettingRepository().apply { setThemeMode(ThemeMode.DARK) }
            val viewModel = createViewModel(fakeSettingRepo)

            viewModel.themeMode.test {
                awaitItem()

                fakeSettingRepo.setThemeMode(ThemeMode.LIGHT)

                val state = awaitItem()
                assertEquals(ThemeMode.LIGHT, state)

                cancelAndIgnoreRemainingEvents()
            }
        }

}