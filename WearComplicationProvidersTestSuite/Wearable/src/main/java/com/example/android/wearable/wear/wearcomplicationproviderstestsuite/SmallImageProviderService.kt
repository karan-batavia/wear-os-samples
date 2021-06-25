/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.wear.wearcomplicationproviderstestsuite

import android.app.PendingIntent
import android.content.ComponentName
import android.graphics.drawable.Icon
import androidx.datastore.core.DataStore
import androidx.wear.complications.ComplicationProviderService
import androidx.wear.complications.ComplicationRequest
import androidx.wear.complications.data.ComplicationData
import androidx.wear.complications.data.ComplicationText
import androidx.wear.complications.data.ComplicationType
import androidx.wear.complications.data.PlainComplicationText
import androidx.wear.complications.data.SmallImage
import androidx.wear.complications.data.SmallImageComplicationData
import androidx.wear.complications.data.SmallImageType

/**
 * A complication provider that supports only [ComplicationType.SMALL_IMAGE] and cycles
 * between the different image styles on tap.
 *
 * Note: This subclasses [SuspendingComplicationProviderService] instead of [ComplicationProviderService] to support
 * coroutines, so data operations (specifically, calls to [DataStore]) can be supported directly in the
 * [onComplicationRequest].
 * See [SuspendingComplicationProviderService] for the implementation details.
 *
 * If you don't perform any suspending operations to update your complications, you can subclass
 * [ComplicationProviderService] and override [onComplicationRequest] directly.
 * (see [NoDataProviderService] for an example)
 */
class SmallImageProviderService : SuspendingComplicationProviderService() {
    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        if (request.complicationType != ComplicationType.SMALL_IMAGE) {
            return null
        }
        val args = ComplicationToggleArgs(
            providerComponent = ComponentName(this, javaClass),
            complicationInstanceId = request.complicationInstanceId
        )
        val complicationTogglePendingIntent =
            ComplicationToggleReceiver.getComplicationToggleIntent(
                context = this,
                args = args
            )
        // Suspending function to retrieve the complication's state
        val state = args.getState(this)
        val case = Case.values()[state.mod(Case.values().size)]
        return getComplicationData(
            tapAction = complicationTogglePendingIntent,
            case = case
        )
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData =
        getComplicationData(
            tapAction = null,
            case = Case.PHOTO
        )

    private fun getComplicationData(
        tapAction: PendingIntent?,
        case: Case
    ): ComplicationData {
        val smallImage: SmallImage
        val contentDescription: ComplicationText

        when (case) {
            Case.PHOTO -> {
                // An image using IMAGE_STYLE_PHOTO may be cropped to fill the space given to it.
                smallImage = SmallImage.Builder(
                    image = Icon.createWithResource(this, R.drawable.aquarium),
                    type = SmallImageType.PHOTO
                ).build()

                contentDescription = PlainComplicationText.Builder(
                    text = getText(R.string.small_image_photo_content_description)
                ).build()
            }
            Case.ICON -> {
                // An image using IMAGE_STYLE_ICON must not be cropped, and should fit within the
                // space given to it.
                smallImage = SmallImage.Builder(
                    image = Icon.createWithResource(this, R.drawable.ic_launcher),
                    type = SmallImageType.ICON
                ).build()

                contentDescription = PlainComplicationText.Builder(
                    text = getText(R.string.small_image_icon_content_description)
                ).build()
            }
        }

        return SmallImageComplicationData.Builder(
            smallImage = smallImage,
            contentDescription = contentDescription
        )
            .setTapAction(tapAction)
            .build()
    }

    private enum class Case {
        PHOTO, ICON
    }
}
