package main

import java.io.{File, IOException}

import scala.io.Source._

class Base {

    val path: String = new File(getClass.getResource(".").getFile).getAbsolutePath + "/../../../../"
    
    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }
    
    def checkOS(): Unit = {
        if (!System.getProperty("os.name").contains("Windows")) {
            println("This application is not designed to run on any operating system other than Windows. Sorry.")
            System.exit(0)
        }
    }
    
    def checkService(): Unit = {
        try {
            if (!fromInputStream(Runtime.getRuntime.exec("tasklist.exe").getInputStream).getLines().mkString
                    .contains(ex(MediaManager.settings.get("dl_exe")))) {
                println("Service not running. Sorry.")
                System.exit(0)
            }
        } catch {
            case e: IOException => 
                e.printStackTrace()
                System.exit(0)
        }
    }

}