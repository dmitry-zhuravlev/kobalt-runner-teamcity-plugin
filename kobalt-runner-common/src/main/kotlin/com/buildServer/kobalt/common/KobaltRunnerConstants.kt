package com.buildServer.kobalt.common

/**
 * @author Dmitry Zhuravlev
 *         Date:  07.10.2016
 */
object KobaltRunnerConstants {
    const val MIN_KOBALT_VERSION = "0.864"

    const val RUNNER_TYPE = "kobalt-runner"
    const val KOBALT_MAIN_CLASS = "com.beust.kobalt.MainKt"
    const val DEFAULT_KOBALT_BUILD_TASKS = "clean assemble"
    const val PATH_TO_BUILD_FILE = "ui.kobalt.runner.build.file.path"
    const val KOBALT_TASKS = "ui.kobalt.runner.kobalt.tasks"
    const val KOBALT_CMD_PARAMS = "ui.kobalt.additional.kobalt.cmd.params"
    const val KOBALT_SETTINGS = "ui.kobalt.runner.kobalt.settings"
    const val USE_KOBALT_VERSION = "ui.kobalt.runner.version.useVersion"
    const val USE_KOBALT_WRAPPER = "ui.kobalt.runner.wrapper.useWrapper"
}