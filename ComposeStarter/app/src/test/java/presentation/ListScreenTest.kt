/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package presentation

import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.example.android.wearable.composestarter.presentation.ListScreen
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class ListScreenTest(override val device: WearDevice) : WearScreenshotTest() {
    override val tolerance = 0.03f

    @Test
    fun listScreenTest() {
        runTest {
            AppScaffold(
                timeText = { ResponsiveTimeText(timeSource = FixedTimeSource) }
            ) {
                ListScreen()
            }
        }

        composeRule.onNode(hasScrollToIndexAction())
            .performScrollToIndex(3)
            .performTouchInput {
                swipeUp()
            }

        captureScreenshot("_end")
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun devices() = WearDevice.entries
    }
}
