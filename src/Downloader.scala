import java.io.{InputStreamReader, BufferedReader, InputStream}
import java.net.{URL, HttpURLConnection}
import java.util.Base64

object Downloader extends Base {

    /**
     * cache path / download files
     */
    val cachePath: String = MediaManager.basePath + "cache/"

    /**
     * message displayed when file download finished
     */
    val seedingMessage = "Seeding 100.0 %"

    /**
     * build url with timestamps and other attributes
     * @param params GET parameters
     * @return
     */
    def getURL(params: String): String = {
        val host = if (MediaManager.os) ex(MediaManager.ut.get("win_host")) else ex(MediaManager.ut.get("other_host"))

        "http://" + ex(MediaManager.ut.get("user")) + ":" + ex(MediaManager.ut.get("pass")) + "@" + host + ":8080/gui/?" +
            params + "&list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
    }

    /**
     * Current download status
     * @return String
     */
    def getStatus: String = download(getURL("1=1"), content = true)

    /**
     * stop or delete completed file queue
     * @param hash File hash
     * @param action What to do with it
     * @return
     */
    def sendAction(hash: String, action: String) = download(getURL("action=" + action + "&hash=" + hash), content = false)

    /**
     * Send request
     * @param url String
     * @return None
     */
    def download(url: String, content: Boolean): String = {
        val con: HttpURLConnection  = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
        con.setRequestMethod("GET")
        con.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder.encode(
            (ex(MediaManager.ut.get("user")) + ":" + ex(MediaManager.ut.get("pass"))).getBytes))
        )

        if (!content) {
            con.getResponseCode.toString
        } else {
            val br : BufferedReader = new BufferedReader(new InputStreamReader(con.getContent.asInstanceOf[InputStream]))
            val text: StringBuilder = new StringBuilder
            var line = br.readLine

            while (line != null) {
                text.append(line)
                line = br.readLine
            }

            text.toString()
        }
    }

    /**
     * Stop action
     * @param hash String
     * @return None
     */
    def stop(hash: String) = sendAction(hash, "stop")

    /**
     * Remove action
     * @param hash String
     * @return
     */
    def remove(hash: String) = sendAction(hash, "remove")

    /**
     * Do both actions for completed file
     * @param hash String
     * @return None
     */
    def clearSeed(hash: String) = {
        stop(hash)
        remove(hash)
    }

}