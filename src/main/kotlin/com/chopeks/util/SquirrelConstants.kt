/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chopeks.util

import com.intellij.notification.NotificationGroup
import com.intellij.openapi.wm.ToolWindowId
import org.jetbrains.annotations.NonNls

object SquirrelConstants {
	const val MODULE_TYPE_ID: String = "SQUIRREL_MODULE"
	const val SDK_TYPE_ID: String = "SQUIRREL_SDK"
	const val SQUIRREL_VERSION_FILE_PATH: @NonNls String = "include/squirrel.h"
	const val SQUIRREL_COMPILER_NAME: @NonNls String = "sq"
	const val SQUIRREL: @NonNls String = "squirrel"

	val SQUIRREL_NOTIFICATION_GROUP: NotificationGroup = NotificationGroup.balloonGroup("Squirrel plugin notifications")
	val SQUIRREL_EXECUTION_NOTIFICATION_GROUP: NotificationGroup = NotificationGroup.toolWindowGroup("Squirrel Execution", ToolWindowId.RUN)
}
