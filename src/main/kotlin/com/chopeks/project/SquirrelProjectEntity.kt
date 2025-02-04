package com.chopeks.project

import com.intellij.platform.workspace.storage.WorkspaceEntity

interface SquirrelProjectEntity : WorkspaceEntity {
	val project: SquirrelProjectData
}