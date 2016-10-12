package com.buildServer.kobalt.agent

import com.buildServer.kobalt.common.KobaltPathUtils
import com.buildServer.kobalt.common.KobaltRunnerConstants
import jetbrains.buildServer.agent.runner.JavaRunnerUtil
import jetbrains.buildServer.runner.JavaRunnerConstants
import jetbrains.buildServer.util.StringUtil.newLineToSpaceDelimited
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */

fun Map<String, String>.isParameterEnabled(key: String) = containsKey(key) && get(key) == java.lang.Boolean.TRUE.toString()

fun Map<String, String>.getJVMArgs() = getJVMArgsString().let {argsStr->
    val fileEncoding = "-Dfile.encoding=UTF-8"
    if (argsStr.isNotEmpty()) argsStr.split(" ") + fileEncoding else listOf(fileEncoding)
}

fun Map<String, String>.getJVMArgsString() = newLineToSpaceDelimited(get(JavaRunnerConstants.JVM_ARGS_KEY).orEmpty())


fun Map<String, String>.getProxy() = JavaRunnerUtil.extractJvmArgs(get(JavaRunnerConstants.JVM_ARGS_KEY).orEmpty())
        .fold(ProxyParams()) { proxyParams, param ->
            if (param.startsWith("-Dhttp.proxyHost") || param.startsWith("-Dhttps.proxyHost")) proxyParams.host = param.split("=")[1]
            if (param.startsWith("-Dhttp.proxyPort") || param.startsWith("-Dhttp.proxyPort")) proxyParams.port = param.split("=")[1].toInt()
            proxyParams
        }
        .let { proxyParams ->
            val host = proxyParams.host
            val port = proxyParams.port
            if (host != null && port != null) {
                Proxy(Proxy.Type.HTTP, InetSocketAddress(host, port))
            } else null
        }

fun Map<String, String>.getPathToBuildFile() = get(KobaltRunnerConstants.PATH_TO_BUILD_FILE) ?: KobaltPathUtils.DEFAULT_KOBALT_BUILD_FILE_LOCATION
fun Map<String, String>.getKobaltTasks() = get(KobaltRunnerConstants.KOBALT_TASKS).orEmpty().split(" ")
fun Map<String, String>.useKobaltWrapper() = get(KobaltRunnerConstants.USE_KOBALT_WRAPPER)?.toBoolean() ?: false

private class ProxyParams {
    var host: String? = null
    var port: Int? = null
}