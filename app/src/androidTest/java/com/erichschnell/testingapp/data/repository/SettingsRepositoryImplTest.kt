package com.erichschnell.testingapp.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.core.mockwebserver.MockWebServerUrlHolder
import com.erichschnell.testingapp.core.mockwebserver.rules.MockWebServerRule
import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsRepositoryImplTest {

    @get:Rule(order = 0)
    val mockWebServer = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var settingsRepository: SettingsRepository


    @Before
    fun setUp() = runTest {
        hilt.inject()
        settingsRepository.clear()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }



    @Test
    fun givenNoDataSaved_whenInStockOnlyIsRead_thenReturnsDefaultFalse() = runTest {
        val result = settingsRepository.inStockOnly.first()
        assertTrue(!result)
    }

    @Test
    fun givenNoDataSaved_whenShowTaxesIsRead_thenReturnsDefaultFalse() = runTest {
        val result = settingsRepository.showTaxes.first()
        assertTrue(!result)
    }

    @Test
    fun givenNoDataSaved_whenSelectedCategoryIsRead_thenReturnsDefaultNull() = runTest {
        val result = settingsRepository.selectedCaregory.first()
        assertNull(result)
    }

    @Test
    fun givenNoDataSaved_whenFiltersVisibleIsRead_thenReturnsDefaultTrue() = runTest {
        val result = settingsRepository.filtersVisible.first()
        assertTrue(result)
    }

    @Test
    fun givenNoDataSaved_whenThemeModeIsRead_thenReturnsDefaultThemeModeSYSTEM() = runTest {
        val result = settingsRepository.themeMode.first()
        assertTrue(result == ThemeMode.SYSTEM)
    }

    @Test
    fun givenNoDataSaved_whenSortOptionIsRead_thenReturnsDefaultFalse() = runTest {
        val result = settingsRepository.sortOption.first()
        assertTrue(result == SortOption.NONE)
    }

    @Test
    fun givenMultipleSettingsChanges_whenReadAll_thenStateIsConsistent() = runTest {
        settingsRepository.setInStockOnly(true)
        settingsRepository.setShowTaxes(true)
        settingsRepository.setSelectedCaregory("category")
        settingsRepository.setFiltersVisible(false)
        settingsRepository.setThemeMode(ThemeMode.DARK)
        settingsRepository.setSortOption(SortOption.DISCOUNT)

        val inStockOnly = settingsRepository.inStockOnly.first()
        val showTaxes = settingsRepository.showTaxes.first()
        val selectedCaregory = settingsRepository.selectedCaregory.first()
        val filtersVisible = settingsRepository.filtersVisible.first()
        val themeMode = settingsRepository.themeMode.first()
        val sortOption = settingsRepository.sortOption.first()

        assertTrue(inStockOnly)
        assertTrue(showTaxes)

        assertNotNull(selectedCaregory)
        assertEquals("category", selectedCaregory)

        assertTrue(!filtersVisible)

        assertTrue(themeMode == ThemeMode.DARK)

        assertTrue(sortOption == SortOption.DISCOUNT)

    }
}