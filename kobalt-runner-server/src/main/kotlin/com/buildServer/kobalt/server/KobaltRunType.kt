package com.buildServer.kobalt.server

import com.buildServer.kobalt.common.KobaltRunnerConstants
import com.buildServer.kobalt.common.KobaltRunnerConstants.PATH_TO_BUILD_FILE
import com.buildServer.kobalt.common.KobaltRunnerConstants.RUNNER_TYPE
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.web.openapi.PluginDescriptor

/**
 * @author Dmitry Zhuravlev
 *         Date:  07.10.2016
 */
open class KobaltRunType(runTypeRegistry: RunTypeRegistry, val pluginDescriptor: PluginDescriptor) : RunType() {

    init {
        runTypeRegistry.registerRunType(this)
    }

    override fun getType() = RUNNER_TYPE

    override fun getDisplayName() = "Kobalt"

    override fun getDescription() = "Runner for Kobalt projects"

    override fun getEditRunnerParamsJspFilePath() = "${pluginDescriptor.pluginResourcesPath}/editKobaltRunnerRunParams.jsp"

    override fun getViewRunnerParamsJspFilePath() = "${pluginDescriptor.pluginResourcesPath}/viewKobaltRunnerRunParams.jsp"

    override fun getRunnerPropertiesProcessor() = null

    override fun getDefaultRunnerProperties() = mutableMapOf(PATH_TO_BUILD_FILE to KobaltRunnerConstants.DEFAULT_KOBALT_BUILD_FILE_LOCATION)
}

