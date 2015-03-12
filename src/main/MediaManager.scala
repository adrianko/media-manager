package main

/**
 * MediaManager class
 * TODO Process files to be kept
 * TODO SD files move to sync and library
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
        Manager.retrieveFiles().foreach{ f =>
            if (f.getName.toLowerCase.contains("1080p")) {
                Manager.move(f.getAbsoluteFile)
            } else {
                Manager.copy(f.getAbsoluteFile)
            }
        }
        Manager.processFolder()
        Manager.cleanupFolder()
        MediaLibrary.retrieveFiles()
        MediaLibrary.refresh()
    }

}