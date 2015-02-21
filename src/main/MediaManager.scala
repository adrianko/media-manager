package main

/**
 * MediaManager class
 * TODO Process files to be kept
 * TODO SD files move to sync and library
 * TODO HD files straight to library
 */
object MediaManager extends Base {

    val settings: Map[String, String] = DB.loadSettings

    def main(args: Array[String]) {
        checkOS()
        checkService()
        MediaManager()
    }
    
    def MediaManager(): Unit = {
        Downloader.clearFinished()
        Manager.retrieveFiles().foreach{ f => Manager.move(f.getAbsoluteFile) }
        Manager.processFolder()
        Manager.cleanupFolder()
        MediaLibrary.refresh()
    }

}