class Base {

    /**
     * extract map options
     * @param x Some/None
     * @return
     */
    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }

}