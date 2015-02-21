package main

import java.io.{File, IOException}

import scala.io.Source.fromInputStream

class Base {

    val path: String = new File(getClass.getResource(".").getFile).getAbsolutePath + "/../../../../"
    
    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }

    def exList(x: Option[List[String]]) = x match {
        case Some(s) => s
        case None => List()
    }
    
    def exit(code: Int): Unit = {
        println("Exiting...")
        System.exit(code)
    }
    
    def checkOS(): Unit = {
        if (!System.getProperty("os.name").contains("Windows")) {
            println("This application is not designed to run on any operating system other than Windows. Sorry.")
            exit(0)
        }
    }
    
    def checkService(): Unit = {
        try {
            if (!fromInputStream(Runtime.getRuntime.exec("tasklist.exe").getInputStream).getLines().mkString
                    .contains(ex(MediaManager.settings.get("dl_exe")))) {
                println("Service not running. Sorry.")
                exit(0)
            }
        } catch {
            case e: IOException => 
                e.printStackTrace()
                exit(0)
        }
    }

}