package com.buildServer.kobalt.server

import com.buildServer.kobalt.common.KobaltPathUtils.KOBALT_BUILD_FILE_NAME
import com.buildServer.kobalt.common.KobaltPathUtils.KOBALT_DIR
import com.buildServer.kobalt.common.KobaltRunnerConstants
import com.buildServer.kobalt.common.KobaltRunnerConstants.KOBALT_TASKS
import com.buildServer.kobalt.common.KobaltRunnerConstants.PATH_TO_BUILD_FILE
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
                elem.subElement("src").subElement(KOBALT_BUILD_FILE_NAME)?.let { buildFileElement ->
                    add(DiscoveredObject(KobaltRunnerConstants.RUNNER_TYPE, mapOf(
                            PATH_TO_BUILD_FILE to buildFileElement.fullName,
                            KOBALT_TASKS to "clean build"
                    )))
                }
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

    private fun Element?.subElement(elemNameToFind: String): Element?
            = (this?.children?.iterator() ?: null)
            ?.forEach { elem -> if (elem.name == elemNameToFind) return@subElement elem }
            .run { return null }

}