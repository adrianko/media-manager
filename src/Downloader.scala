import java.io.{InputStreamReader, BufferedReader, InputStream}
import java.net.{URL, HttpURLConnection}
import java.util.Base64

object Downloader extends Base {
    
    val seedingMessage: String = "Seeding 100.0 %"
    
    def getURL(params: String): String = "http://" + ex(MediaManager.ut.get("win_host")) + ":8080/gui/?" + params +
        "&list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
    
    def getStatus: String = download(getURL("1=1"), content = true)
    
    def sendAction(hash: String, action: String) = download(getURL("action=" + action + "&hash=" + hash), content = false)
    
    def download(url: String, content: Boolean): String = {
        val con: HttpURLConnection  = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
        con.setRequestMethod("GET")
        con.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder.encode(
            (ex(MediaManager.ut.get("user")) + ":" + ex(MediaManager.ut.get("pass"))).getBytes))
        )

        if (!content) {
            con.getResponseCode.toString
        } else {
            Iterator.continually(new BufferedReader(new InputStreamReader(con.getContent.asInstanceOf[InputStream])).readLine)
                    .takeWhile(_ != null).mkString
        }
    }
    
    def stop(hash: String) = sendAction(hash, "stop")
    
    def remove(hash: String) = sendAction(hash, "remove")
    
    def clearSeed(hash: String) = {
        stop(hash)
        remove(hash)
    }

}