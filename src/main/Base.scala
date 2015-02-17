package main

class Base {
    
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

}