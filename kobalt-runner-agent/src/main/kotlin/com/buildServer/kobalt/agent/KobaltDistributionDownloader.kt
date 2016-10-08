package com.buildServer.kobalt.agent

import com.buildServer.kobalt.agent.KobaltPathUtils.kobaltDistributionsDir
import com.buildServer.kobalt.common.KobaltRunnerConstants.MIN_KOBALT_VERSION
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.util.io.ZipUtil
import jetbrains.buildServer.agent.BuildProgressLogger
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.zip.ZipFile
import jetbrains.buildServer.log.Loggers.AGENT as AGENT_LOG

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */
internal class KobaltDistributionDownloader(val buildProgressLogger: BuildProgressLogger) {
    companion object {
        private val RELEASE_URL = "https://api.github.com/repos/cbeust/kobalt/releases"
        private val FILE_NAME = "kobalt"

        fun latestKobaltVersionOrDefault(default: String = MIN_KOBALT_VERSION): String {
            try {
                return latestKobaltVersionRequest().get(20, TimeUnit.SECONDS)
            } catch(ex: Exception) {
                return default
            }
        }


        fun latestKobaltVersionRequest(): Future<String> {
            val callable = Callable<String> {
                var result = MIN_KOBALT_VERSION
                try {
                    val (request, response, responseStr) = RELEASE_URL.httpGet().responseString()
                    result = parseVersion(responseStr.get())
                } catch(ex: IOException) {
                    AGENT_LOG.warn(
                            "Couldn't load the release URL: $RELEASE_URL")
                }
                result
            }
            return Executors.newFixedThreadPool(1).submit(callable)
        }

        private fun parseVersion(response: String) = with(response) {
            var version: String = MIN_KOBALT_VERSION
            val jo = JsonParser().parse(this) as JsonArray
            if (jo.size() > 0) {
                var versionName = (jo.get(0) as JsonObject).get("name").asString
                if (versionName == null || versionName.isBlank()) {
                    versionName = (jo.get(0) as JsonObject).get("tag_name").asString
                }
                if (versionName != null) {
                    version = versionName
                }
            }
            version
        }
    }

    fun installIfNeeded(version: String, onSuccessDownload: (String) -> Unit, onSuccessInstall: (String) -> Unit)
            = with(version) {
        if (!Files.exists(KobaltPathUtils.kobaltExecutablePath(version))) {
            install(version = version, onSuccessDownload = onSuccessDownload, onSuccessInstall = onSuccessInstall)
        }
    }

    fun installLatestIfNeeded(onSuccessDownload: (String) -> Unit, onSuccessInstall: (String) -> Unit)
            = KobaltDistributionDownloader.latestKobaltVersionOrDefault().let { latestVersion ->
        if (!Files.exists(KobaltPathUtils.kobaltExecutablePath(latestVersion))) {
            install(version = latestVersion, onSuccessDownload = onSuccessDownload, onSuccessInstall = onSuccessInstall)
        }
    }

    fun install(version: String = latestKobaltVersionOrDefault(), onSuccessDownload: (String) -> Unit, onSuccessInstall: (String) -> Unit): Path {
        val fileName = "$FILE_NAME-$version.zip"
        File(kobaltDistributionsDir).mkdirs()
        val localZipFile = Paths.get(kobaltDistributionsDir, fileName)
        val zipOutputDir = kobaltDistributionsDir
        val kobaltJarFile = Paths.get(zipOutputDir, "kobalt-$version/kobalt/wrapper/$FILE_NAME-$version.jar")
        if (!Files.exists(localZipFile) || !Files.exists(kobaltJarFile)) {
            //
            // Either the .zip or the .jar is missing, downloading it
            //
            AGENT_LOG.info("Downloading $fileName ...")
            val file = localZipFile.toFile()
            download(version, file)
            AGENT_LOG.info("Download complete $fileName")

            if (!file.exists()) {
                AGENT_LOG.debug(file.toString() + " downloaded, extracting it")
            } else {
                AGENT_LOG.debug(file.toString() + " already exists, extracting it")
            }
            //
            // Extract all the zip files
            //
            val zipFile = ZipFile(file)
            val outputDirectory = File(kobaltDistributionsDir)
            outputDirectory.mkdirs()
            buildProgressLogger.progressMessage("Extracting $fileName to ${outputDirectory.absolutePath}")
            try {
                ZipUtil.extract(zipFile, outputDirectory, null)
            } catch(e: IOException) {
                AGENT_LOG.warn("Error while unzipping $zipFile to $outputDirectory : $e")
            }

        } else {
            AGENT_LOG.debug("$localZipFile already present, no need to download it")
            onSuccessInstall(version)
        }

        return kobaltJarFile
    }

    private fun download(version: String, file: File) {
        val downloadUrl = "http://beust.com/kobalt/kobalt-$version.zip"
        buildProgressLogger.progressStarted("Downloading $downloadUrl")
        Fuel.download(downloadUrl).destination { response, url ->
            file
        }.responseString()
        buildProgressLogger.progressMessage("Download complete")
        buildProgressLogger.progressFinished()
    }

}