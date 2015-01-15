import scala.io.Source._
import sys.process._

import java.io.File

object MediaManager {

    val sourceDir: String = "G:/Downloads/complete"
    val keepList: String = "keep.list"
    val cachePath: String = new File(".").getCanonicalPath + "/cache/"

    val ut: Map[String, String] = Map[String, String](
        "user" -> "root",
        "pass" -> fromFile("password").getLines().toList.mkString(""),
        "host" -> "localhost:8080/gui"
    )

    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }

    def getStatus: String = {
        val url = "http://" + ex(ut.get("user")) + ":" + ex(ut.get("pass")) + "@" + ex(ut.get("host")) + "/?list=1&cid=0&getmsg=1"
        Seq("wget", "-q", url + "&t=" + System.currentTimeMillis, "-O", cachePath + "download").!
        fromFile(cachePath + "download").getLines().toList.mkString("")
    }

    def main(args: Array[String]) {
        val keepListShows: collection.mutable.Map[String, Int] = collection.mutable.Map()
        //val sourceFiles: List[File] = new File(sourceDir).listFiles.toList
        //sourceFiles.foreach((f: File) => println(f.getAbsoluteFile))

        fromFile(keepList).getLines().map(_.replace("\n", "").split(",")).foreach((line: Array[String]) => {
            keepListShows += line(0) -> line(1).toInt
        })

        for(i <- 1 to 10) {
            println(getStatus)
        }

    }

}
