package main

import java.io.File
import java.sql._

object DB {

    var conn: Connection = null
    var stmt: Statement = null

    def getConnection: Connection = {
        if (conn == null) {
            try {
                Class.forName("org.sqlite.JDBC")
                conn = DriverManager.getConnection("jdbc:sqlite:" + new File(getClass.getResource(".").getFile).getAbsolutePath + "/../../../../db/config.db")
            } catch {
                case e: SQLException => e.printStackTrace()
            }
        }

        conn
    }

    def getStatement: Statement = {
        if (stmt == null) {
            stmt = getConnection.createStatement
        }

        stmt
    }

    def loadSettings: Map[String, String] = {
        //val settings: collection.mutable.Map[String, String] = collection.mutable.Map[String, String]()
        try {
            val rs: ResultSet = getStatement.executeQuery("SELECT * FROM settings")

            /*
            while (rs.next) {
                settings += rs.getString("property") -> rs.getString("value")
            }
            */
            class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
                def hasNext = rs.next()
                def next() = rs
            }

            new ResultSetIterator(rs).map(x => (x.getString("property"), x.getString("value"))).toMap
        } catch {
            case e: SQLException =>
                e.printStackTrace()
                Map[String, String]()
        }

        //settings.toMap

    }

}