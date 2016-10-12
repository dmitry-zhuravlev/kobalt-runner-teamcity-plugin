package com.buildServer.kobalt.agent

import com.buildServer.kobalt.common.KobaltPathUtils
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
        val useKobaltWrapper = runnerParameters.useKobaltWrapper()
        val kobaltWrapperAbsolutePath = KobaltPathUtils.kobaltWrapperPath(kobaltVersion).toFile().absolutePath
        val kobaltJarAbsolutePath = KobaltPathUtils.kobaltJarPath(kobaltVersion).toFile().absolutePath
        val jvmArgs = runnerParameters.getJVMArgs()

        try {
            if (!useKobaltWrapper) distributionDownloader.installIfNeeded(kobaltVersion, {}, {})
        } catch(e: DistributionDownloaderException) {
            throw RunBuildException(e.message)
        }
        env += environmentVariables
        val javaHome = getJavaHome()
        env[JAVA_HOME] = javaHome
        var exePath = if (SystemInfo.isUnix) "bash " else ""
        if (useKobaltWrapper) exePath += kobaltWrapperAbsolutePath else exePath += getJavaExecutable()
        params.addAll(jvmArgs)
        if (!useKobaltWrapper) {
            params += "-jar"
            params += kobaltJarAbsolutePath
        }
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