package com.buildServer.kobalt.agent

import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.ToolProvider


/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */
internal open class KobaltToolProvider : ToolProvider {
    override fun getPath(toolName: String): String {
        //TODO()
        return ""
    }

    override fun getPath(toolName: String,
                         build: AgentRunningBuild,
                         runner: BuildRunnerContext): String {
        //TODO()
        return ""
    }

    override fun supports(toolName: String) = toolName == "kobalt"
}