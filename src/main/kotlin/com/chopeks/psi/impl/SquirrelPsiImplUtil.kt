package com.chopeks.psi.impl

import com.chopeks.SquirrelFileType
import com.chopeks.psi.*
import com.chopeks.psi.reference.SquirrelGfxReference
import com.chopeks.psi.reference.SquirrelScriptReference
import com.chopeks.psi.reference.SquirrelStdIdReference
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference


internal val LOG by lazy(LazyThreadSafetyMode.PUBLICATION) {
	logger<SquirrelPsiImplUtil>()
}

object SquirrelPsiImplUtil {
	private val lookupScripts = ConcurrentHashMap<SquirrelStringLiteral, SquirrelScriptReference>()
	private val lookupGfx = ConcurrentHashMap<SquirrelStringLiteral, SquirrelGfxReference>()

	private val projectFiles = ConcurrentHashMap<String, Collection<VirtualFile>>()
	private val lookups = ConcurrentHashMap<SquirrelId, SquirrelFunctionDeclarationPsiReferenceBase>()

	@JvmStatic
	fun getReference(element: SquirrelStringLiteral): PsiReference? {
		val text = element.string.text.trim('"')
		val looksLikeFile = "/" in text && " " !in text
		if (!looksLikeFile)
			return null

		if (text.startsWith("ui/"))
			return lookupGfx[element] ?: element.containingFile.getFile(("gfx/$text").toNioPathOrNull())
				?.let { SquirrelGfxReference(it, element) }
				?.also {
					if (!lookupGfx.containsKey(element))
						lookupGfx.putIfAbsent(element, it)
				}

		if (text.startsWith("scripts"))
			return lookupScripts[element] ?: element.containingFile.getFile(("$text.nut").toNioPathOrNull())
				?.let { SquirrelScriptReference(it, element) }
				?.also {
					if (!lookupScripts.containsKey(element))
						lookupScripts.putIfAbsent(element, it)
				}

		return lookupScripts[element] ?: element.containingFile.getFile(("scripts/$text.nut").toNioPathOrNull())
			?.let { SquirrelScriptReference(it, element) }
			?.also {
				if (!lookupScripts.containsKey(element))
					lookupScripts.putIfAbsent(element, it)
			}
	}

	@JvmStatic
	fun getReference(element: SquirrelStdIdentifier): PsiReference? {
		return SquirrelStdIdReference(element)
	}

	@JvmStatic
	fun getNameIdentifier(element: SquirrelStdIdentifier): PsiElement? {
		return element.identifier.node?.psi
	}

	@JvmStatic
	fun getName(element: SquirrelStdIdentifier?): String? {
		return element?.identifier?.node?.text
	}

	@JvmStatic
	fun setName(element: SquirrelStdIdentifier, newName: String): PsiElement {
		return element // todo, i have no damn clue how to do that, calling element.setName causes recursion here

	}

	@JvmStatic
	fun getReference(element: SquirrelId): PsiReference? {
		LOG.warn("getReference() called with: element = ${element.containingFile.name}")
		try {
			val inTestSourceContent = ProjectRootManager.getInstance(element.project).fileIndex.isInProject(element.containingFile.virtualFile)
			val projectFilePath = Objects.requireNonNull(element.project.projectFilePath) + inTestSourceContent

			if (!projectFiles.containsKey(projectFilePath)) {
				val containingFiles = FileBasedIndex.getInstance().getContainingFiles(
					FileTypeIndex.NAME, SquirrelFileType.INSTANCE, GlobalSearchScope.moduleRuntimeScope(ModuleUtil.findModuleForPsiElement(element)!!, inTestSourceContent)
				)

				projectFiles.putIfAbsent(projectFilePath, containingFiles)
			}

			if (!lookups.containsKey(element)) {
				lookups.putIfAbsent(element, SquirrelFunctionDeclarationPsiReferenceBase(element, projectFiles[projectFilePath]!!))
			}
			return lookups[element]
		} catch (e: Exception) {
			return null
		}
	}

	class SquirrelFilePsiReferenceBase internal constructor(psiElement: PsiElement, private val reference: PsiFile) : PsiPolyVariantReferenceBase<PsiElement?>(psiElement) {
		override fun getVariants(): Array<Any> {
			return emptyArray()
		}

		override fun multiResolve(b: Boolean): Array<ResolveResult> {
			return arrayOf(PsiElementResolveResult(reference))
		}
	}

	class SquirrelFunctionDeclarationPsiReferenceBase internal constructor(element: PsiElement, private val allFilesInProject: Collection<VirtualFile>) : PsiPolyVariantReferenceBase<PsiElement?>(element) {
		private val cached = AtomicReference<Array<ResolveResult>>()

		override fun getVariants(): Array<Any> {
			return emptyArray()
		}

		override fun multiResolve(b: Boolean): Array<ResolveResult> {
			var result = cached.get()
			if (result == null) {
				result = lookup()
				if (!cached.compareAndSet(null, result)) {
					return cached.get()
				}
			}
			return result
		}

		private fun lookup(): Array<ResolveResult> {
			val result: MutableList<ResolveResult> = ArrayList()

			variableSearch(result)
			paramSearch(result)
			if (result.isEmpty()) searchInFile(result, myElement!!.containingFile.virtualFile)

			if (result.isEmpty()) for (vf in allFilesInProject) {
				searchInFile(result, vf)
			}

			return result.toTypedArray<ResolveResult>()
		}

		private fun variableSearch(result: MutableList<ResolveResult>) {
			val id = myElement!!.text

			var variable: SquirrelVarItem? = null
			val variables = PsiTreeUtil.findChildrenOfType(myElement!!.containingFile, SquirrelVarItem::class.java)
			for (variableDeclaration in variables) {
				val v = variableDeclaration.id.stdIdentifier
				if (id == v?.text) {
					val parentFunction = PsiTreeUtil.findFirstParent(v) { psiElement -> psiElement is SquirrelFunctionDeclaration || psiElement is SquirrelMethodDeclaration }

					if (PsiTreeUtil.isAncestor(parentFunction, myElement!!, true)) variable = variableDeclaration
				}
			}

			if (variable != null) result.add(PsiElementResolveResult(variable))
		}

		private fun paramSearch(result: MutableList<ResolveResult>) {
			val id = myElement!!.text
			val parameterDeclarations = PsiTreeUtil.findChildrenOfType(myElement!!.containingFile, SquirrelParameter::class.java)
			for (param in parameterDeclarations) {
				val id1 = param.id

				if (id == id1.text && PsiTreeUtil.isAncestor(param.parent.parent.parent, myElement!!, false)) {
					result.add(PsiElementResolveResult(param))
				}
			}
		}

		private fun searchInFile(result: MutableList<ResolveResult>, vf: VirtualFile) {
			try {
				val squirrelFile = PsiManager.getInstance(myElement!!.project).findFile(vf) as SquirrelFile?
				if (squirrelFile != null) {
					try {
						val id = myElement!!.text

						if (result.isEmpty()) {
							val variables = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelVarItem::class.java)
							for (methodDeclaration in variables) {
								val variable = methodDeclaration.id.stdIdentifier
								//only top level locals only
								if (id == variable?.text) {
									val localDeclaration = PsiTreeUtil.getParentOfType(variable, SquirrelLocalDeclaration::class.java)
									if (localDeclaration != null && localDeclaration.parent is SquirrelFile) result.add(PsiElementResolveResult(variable!!))
								}
							}
						}

						if (result.isEmpty()) {
							val functionDeclarations = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelFunctionDeclaration::class.java)
							for (functionDeclaration in functionDeclarations) {
								val functionName1 = functionDeclaration.functionName
								if (functionName1 != null) {
									if (id == functionName1.text) {
										result.add(PsiElementResolveResult(functionName1))
									}
								}
							}
						}

						if (result.isEmpty()) {
							val methodDeclarations = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelMethodDeclaration::class.java)
							for (methodDeclaration in methodDeclarations) {
								val methodName1 = methodDeclaration.functionName
								if (id == methodName1.text) {
									result.add(PsiElementResolveResult(methodName1))
								}
							}
						}

						if (result.isEmpty()) {
							val className = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelConstDeclaration::class.java)
							for (constDeclaration in className) {
								val idList = constDeclaration.id
								if (idList != null && id == idList.text) {
									result.add(PsiElementResolveResult(idList))
								}
							}
						}
						if (result.isEmpty()) {
							val className = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelEnumItem::class.java)
							for (enumDeclaration in className) {
								val idList = enumDeclaration.id
								if (id == idList.text) {
									result.add(PsiElementResolveResult(idList))
								}
							}
						}
						if (result.isEmpty()) {
							val variables = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelClassMember::class.java)
							for (member in variables) {
								val variable: PsiElement? = member.key
								if (variable != null) {
									if (id == variable.text) {
										result.add(PsiElementResolveResult(variable))
									}
								}
							}
						}

						if (result.isEmpty()) {
							val className = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelClassDeclaration::class.java)
							for (classDeclaration in className) {
								val idList = classDeclaration.classNameList[0]
								if (id == idList.text) {
									result.add(PsiElementResolveResult(idList))
								}
							}
						}
					} catch (e: Exception) {
						e.printStackTrace()
					}
				}
			} catch (e: Exception) {
				e.printStackTrace()
				throw e
			}
		}
	}
}
