import scala.io.Source._
import sys.process._

import java.io.File

object MediaManager {

    val cachePath: String = new File(".").getCanonicalPath + "/cache/"
    val config: Map[String, String] = Map(fromFile("config").getLines().map(_.replace("\n", "").split("=")).map(line => line(0) -> line(1)).toList : _*)

    val ut: Map[String, String] = Map[String, String](
        "user" -> "root",
        "pass" -> ex(config.get("password")),
        "win_host" -> "localhost",
        "other_host" -> ex(config.get("ip"))
    )

    val sourceDir: String = ex(config.get("video_dir"))
    val keepList: String = ex(config.get("keep_list"))


    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }

    def getStatus: String = {
        val host = if (System.getProperty("os.name").contains("Windows")) ex(ut.get("win_host")) else ex(ut.get("other_host"))
        val url = "http://" + ex(ut.get("user")) + ":" + ex(ut.get("pass")) + "@" + host + ":8080/gui/?list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
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

        val jsonS = getStatus

    }

}
