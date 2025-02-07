package com.chopeks.psi.index

import com.chopeks.SquirrelFileType
import com.chopeks.psi.SquirrelAssignExpression
import com.chopeks.psi.SquirrelExpressionStatement
import com.chopeks.psi.SquirrelReferenceExpression
import com.chopeks.psi.SquirrelTableExpression
import com.chopeks.psi.impl.LOG
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.vfs.isFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import java.io.DataInput
import java.io.DataOutput

class BBGlobalSymbolsIndex : FileBasedIndexExtension<String, String>() {
	private data class Model(val parent: String, val file: String, val symbols: List<String>) {
		override fun toString() = "${symbols.joinToString("$$")}@@$file"
	}

	override fun getName() = BBIndexes.BBGlobalSymbols
	override fun getVersion() = 1
	override fun dependsOnFileContent() = true
	override fun getKeyDescriptor() = EnumeratorStringDescriptor.INSTANCE!!
	override fun getValueExternalizer(): DataExternalizer<String> {
		return object : DataExternalizer<String> {
			override fun save(out: DataOutput, value: String) {
				out.writeUTF(value)
			}

			override fun read(input: DataInput): String = input.readUTF()
		}
	}

	override fun getInputFilter() = FileBasedIndex.InputFilter { file ->
		file.isFile && file.name.endsWith(SquirrelFileType.EXTENSION)
	}

	override fun getIndexer() = object : DataIndexer<String, String, FileContent> {
		override fun map(inputData: FileContent): MutableMap<String, String> {
			if (inputData.file.isDirectory)
				return mutableMapOf()
			val classFields = mutableMapOf<String, String>()
			ReadAction.run<Exception> {
				findFields(inputData.psiFile)?.forEach {
					classFields[it.parent] = it.toString()
					LOG.warn("added ${it.parent} -> $it")
				}
			}
			return classFields
		}
	}

	private fun findFields(file: PsiFile): List<Model>? {
		val path = file.virtualFile.toNioPathOrNull()
			?: return null

		val expressions = mutableSetOf<String>()
		file.childrenOfType<SquirrelExpressionStatement>().forEach x@{
			it.childrenOfType<SquirrelAssignExpression>().forEach { assign ->
				val leftSideExpression = assign.expressionList.firstOrNull { it is SquirrelReferenceExpression }
					?: return@forEach
				if (assign.parent.parent != file)
					return@forEach
				val rightSideExpression = assign.expressionList.last()
				val qualifiedName = leftSideExpression.text
				if (!(qualifiedName.startsWith("::") || qualifiedName.startsWith("gt.")))
					return@forEach
				(listOf(assign.expressionList.first()) + PsiTreeUtil.findChildrenOfType(assign.expressionList.first(), SquirrelReferenceExpression::class.java)).forEachIndexed { i, refExpr ->
					if (i == 0) {
						if (rightSideExpression is SquirrelTableExpression) {
							rightSideExpression.flatten().mapNotNull { "${refExpr.text}.$it".trim('.') }
								.map { it.replace("gt.", "::") }
								.also(expressions::addAll)
						}
					}
					expressions.add(refExpr.text.replace("gt.", "::"))
				}
			}
		}
		val fieldMap = sortedListShortNames(expressions)
		return fieldMap.keys.map {
			Model(it, path.toString(), fieldMap[it] ?: emptyList())
		}
	}

	private fun sortedListShortNames(set: Set<String>): Map<String, List<String>> {
		val sortedList = set.toList().sorted().map { "$it.<eof>" }
		val hierarchy = mutableMapOf<String, MutableList<String>>()
		sortedList.map { it.split('.') }.forEach { path ->
			val pathSize = path.size
			for (i in pathSize - 1 downTo 1) {
				val parent = path.subList(0, i).joinToString(".")
				val child = path[i]
				hierarchy.computeIfAbsent(parent) { mutableListOf() }.add(child)
				if ("." !in parent)
					hierarchy.computeIfAbsent("::") { mutableListOf() }.add(parent.trim(':'))
			}
		}
		hierarchy.forEach { (parent, children) ->
			hierarchy[parent] = children.distinct().sorted().toMutableList()
		}
		return hierarchy
	}
}
