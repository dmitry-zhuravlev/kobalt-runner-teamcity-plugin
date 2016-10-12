package com.buildServer.kobalt.server

import com.buildServer.kobalt.common.KobaltPathUtils
import com.buildServer.kobalt.common.KobaltRunnerConstants
import com.buildServer.kobalt.common.KobaltRunnerConstants.KOBALT_TASKS
import com.buildServer.kobalt.common.KobaltRunnerConstants.PATH_TO_BUILD_FILE
import com.buildServer.kobalt.common.KobaltRunnerConstants.RUNNER_TYPE
import com.buildServer.kobalt.common.KobaltRunnerConstants.USE_KOBALT_WRAPPER
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.web.openapi.PluginDescriptor

/**
 * @author Dmitry Zhuravlev
 *         Date:  07.10.2016
 */
open internal class KobaltRunType(runTypeRegistry: RunTypeRegistry, val pluginDescriptor: PluginDescriptor) : RunType() {

    init {
        runTypeRegistry.registerRunType(this)
    }

    override fun getType() = RUNNER_TYPE

    override fun getDisplayName() = "Kobalt"

    override fun getDescription() = "Runner for Kobalt projects"

    override fun getEditRunnerParamsJspFilePath() = "${pluginDescriptor.pluginResourcesPath}/editKobaltRunnerRunParams.jsp"

    override fun getViewRunnerParamsJspFilePath() = "${pluginDescriptor.pluginResourcesPath}/viewKobaltRunnerRunParams.jsp"

    override fun getRunnerPropertiesProcessor() = null

    override fun getDefaultRunnerProperties() = mutableMapOf(
            PATH_TO_BUILD_FILE to KobaltPathUtils.DEFAULT_KOBALT_BUILD_FILE_LOCATION,
            KOBALT_TASKS to KobaltRunnerConstants.DEFAULT_KOBALT_BUILD_TASKS
    )

    override fun describeParameters(parameters: MutableMap<String, String>) = with(StringBuilder()) {
        val kobaltTasks = parameters[KOBALT_TASKS]
        val pathToBuildFile = parameters[PATH_TO_BUILD_FILE]
        val useKobaltWrapper = parameters[USE_KOBALT_WRAPPER]
        if (!kobaltTasks.isNullOrEmpty()) {
            append("Kobalt Tasks: $kobaltTasks\n")
        }
        if (!pathToBuildFile.isNullOrEmpty()) {
            append("Path to build file: $pathToBuildFile")
        }
        if (!useKobaltWrapper.isNullOrEmpty()) {
            append("Use kobalt wrapper: ${useKobaltWrapper!!.toBoolean()}")
        }
        toString()
    }
}

