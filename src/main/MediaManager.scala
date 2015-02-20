package main

import org.json.simple.JSONArray

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
        val queue: JSONArray = Downloader.getQueue
        
        //JSON array doesn't support foreach. Maybe use an iterator?
        for (i: Int <- 0 to (queue.size() - 1)) {
            val t = queue.get(i).asInstanceOf[JSONArray]

            if (Downloader.complete(t.get(21).toString)) {
                Downloader.clear(t.get(0).toString)
            }
        }

        Manager.processFolder(Manager.fileList, DB.getKeepList).foreach{ f => println(f.getAbsoluteFile) }
    }

}