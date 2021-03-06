package com.buildServer.kobalt.agent

import com.buildServer.kobalt.common.KobaltPathUtils
import com.buildServer.kobalt.common.KobaltRunnerConstants
import jetbrains.buildServer.runner.JavaRunnerConstants
import jetbrains.buildServer.util.StringUtil.newLineToSpaceDelimited

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */

fun Map<String, String>.isParameterEnabled(key: String) = containsKey(key) && get(key) == java.lang.Boolean.TRUE.toString()

fun Map<String, String>.getJVMArgs() = getJVMArgsString().let {argsStr->
    val fileEncoding = "-Dfile.encoding=${Charsets.UTF_8}"
    if (argsStr.isNotEmpty()) argsStr.split(" ") + fileEncoding else listOf(fileEncoding)
}

fun Map<String, String>.getJVMArgsString() = newLineToSpaceDelimited(get(JavaRunnerConstants.JVM_ARGS_KEY).orEmpty())

fun Map<String, String>.getPathToBuildFile() = get(KobaltRunnerConstants.PATH_TO_BUILD_FILE) ?: KobaltPathUtils.DEFAULT_KOBALT_BUILD_FILE_LOCATION

fun Map<String, String>.getKobaltTasks() = get(KobaltRunnerConstants.KOBALT_TASKS).orEmpty().split(" ")

fun Map<String, String>.getKobaltCmdParams() = get(KobaltRunnerConstants.KOBALT_CMD_PARAMS).orEmpty().split(" ")

fun Map<String, String>.getKobaltSettingsFileContent() = get(KobaltRunnerConstants.KOBALT_SETTINGS).orEmpty()

fun Map<String, String>.useKobaltWrapper() = get(KobaltRunnerConstants.USE_KOBALT_WRAPPER)?.toBoolean() ?: false

fun Map<String, String>.useKobaltVersion() = get(KobaltRunnerConstants.USE_KOBALT_VERSION).orEmpty()
