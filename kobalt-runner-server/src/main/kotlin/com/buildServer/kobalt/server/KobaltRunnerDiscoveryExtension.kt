package com.buildServer.kobalt.server

import com.buildServer.kobalt.common.KobaltPathUtils.KOBALT_BUILD_FILE_NAME
import com.buildServer.kobalt.common.KobaltPathUtils.KOBALT_DIR
import com.buildServer.kobalt.common.KobaltPathUtils.KOBALT_WRAPPER_DIR
import com.buildServer.kobalt.common.KobaltPathUtils.UNIX_KOBALT_WRAPPER_NAME
import com.buildServer.kobalt.common.KobaltPathUtils.WIN_KOBALT_WRAPPER_NAME
import com.buildServer.kobalt.common.KobaltRunnerConstants
import com.buildServer.kobalt.common.KobaltRunnerConstants.DEFAULT_KOBALT_BUILD_TASKS
import com.buildServer.kobalt.common.KobaltRunnerConstants.KOBALT_TASKS
import com.buildServer.kobalt.common.KobaltRunnerConstants.PATH_TO_BUILD_FILE
import com.buildServer.kobalt.common.KobaltRunnerConstants.USE_KOBALT_WRAPPER
import jetbrains.buildServer.serverSide.BuildTypeSettings
import jetbrains.buildServer.serverSide.discovery.BreadthFirstRunnerDiscoveryExtension
import jetbrains.buildServer.serverSide.discovery.DiscoveredObject
import jetbrains.buildServer.util.browser.Browser
import jetbrains.buildServer.util.browser.Element

/**
 * @author Dmitry Zhuravlev
 *         Date:  11.10.2016
 */
open internal class KobaltRunnerDiscoveryExtension : BreadthFirstRunnerDiscoveryExtension() {
    override fun discoverRunnersInDirectory(dir: Element, filesAndDirs: MutableList<Element>) = with(mutableListOf<DiscoveredObject>()) {
        for (elem in filesAndDirs) {
            if (!elem.isLeaf && elem.name == KOBALT_DIR) {
                val parameters = mutableMapOf<String, String>()
                elem.subElement("src").subElement(KOBALT_BUILD_FILE_NAME)?.let { buildFileElement ->
                    parameters[PATH_TO_BUILD_FILE] = buildFileElement.fullName
                    parameters[KOBALT_TASKS] = DEFAULT_KOBALT_BUILD_TASKS

                }
                if (dir.findWrapper() != null && elem.subElement(KOBALT_WRAPPER_DIR) != null) {
                    parameters[USE_KOBALT_WRAPPER] = true.toString()
                }
                add(DiscoveredObject(KobaltRunnerConstants.RUNNER_TYPE, parameters))
            }
        }
        this
    }

    override fun postProcessDiscoveredObjects(settings: BuildTypeSettings, browser: Browser, discovered: MutableList<DiscoveredObject>): MutableList<DiscoveredObject> {
        val discoveredBuildFiles = hashSetOf<String>()
        for (descriptor in settings.buildRunners) {
            if (KobaltRunnerConstants.RUNNER_TYPE == descriptor.type) {
                discoveredBuildFiles.add(descriptor.parameters[PATH_TO_BUILD_FILE].orEmpty())
            }
        }
        val iterator = discovered.iterator()
        while (iterator.hasNext()) {
            val parameters = iterator.next().parameters
            if (discoveredBuildFiles.contains(parameters[PATH_TO_BUILD_FILE].orEmpty())) {
                iterator.remove()
            }
        }
        return discovered
    }

    private fun Element.findWrapper() =
            subElement(UNIX_KOBALT_WRAPPER_NAME) ?: subElement(WIN_KOBALT_WRAPPER_NAME)

    private fun Element?.subElement(elemNameToFind: String): Element?
            = (this?.children?.iterator() ?: null)
            ?.forEach { elem -> if (elem.name == elemNameToFind) return@subElement elem }
            .run { return null }

}