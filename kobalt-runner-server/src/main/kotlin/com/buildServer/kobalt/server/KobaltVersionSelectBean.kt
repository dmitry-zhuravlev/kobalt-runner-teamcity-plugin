package com.buildServer.kobalt.server

import com.buildServer.kobalt.common.KobaltVersionManager

/**
 * @author Dmitry Zhuravlev
 *         Date:  13.10.2016
 */
class KobaltVersionSelectBean {
    var selectedVersion = ""
    fun isVersionSelected() = selectedVersion.isNotEmpty()
    fun getVersions() = with(KobaltVersionManager()) {
        kobaltVersions()
    }
}