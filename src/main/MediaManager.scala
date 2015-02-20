package main

import java.io.File

import org.json.simple.{JSONArray, JSONObject}
import org.json.simple.parser.JSONParser

/**
 * MediaManager class
 * TODO Process files to be kept
 * TODO SD files move to sync
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
        val queue: JSONArray = new JSONParser().parse(Downloader.getStatus).asInstanceOf[JSONObject].get("torrents")
                .asInstanceOf[JSONArray]
        
        //JSON array doesn't support foreach. Maybe use an iterator?
        for (i: Int <- 0 to (queue.size() - 1)) {
            val t = queue.get(i).asInstanceOf[JSONArray]

            if (Downloader.seedingMessage.contains(t.get(21).toString)) {
                Downloader.clearSeed(t.get(0).toString)
            }
        }

        Manager.processFolder(new File(ex(settings.get("video_dir"))).listFiles.toList, DB.getKeepList).foreach{
            f => println(f.getAbsoluteFile)
        }
    }

}