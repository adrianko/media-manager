package main

/**
 * MediaManager class
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
        MediaLibrary.retrieveFiles()
        MediaLibrary.refresh()
    }

}