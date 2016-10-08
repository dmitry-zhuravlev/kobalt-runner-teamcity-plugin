package com.buildServer.kobalt.agent

import com.intellij.openapi.util.SystemInfo.isUnix
import com.intellij.openapi.util.SystemInfo.isWindows
import jetbrains.buildServer.util.FileUtil
import java.io.File
import java.nio.file.Paths

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */
internal object KobaltPathUtils {

    const val KOBALT_DOT_DIR = ".kobalt"
    const val KOBALT_DIR = "kobalt"
    const val WIN_KOBALT_BIN = "bin/kobaltw.bat"
    const val UNIX_KOBALT_BIN = "bin/kobaltw"

    /** Where all the .zip files are extracted */
    val kobaltDistributionsDir = homeDir(KOBALT_DOT_DIR, "wrapper", "dist")

    fun kobaltExecutablePath(version: String) = Paths.get(FileUtil.toSystemIndependentName(homeDir(KOBALT_DOT_DIR, "wrapper", "dist", "kobalt-$version",
            if (isWindows) WIN_KOBALT_BIN else if (isUnix) UNIX_KOBALT_BIN else throw RuntimeException("OS not supported"))))

    fun kobaltHomeDir(version: String) = FileUtil.toSystemIndependentName(homeDir(KOBALT_DOT_DIR, "wrapper", "dist", "kobalt-$version"))

    fun homeDir(vararg dirs: String): String = System.getProperty("user.home") +
            File.separator + dirs.toMutableList().joinToString(File.separator)

}