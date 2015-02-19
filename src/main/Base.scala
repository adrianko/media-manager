package main

import java.io._

import scala.io.Source._

class Base {

    val path: String = new File(getClass.getResource(".").getFile).getAbsolutePath + "/../../../../"
    
    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }
    
    def checkOS(): Unit = {
        if (!System.getProperty("os.name").contains("Windows")) {
            System.out.println("This application is not designed to run on any operating system other than Windows. Sorry.")
            System.exit(0)
        }
    }
    
    def checkService: Boolean = {
        try {
            fromInputStream(Runtime.getRuntime.exec("tasklist.exe").getInputStream).getLines().mkString
                    .contains(ex(MediaManager.settings.get("dl_exe")))
        } catch {
            case e: IOException => 
                e.printStackTrace()
                false
        }
    }

}