package main

import java.io.File

object MediaLibrary extends Base {

    def move(src: File): Unit = ()

    def refresh(): Unit = Downloader.download("http://" + ex(MediaManager.settings.get("lib_host")) + ":" +
        ex(MediaManager.settings.get("lib_port")) + ex(MediaManager.settings.get("lib_path")))
}