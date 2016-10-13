package com.buildServer.kobalt.server

import com.buildServer.kobalt.common.KobaltVersionManager
import com.buildServer.kobalt.common.http.ProxyLocator

/**
 * @author Dmitry Zhuravlev
 *         Date:  13.10.2016
 */
class KobaltVersionSelectBean {
    var selectedVersion = ""
    fun isVersionSelected() = selectedVersion.isNotEmpty()
    fun getVersions() = with(KobaltVersionManager(ProxyLocator.findServerProxyConfiguration())) {
        kobaltVersions()
    }
}