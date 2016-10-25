package com.buildServer.kobalt.common

import com.github.kittinunf.fuel.httpGet
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import jetbrains.buildServer.log.Loggers
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * @author Dmitry Zhuravlev
 *         Date:  13.10.2016
 */
class KobaltVersionManager() {
    companion object {
        private val RELEASE_URL = "https://api.github.com/repos/cbeust/kobalt/releases"
    }

    fun kobaltVersions(): List<String> {
        try {
            return kobaltVersionRequest().get(20, TimeUnit.SECONDS)
        } catch(ex: Exception) {
            return emptyList()
        }
    }

    fun latestKobaltVersionOrDefault(default: String = KobaltRunnerConstants.MIN_KOBALT_VERSION): String {
        try {
            return with(kobaltVersionRequest().get(20, TimeUnit.SECONDS)){ if(isEmpty()) default else get(0)}
        } catch(ex: Exception) {
            return default
        }
    }


    private fun kobaltVersionRequest(): Future<List<String>> {
        val callable = Callable<List<String>> {
            try {
                val (request, response, responseStr) = RELEASE_URL.httpGet().responseString()
                return@Callable parseVersions(responseStr.get())
            } catch(ex: IOException) {
                Loggers.AGENT.warn(
                        "Couldn't load the release URL: $RELEASE_URL")
            }
          emptyList<String>()
        }
        return Executors.newFixedThreadPool(1).submit(callable)
    }

    private fun parseVersions(response: String) = with(response) {
        val array = JsonParser().parse(this) as JsonArray
        val versions = sortedSetOf<String>()
        if (array.size() > 0) {
            val arrayIterator = array.iterator()
            while(arrayIterator.hasNext()) {
                with(versions) {
                    val elem = arrayIterator.next()
                    var versionName = (elem as JsonObject).get("name").asString
                    if (versionName == null || versionName.isBlank()) {
                        versionName = elem.get("tag_name").asString
                    }
                    if (versionName != null) {
                        add(versionName)
                    }
                }
            }
        }
        versions.sortedDescending()
    }
}