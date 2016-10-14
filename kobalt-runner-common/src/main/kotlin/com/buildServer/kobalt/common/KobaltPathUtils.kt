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
    const val KOBALT_CONFIG_DIR = ".config"
    const val KOBALT_SETTINGS_FILE_NAME = "settings.xml"
    const val KOBALT_DISTRIBUTION_DIR = "dist"
    const val KOBALT_BIN_DIR = "bin"
    const val KOBALT_WRAPPER_DIR = "wrapper"
    const val WIN_KOBALT_WRAPPER_NAME = "kobaltw.bat"
    const val UNIX_KOBALT_WRAPPER_NAME = "kobaltw"

    val kobaltWrapperName by lazy { if (isWindows) WIN_KOBALT_WRAPPER_NAME else if (isUnix) UNIX_KOBALT_WRAPPER_NAME else throw RuntimeException("OS not supported") }
    val kobaltDistributionsDir = homeDir(KOBALT_DOT_DIR, "wrapper", "dist")

    fun kobaltSettingsPath() = Paths.get(FileUtil.toSystemIndependentName(homeDir(KOBALT_CONFIG_DIR, KOBALT_DIR, KOBALT_SETTINGS_FILE_NAME)))
    fun kobaltWrapperPath(version: String) = Paths.get(FileUtil.toSystemIndependentName(homeDir(KOBALT_DOT_DIR, KOBALT_WRAPPER_DIR, KOBALT_DISTRIBUTION_DIR, "kobalt-$version", KOBALT_BIN_DIR, kobaltWrapperName)))
    fun kobaltJarPath(version: String) = Paths.get(FileUtil.toSystemIndependentName(homeDir(KOBALT_DOT_DIR, KOBALT_WRAPPER_DIR, KOBALT_DISTRIBUTION_DIR, "kobalt-$version", KOBALT_DIR, KOBALT_WRAPPER_DIR,"kobalt-$version.jar")))
    fun kobaltHomeDir(version: String) = FileUtil.toSystemIndependentName(homeDir(KOBALT_DOT_DIR, KOBALT_WRAPPER_DIR, KOBALT_DISTRIBUTION_DIR, "kobalt-$version"))
    fun homeDir(vararg dirs: String): String = System.getProperty("user.home") +
            File.separator + dirs.toMutableList().joinToString(File.separator)

}