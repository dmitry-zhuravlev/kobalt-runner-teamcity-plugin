package com.buildServer.kobalt.agent

import com.buildServer.kobalt.common.KobaltRunnerConstants
import com.intellij.openapi.util.SystemInfo
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.log.Loggers

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */
internal open class KobaltRunnerServiceFactory : CommandLineBuildServiceFactory {

    override fun createService() = KobaltRunnerService()

    override fun getBuildRunnerInfo() = info

    private val info = object : AgentBuildRunnerInfo {
        val isOSSupported = SystemInfo.isWindows || SystemInfo.isUnix

        init {
            if (!isOSSupported) {
                Loggers.AGENT.warn("Kobalt runner plugin does not support current OS. Kobalt build runner will not be available.")
            }
        }

        override fun canRun(agentConfiguration: BuildAgentConfiguration) = isOSSupported

        override fun getType() = KobaltRunnerConstants.RUNNER_TYPE
    }

}

