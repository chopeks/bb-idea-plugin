package com.chopeks.codecompletion

import com.chopeks.psi.SquirrelStringLiteral
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.util.ProcessingContext
import java.nio.file.Files
import kotlin.io.path.relativeTo
import kotlin.streams.toList

class BBStringCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {

		val file = parameters.originalFile
		val element = parameters.position
		if (element.parent is SquirrelStringLiteral) {
			val referenceName = element.text.replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "").trim('"').trim('.').trim(';')
			if (referenceName.length < 3) {
				result.addElement(LookupElementBuilder.create("scripts/").withIcon(AllIcons.Nodes.Alias))
				return
			}
			val sourceRoots = ProjectRootManager.getInstance(file.project).contentSourceRoots
			// Collect all files recursively
			val files = sourceRoots.flatMap { sourceRoot ->
				Files.walk(sourceRoot.path.toNioPathOrNull())
					.filter { Files.isRegularFile(it) } // Only regular files
					.map { it.relativeTo(sourceRoot.path.toNioPathOrNull()!!).toString().replace("\\", "/").substringBefore(".nut") } // Convert paths to relative
					.filter { it.startsWith(referenceName) }
					.toList()
			}
			files.forEach { result.addElement(LookupElementBuilder.create(it).withIcon(AllIcons.Nodes.Alias)) }
		}
	}
}
