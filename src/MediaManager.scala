import org.json.simple._
import org.json.simple.parser._

import scala.io.Source._
import sys.process._

import java.io.File

object MediaManager {

    val os = System.getProperty("os.name").contains("Windows")
    val basePath: String = (getClass.getResource(".").getPath + "../../../").drop(if (os) 1 else 0)

    val cachePath: String = basePath + "cache/"
    val config: Map[String, String] = Map(fromFile(basePath + "conf/config").getLines()
        .map(_.replace("\n", "").split("=")).map(line => line(0).trim -> line(1).trim).toList: _*)

    val ut: Map[String, String] = Map[String, String](
        "user" -> "root",
        "pass" -> ex(config.get("password")),
        "win_host" -> "localhost",
        "other_host" -> ex(config.get("ip"))
    )

    val sourceDir: String = ex(config.get("video_dir"))
    val keepList: String = basePath + ex(config.get("keep_list"))
    val seedingMessage = "Seeding 100.0 %"

    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }

    def getURL(params: String): String = {
        val host = if (os) ex(ut.get("win_host")) else ex(ut.get("other_host"))

        "http://" + ex(ut.get("user")) + ":" + ex(ut.get("pass")) +
            "@" + host + ":8080/gui/?" + params + "&list=1&cid=0&getmsg=1" +
            "&t=" + System.currentTimeMillis
    }

    def getStatus: String = {
        Seq("wget", "-q", getURL("1=1"), "-O", cachePath + "download").!
        fromFile(cachePath + "download").getLines().toList.mkString("")
    }

    def sendAction(hash: String, action: String) = 
        Seq("wget", "-q", getURL("action=" + action + "&hash=" + hash), "-O", cachePath + "download").!
    

    def stop(hash: String) = sendAction(hash, "stop")

    def remove(hash: String) = sendAction(hash, "remove")
    
    def clearSeed(hash: String) = {
        stop(hash)
        remove(hash)
    }

    def rename(file: File) = {

    }

    def isVideoFile(f: File): Boolean = {
        (f.getName.contains(".mp4") || f.getName.contains(".mkv")) && f.isFile
    }

    def keepFile(files: List[File], keepList: Map[String, Int]): Unit = {
       files.foreach { f: File =>
            keepList.keys.foreach { t =>
                if (f.getName.contains(t) && isVideoFile(f)) {
                    //do something with it
                } else if (f.isDirectory) {
                    keepFile(f.listFiles.toList, keepList)
                }
            }
        }
    }

    def main(args: Array[String]) {
        val torrents: JSONArray = new JSONParser().parse(getStatus).asInstanceOf[JSONObject]
            .get("torrents").asInstanceOf[JSONArray]

        for (i: Int <- 0 to (torrents.size() - 1)) {
            val t = torrents.get(i).asInstanceOf[JSONArray]

            if (t.get(21) == seedingMessage) {
                clearSeed(t.get(0).toString)
            }
        }

        if (!os) {
            System.exit(0)
        }
        
        keepFile(new File(sourceDir).listFiles.toList, Map(fromFile(keepList).getLines()
            .map(_.replace("\n", "").split(",")).map(line => line(0).trim -> line(1).trim.toInt).toList: _*)
        )
    }

}