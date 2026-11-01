package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Hold Chapter 3", appName)
  }

  @Test
  fun `verify game view model initialization and tap`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = GameViewModel(application)
    val initialState = viewModel.uiState.value

    assertNotNull(initialState.tactics)
    assertTrue(initialState.tactics.isNotEmpty())
    assertTrue(initialState.balanceDemands.isNotEmpty())

    val initialClicks = initialState.totalClicks
    val initialDamage = initialState.totalDamageDealt

    viewModel.tapDog(Offset.Zero)

    val updatedState = viewModel.uiState.value
    assertEquals(initialClicks + 1, updatedState.totalClicks)
    assertTrue(updatedState.totalDamageDealt > initialDamage)
  }
}
