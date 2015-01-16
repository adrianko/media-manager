import org.json.simple._
import org.json.simple.parser._

import scala.io.Source._
import sys.process._

import java.io.File

object MediaManager {

    val cachePath: String = new File(".").getCanonicalPath + "/cache/"
    val config: Map[String, String] = Map(fromFile("conf/config").getLines()
        .map(_.replace("\n", "").split("=")).map(line => line(0).trim -> line(1).trim).toList: _*)

    val ut: Map[String, String] = Map[String, String](
        "user" -> "root",
        "pass" -> ex(config.get("password")),
        "win_host" -> "localhost",
        "other_host" -> ex(config.get("ip"))
    )

    val sourceDir: String = ex(config.get("video_dir"))
    val keepList: String = ex(config.get("keep_list"))
    val seedingMessage = "Seeding 100.0 %"

    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }

    def getStatus: String = {
        val host = if (System.getProperty("os.name").contains("Windows")) {
            ex(ut.get("win_host"))
        } else {
            ex(ut.get("other_host"))
        }
        val url = "http://" + ex(ut.get("user")) + ":" + ex(ut.get("pass")) +
            "@" + host + ":8080/gui/?list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
        Seq("wget", "-q", url, "-O", cachePath + "download").!
        fromFile(cachePath + "download").getLines().toList.mkString("")
    }

    def main(args: Array[String]) {
        val keepListShows: collection.mutable.Map[String, Int] = collection.mutable.Map()

        if (System.getProperty("os.name").contains("Windows")) {
            val sourceFiles: List[File] = new File(sourceDir).listFiles.toList
        }

        fromFile(keepList).getLines().map(_.replace("\n", "").split(",")).foreach((line: Array[String]) => {
            keepListShows += line(0) -> line(1).toInt
        })

        val json: JSONObject = new JSONParser().parse(getStatus).asInstanceOf[JSONObject]
        val torrents: JSONArray = json.get("torrents").asInstanceOf[JSONArray]

        for (i: Int <- 0 to (torrents.size() - 1)) {
            val t = torrents.get(i).asInstanceOf[JSONArray]
            val hash = t.get(0)
            val status = t.get(21)
            println(hash + " " + status)
            if(status == seedingMessage) {
                val host = if (System.getProperty("os.name").contains("Windows")) {
                    ex(ut.get("win_host"))
                } else {
                    ex(ut.get("other_host"))
                }
                val url = "http://" + ex(ut.get("user")) + ":" + ex(ut.get("pass")) +
                    "@" + host + ":8080/gui/?" + "action=stop&hash="+hash+"&"
                    "list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
                Seq("wget", "-q", url, "-O", cachePath + "download").!
                val url2 = "http://" + ex(ut.get("user")) + ":" + ex(ut.get("pass")) +
                    "@" + host + ":8080/gui/?" + "action=remove&hash="+hash+"&"
                "list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
                Seq("wget", "-q", url2, "-O", cachePath + "download").!
            }
        }


    }

}