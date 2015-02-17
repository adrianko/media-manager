package main

class Base {
    
    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }

}