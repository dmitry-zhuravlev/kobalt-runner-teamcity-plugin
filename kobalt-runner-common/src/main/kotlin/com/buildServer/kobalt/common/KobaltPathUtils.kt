package com.buildServer.kobalt.common

import com.intellij.openapi.util.SystemInfo.isUnix
import com.intellij.openapi.util.SystemInfo.isWindows
import jetbrains.buildServer.util.FileUtil
import java.io.File
import java.nio.file.Paths

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */
object KobaltPathUtils {

    const val KOBALT_BUILD_FILE_NAME = "Build.kt"
    const val DEFAULT_KOBALT_BUILD_FILE_LOCATION = "kobalt/src/$KOBALT_BUILD_FILE_NAME"
    const val KOBALT_DOT_DIR = ".kobalt"
    const val KOBALT_DIR = "kobalt"
    const val KOBALT_WRAPPER_DIR = "wrapper"
    const val WIN_KOBALT_WRAPPER_NAME = "kobaltw.bat"
    const val UNIX_KOBALT_WRAPPER_NAME = "kobaltw"

    val kobaltWrapperName by lazy { if (isWindows) WIN_KOBALT_WRAPPER_NAME else if (isUnix) UNIX_KOBALT_WRAPPER_NAME else throw RuntimeException("OS not supported") }
    val kobaltDistributionsDir = homeDir(KOBALT_DOT_DIR, "wrapper", "dist")

    fun kobaltWrapperPath(version: String) = Paths.get(FileUtil.toSystemIndependentName(homeDir(KOBALT_DOT_DIR, "wrapper", "dist", "kobalt-$version", "bin", kobaltWrapperName)))
    fun kobaltJarPath(version: String) = Paths.get(FileUtil.toSystemIndependentName(homeDir(KOBALT_DOT_DIR, "wrapper", "dist", "kobalt-$version", KOBALT_DIR, KOBALT_WRAPPER_DIR,"kobalt-$version.jar")))
    fun kobaltHomeDir(version: String) = FileUtil.toSystemIndependentName(homeDir(KOBALT_DOT_DIR, "wrapper", "dist", "kobalt-$version"))
    fun homeDir(vararg dirs: String): String = System.getProperty("user.home") +
            File.separator + dirs.toMutableList().joinToString(File.separator)

}