package com.buildServer.kobalt.agent

import com.intellij.openapi.util.SystemInfo
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.AgentRuntimeProperties
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.JavaRunnerUtil
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine
import jetbrains.buildServer.runner.JavaRunnerConstants.JAVA_HOME
import jetbrains.buildServer.runner.JavaRunnerConstants.TARGET_JDK_HOME
import jetbrains.buildServer.util.FileUtil
import java.io.File
import java.util.Collections.singletonList

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */
internal open class KobaltRunnerService : BuildServiceAdapter() {
    override fun makeProgramCommandLine(): ProgramCommandLine {
        val env = mutableMapOf<String, String>()
        val params = mutableListOf<String>()
        val distributionDownloader = KobaltDistributionDownloader(logger, runnerParameters.getProxy())
        val kobaltVersion = distributionDownloader.latestKobaltVersionOrDefault()
        try {
            distributionDownloader.installIfNeeded(kobaltVersion, {}, {})
        } catch(e: DistributionDownloaderException) {
            throw RunBuildException(e.message)
        }
        env += environmentVariables
        env[JAVA_HOME] = getJavaHome()
        val kobaltExecAbsolutePath = KobaltPathUtils.kobaltExecutablePath(kobaltVersion).toFile().absolutePath
        val exePath = if (SystemInfo.isUnix) {
            params.add(kobaltExecAbsolutePath)
            "bash"
        } else {
            kobaltExecAbsolutePath
        }
        val jvmArgs = runnerParameters.getJVMArgs().apply { if(isNotEmpty()) this + " "}
        params.add("$jvmArgs--buildFile ${runnerParameters.getPathToBuildFile()} ${runnerParameters.getKobaltTasks()}")
        return SimpleProgramCommandLine(env, workingDirectory.path, exePath, params)
    }

    override fun getListeners() = singletonList(KobaltLoggingListener(logger))

    private fun getJavaHome()
            = JavaRunnerUtil.findJavaHome(runnerParameters[TARGET_JDK_HOME],
            buildParameters.allParameters, AgentRuntimeProperties.getCheckoutDir(runnerParameters))
            ?.let { javaHome -> FileUtil.getCanonicalFile(File(javaHome)).path }
            ?: throw RunBuildException("Unable to find Java home")

}