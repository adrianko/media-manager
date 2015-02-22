package main

import java.io.File

object MediaLibrary extends Base {

    val mediaLibraryFolder: File = new File(ex(MediaManager.settings.get("lib_folder")))

    val processingList = new File(ex(MediaManager.settings.get("processed_dir"))).listFiles.toList

    def retrieveFiles(): Unit = {
        val showFolders:Map[String, File] = Map(mediaLibraryFolder.listFiles.filter(_.isDirectory)
            .map(d => d.getName -> d).toList: _*)
    }

    def move(src: File): Unit = ()

    def refresh(): Unit = Downloader.download("http://" + ex(MediaManager.settings.get("lib_host")) + ":" +
        ex(MediaManager.settings.get("lib_port")) + ex(MediaManager.settings.get("lib_path")))

}