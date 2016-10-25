package com.buildServer.kobalt.agent

import com.buildServer.kobalt.common.KobaltPathUtils
import com.buildServer.kobalt.common.KobaltPathUtils.kobaltDistributionsDir
import com.buildServer.kobalt.common.KobaltVersionManager
import com.github.kittinunf.fuel.Fuel
import com.intellij.util.io.ZipUtil
import jetbrains.buildServer.agent.BuildProgressLogger
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile
import jetbrains.buildServer.log.Loggers.AGENT as AGENT_LOG

/**
 * @author Dmitry Zhuravlev
 *         Date: 08/10/2016
 */
internal class DistributionDownloaderException(override val message: String, override val cause: Throwable) : Exception(message, cause)

internal class KobaltDistributionDownloader(val buildProgressLogger: BuildProgressLogger) {
    companion object {
        private val RELEASE_URL = "https://api.github.com/repos/cbeust/kobalt/releases"
        private val FILE_NAME = "kobalt"
    }
  private val kobaltVersionManager = KobaltVersionManager()

    fun installIfNeeded(version: String, onSuccessDownload: (String) -> Unit, onSuccessInstall: (String) -> Unit)
            = with(version) {
        if (!Files.exists(KobaltPathUtils.kobaltJarPath(version))) {
            install(version = version, onSuccessDownload = onSuccessDownload, onSuccessInstall = onSuccessInstall)
        }
    }

    fun installLatestIfNeeded(onSuccessDownload: (String) -> Unit, onSuccessInstall: (String) -> Unit)
            = kobaltVersionManager.latestKobaltVersionOrDefault().let { latestVersion ->
        if (!Files.exists(KobaltPathUtils.kobaltJarPath(latestVersion))) {
            install(version = latestVersion, onSuccessDownload = onSuccessDownload, onSuccessInstall = onSuccessInstall)
        }
    }

    fun install(version: String = kobaltVersionManager.latestKobaltVersionOrDefault(), onSuccessDownload: (String) -> Unit, onSuccessInstall: (String) -> Unit): Path {
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
            onSuccessDownload(version)
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
        val (request, response, result) = Fuel.download(downloadUrl).destination { response, url ->
            file
        }.response()
        val (data, error) = result //TODO it is not a good idea to load all bytes in memory
        if (error != null) throw  throw DistributionDownloaderException("Download of $downloadUrl failed with error ${error.exception.message ?: ""}", error)
        buildProgressLogger.progressMessage("Download complete")
        buildProgressLogger.progressFinished()
    }

}