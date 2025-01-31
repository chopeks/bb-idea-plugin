package com.chopeks.sdk

import com.chopeks.util.SquirrelConstants
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionHelper
import com.intellij.execution.ExecutionModes.ModalProgressMode
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.PathUtil
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import kotlin.math.max

object SquirrelSdkUtil {
	private val SQUIRREL_VERSION_PATTERN: Pattern = Pattern.compile(
		"#define SQUIRREL_VERSION[\t ]+_SC\\" +
				"(\"Squirrel ([0-9\\.]*?) stable\"\\)"
	)

	// todo test
	@JvmStatic
	fun suggestSdkDirectory(): VirtualFile? {
		if (SystemInfo.isMac || SystemInfo.isLinux) {
			val fromEnv = suggestSdkDirectoryPathFromEnv()
			if (fromEnv != null) {
				return LocalFileSystem.getInstance().findFileByPath(fromEnv)
			}
			val usrLocal = LocalFileSystem.getInstance().findFileByPath("/usr/local/squirrel")
			if (usrLocal != null) return usrLocal
		}
		if (SystemInfo.isMac) {
			val macPorts = "/opt/local/lib/squirrel"
			val homeBrew = "/usr/local/Cellar/squirrel"
			val file = FileUtil.findFirstThatExist(macPorts, homeBrew)
			if (file != null) {
				return LocalFileSystem.getInstance().findFileByIoFile(file)
			}
		}
		return null
	}

	private fun suggestSdkDirectoryPathFromEnv(): String? {
		val fileFromPath = PathEnvironmentVariableUtil.findInPath(SquirrelConstants.SQUIRREL_COMPILER_NAME)
		if (fileFromPath != null) {
			val canonicalFile: File
			try {
				canonicalFile = fileFromPath.canonicalFile
				val path = canonicalFile.path
				if (path.endsWith("bin/" + SquirrelConstants.SQUIRREL_COMPILER_NAME)) {
					return StringUtil.trimEnd(path, "bin/" + SquirrelConstants.SQUIRREL_COMPILER_NAME)
				}
			} catch (ignore: IOException) {
			}
		}
		return null
	}


	// todo test
	@JvmStatic
	fun retrieveSquirrelVersion(sdkPath: String): String? {
		try {
			val versionFilePath = File(sdkPath, SquirrelConstants.SQUIRREL_VERSION_FILE_PATH)
			if (!versionFilePath.exists()) {
				SquirrelSdkService.LOG.debug("Cannot find 'include/squirrel.h' file at sdk path: $sdkPath")
				return null
			}
			val file = FileUtil.loadFile(versionFilePath)
			val version = parseSquirrelVersion(file)
			if (version == null) {
				SquirrelSdkService.LOG.debug("Cannot retrieve squirrel version from 'include/squirrel.h' file: $file")
			}
			return version
		} catch (e: IOException) {
			SquirrelSdkService.LOG.debug("Cannot retrieve squirrel version from sdk path: $sdkPath", e)
			return null
		}
	}

	fun parseSquirrelVersion(text: String): String? {
		val matcher = SQUIRREL_VERSION_PATTERN.matcher(text)
		if (matcher.find()) {
			return matcher.group(1)
		}
		return null
	}

	/**
	 * Check if we've got the directory with list of version directories. If so, grab the last one which have a bin
	 * in it.
	 */
	@JvmStatic
	fun adjustSdkPath(path: String): String {
		var path = path
		val versions: MutableList<Version> = ArrayList()
		var binDirectory = File(path, "bin")

		if (!binDirectory.exists() && path.endsWith("bin/" + SquirrelConstants.SQUIRREL_COMPILER_NAME)) {
			path = StringUtil.trimEnd(path, "bin/" + SquirrelConstants.SQUIRREL_COMPILER_NAME)
		}

		if (!binDirectory.exists() && path.endsWith("bin")) {
			path = StringUtil.trimEnd(path, "bin")
		}

		if (!binDirectory.exists()) {
			val files = File(path).listFiles()
			for (file in files ?: arrayOfNulls<File>(0)) {
				if (file.isDirectory) {
					val versionDir = file.absolutePath
					binDirectory = File(versionDir, "bin")
					if (binDirectory.exists()) {
						versions.add(Version(file.name))
					}
				}
			}
			if (versions.size > 0) {
				Collections.sort(versions, Collections.reverseOrder())
				return path + '/' + versions[0].get()
			}
		}

		return path
	}

	val makeExecutable: String?
		get() {
			val make = PathEnvironmentVariableUtil.findInPath("make") ?: return null
			return make.absolutePath
		}

	val compilerName: String
		get() {
			val resultBinaryName = FileUtil.getNameWithoutExtension(PathUtil.getFileName(SquirrelConstants.SQUIRREL_COMPILER_NAME))
			return if (SystemInfo.isWindows) "$resultBinaryName.exe" else resultBinaryName
		}

	@JvmStatic
	fun getCompilerPath(sdkHomePath: String): String {
		val compilerName = compilerName
		return FileUtil.join(sdkHomePath, "bin", compilerName)
	}

	@JvmStatic
	fun sourcesExist(sdkHomePath: String?): Boolean {
		return File(sdkHomePath, "include/squirrel.h").exists()
	}

	@JvmStatic
	fun canMake(): Boolean {
		val make = makeExecutable
		return make != null
	}

	@JvmStatic
	fun binDirExist(sdkHomePath: String?): Boolean {
		return File(sdkHomePath, "bin").exists()
	}

	@JvmStatic
	fun compilerExist(sdkHomePath: String): Boolean {
		val compilerName = compilerName
		val compiler = FileUtil.join(sdkHomePath, "bin", compilerName)
		return File(compiler).exists()
	}


	@JvmStatic
	fun getSdkDirectoriesToAttach(sdkPath: String, versionString: String): Collection<VirtualFile> {
		// At this point, we only add a root path.
		val srcPath = ""
		val src = VirtualFileManager.getInstance().findFileByUrl(VfsUtilCore.pathToUrl(FileUtil.join(sdkPath, srcPath)))
		if (src != null && src.isDirectory) {
			return listOf(src)
		}
		return emptyList()
	}

	@JvmStatic
	fun makeSquirrelCompiler(sdkPath: String) {
		try {
			if (retrieveSquirrelVersion(sdkPath) == null) {
				throw ExecutionException("Failed to make squirrel compiler, no sources present at the path.")
			}

			val make = makeExecutable ?: throw ExecutionException("Failed to found make executable.")

			val cmd = GeneralCommandLine()
			cmd.exePath = make
			cmd.withWorkDirectory(sdkPath)
			cmd.parametersList.addParametersString("-C $sdkPath")

			val handler = KillableColoredProcessHandler(cmd)
			handler.setShouldDestroyProcessRecursively(true)
			ProcessTerminatedListener.attach(handler)
			handler.startNotify()

			val project = ProjectManager.getInstance().defaultProject
			ExecutionHelper.executeExternalProcess(project, handler, ModalProgressMode("Making a squirrel compiler..."), cmd)
		} catch (e: ExecutionException) {
			SquirrelSdkService.LOG.debug(e.message)
		}
	}

	class Version(version: String) : Comparable<Version?> {
		private val version: String

		fun get(): String {
			return this.version
		}

		init {
			requireNotNull(version) { "Version can not be null" }
			require(version.matches("[0-9]+(\\.[0-9]+)*".toRegex())) { "Invalid version format" }
			this.version = version
		}

		override fun compareTo(that: Version?): Int {
			if (that == null) return 1
			val thisParts = get().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			val thatParts = that.get().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			val length = max(thisParts.size.toDouble(), thatParts.size.toDouble()).toInt()
			for (i in 0..<length) {
				val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
				val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
				if (thisPart < thatPart) return -1
				if (thisPart > thatPart) return 1
			}
			return 0
		}

		override fun equals(that: Any?): Boolean {
			if (this === that) return true
			if (that == null) return false
			if (this.javaClass != that.javaClass) return false
			return this.compareTo(that as Version) == 0
		}
	}
}
