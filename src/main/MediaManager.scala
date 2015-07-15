package main

/**
 * MediaManager class
 * 
 * TODO add kill t process when cleared equals queue size
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
        Manager.moveFiles()
        Manager.processFolder()
        Manager.cleanupFolder()
        MediaLibrary.retrieveFiles()
        MediaLibrary.refresh()
    }

}