package com.buildServer.kobalt.agent

import com.buildServer.kobalt.common.KobaltPathUtils
import com.buildServer.kobalt.common.KobaltVersionManager
import com.buildServer.kobalt.common.http.ProxyLocator
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
        try {
            if (!runnerParameters.useKobaltWrapper()) {
                val proxy = ProxyLocator.findAgentProxyConfiguration() ?: runnerParameters.getProxy()
                val distributionDownloader = KobaltDistributionDownloader(logger, proxy)
                val kobaltVersion = runnerParameters.useKobaltVersion().let { version ->
                    if (version.isEmpty()) KobaltVersionManager(proxy).latestKobaltVersionOrDefault()
                    else version
                }
                distributionDownloader.installIfNeeded(kobaltVersion, {}, {})
                return makeJarExecutionCommandLine()
            } else {
                return makeWrapperExecutionCommandLine()
            }
        } catch(e: DistributionDownloaderException) {
            throw RunBuildException(e.message)
        }
    }

    private fun makeJarExecutionCommandLine(): SimpleProgramCommandLine {
        val env = mutableMapOf<String, String>()
        val params = mutableListOf<String>()
        val proxy = ProxyLocator.findAgentProxyConfiguration() ?: runnerParameters.getProxy()
        val kobaltVersion = runnerParameters.useKobaltVersion().let { version ->
            if (version.isEmpty()) KobaltVersionManager(proxy).latestKobaltVersionOrDefault() else version
        }
        val kobaltJarAbsolutePath = KobaltPathUtils.kobaltJarPath(kobaltVersion).toFile().absolutePath
        val jvmArgs = runnerParameters.getJVMArgs()

        env += environmentVariables
        env[JAVA_HOME] = getJavaHome()

        val exePath = getJavaExecutable()

        params.addAll(jvmArgs)
        params += "-jar"
        params += kobaltJarAbsolutePath
        params += "--buildFile"
        params += runnerParameters.getPathToBuildFile()
        runnerParameters.getKobaltTasks().forEach { task -> params += task }
        return SimpleProgramCommandLine(env, workingDirectory.path, exePath, params)
    }

    private fun makeWrapperExecutionCommandLine(): SimpleProgramCommandLine {
        val env = mutableMapOf<String, String>()
        val params = mutableListOf<String>()
        val kobaltWrapperAbsolutePath = File(workingDirectory, KobaltPathUtils.kobaltWrapperName).absolutePath
        val jvmArgs = runnerParameters.getJVMArgs()

        env += environmentVariables
        env[JAVA_HOME] = getJavaHome()

        val exePath = kobaltWrapperAbsolutePath

        params.addAll(jvmArgs)
        params += "--buildFile"
        params += runnerParameters.getPathToBuildFile()
        runnerParameters.getKobaltTasks().forEach { task -> params += task }
        return SimpleProgramCommandLine(env, workingDirectory.path, exePath, params)
    }

    override fun getListeners() = singletonList(KobaltLoggingListener(logger))

    private fun getJavaHome()
            = JavaRunnerUtil.findJavaHome(runnerParameters[TARGET_JDK_HOME],
            buildParameters.allParameters, AgentRuntimeProperties.getCheckoutDir(runnerParameters))
            ?.let { javaHome -> FileUtil.getCanonicalFile(File(javaHome)).path }
            ?: throw RunBuildException("Unable to find Java home")

    private fun getJavaExecutable()
            = JavaRunnerUtil.findJavaExecutable(runnerParameters[TARGET_JDK_HOME],
            buildParameters.allParameters, AgentRuntimeProperties.getCheckoutDir(runnerParameters))
            ?.let { javaExe -> FileUtil.getCanonicalFile(File(javaExe)).path }
            ?: throw RunBuildException("Unable to find Java executable")

}