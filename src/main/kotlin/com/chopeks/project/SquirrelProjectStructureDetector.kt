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
 * See the License for the specific language squirrelverning permissions and
 * limitations under the License.
 */
package com.chopeks.project

import com.chopeks.SquirrelFileType
import com.chopeks.SquirrelModuleType
import com.intellij.ide.util.importProject.ModuleDescriptor
import com.intellij.ide.util.importProject.ProjectDescriptor
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot
import com.intellij.ide.util.projectWizard.importSources.ProjectFromSourcesBuilder
import com.intellij.ide.util.projectWizard.importSources.ProjectStructureDetector
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.containers.ContainerUtil
import java.io.File
import java.util.regex.Pattern
import javax.swing.Icon

class SquirrelProjectStructureDetector : ProjectStructureDetector() {
	override fun detectRoots(
		dir: File,
		children: Array<File>,
		base: File,
		result: MutableList<DetectedProjectRoot>
	): DirectoryProcessingResult {
		val pattern = Pattern.compile(".*\\." + SquirrelFileType.EXTENSION)
		val filesByMask = FileUtil.findFilesByMask(pattern, base)
		if (filesByMask.isNotEmpty()) {
			result.add(object : DetectedProjectRoot(dir) {
				override fun getRootTypeName(): String {
					return SquirrelModuleType.MODULE_TYPE_ID
				}
			})
		}
		return DirectoryProcessingResult.SKIP_CHILDREN
	}

	override fun setupProjectStructure(
		roots: Collection<DetectedProjectRoot>,
		projectDescriptor: ProjectDescriptor,
		builder: ProjectFromSourcesBuilder
	) {
		if (!roots.isEmpty() && !builder.hasRootsFromOtherDetectors(this)) {
			var modules = projectDescriptor.modules
			if (modules.isEmpty()) {
				modules = ArrayList()
				for (root in roots) {
					modules.add(ModuleDescriptor(root.directory, SquirrelModuleType.instance, ContainerUtil.emptyList()))
				}
				projectDescriptor.modules = modules
			}
		}
	}

	override fun createWizardSteps(builder: ProjectFromSourcesBuilder, projectDescriptor: ProjectDescriptor, stepIcon: Icon): List<ModuleWizardStep> {
		return emptyList()
	}
}
