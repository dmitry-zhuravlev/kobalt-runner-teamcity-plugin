package com.buildServer.kobalt.agent

import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.agent.runner.ProcessListenerAdapter
import jetbrains.buildServer.messages.DefaultMessagesInfo

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */
internal class KobaltLoggingListener(val buildLogger: BuildProgressLogger) : ProcessListenerAdapter() {

    val errorMessages = mutableListOf<String>()

    override fun onStandardOutput(text: String) {
        buildLogger.message(text)
    }

    override fun onErrorOutput(text: String) {
        errorMessages += text
    }

    override fun processFinished(exitCode: Int) =
            if (exitCode != 0 && errorMessages.isNotEmpty()) {
                buildLogger.activityStarted("Kobalt failure", DefaultMessagesInfo.BLOCK_TYPE_TARGET)
                flushErrorMessages()
                buildLogger.activityFinished("Kobalt failure", DefaultMessagesInfo.BLOCK_TYPE_TARGET)
            } else {
                flushErrorMessages()
            }


    private fun flushErrorMessages() = with(errorMessages) {
        for (errorMessage in this) {
            buildLogger.warning(errorMessage)
        }
        errorMessages.clear()
    }

}