package com.buildServer.kobalt.agent

import com.buildServer.kobalt.common.KobaltRunnerConstants
import jetbrains.buildServer.runner.JavaRunnerConstants
import jetbrains.buildServer.util.StringUtil.emptyIfNull
import jetbrains.buildServer.util.StringUtil.newLineToSpaceDelimited

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */

fun Map<String, String>.isParameterEnabled(key: String) = containsKey(key) && get(key) == java.lang.Boolean.TRUE.toString()

fun Map<String, String>.getJVMArgs() = newLineToSpaceDelimited(emptyIfNull(get(JavaRunnerConstants.JVM_ARGS_KEY)))

fun Map<String, String>.getPathToBuildFile() = get(KobaltRunnerConstants.PATH_TO_BUILD_FILE) ?: KobaltRunnerConstants.DEFAULT_KOBALT_BUILD_FILE_LOCATION

fun Map<String, String>.getKobaltTasks() = emptyIfNull(get(KobaltRunnerConstants.KOBALT_TASKS))