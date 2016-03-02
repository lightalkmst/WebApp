package servlet

import org.scalatra._
import java.io._
import scala.io.Source
import java.nio.file.{Files, Paths, Path}

class MyScalatraServlet extends WebappStack {
  val map_of_json = () => 
    scala.util.parsing.json.JSON.parseFull (request.body) match {
      case Some (e: Map[String, String]) => e
      case _ => Map[String, String] ()
    }
  
  /***********
  *          *
  * REGISTER *
  *          *
  ***********/
  post ("/api/register") {
    objects.Credentials.try_register (map_of_json ()) match {
      case Some (s) => s
      case _ => status (403); ""
    }
  }
  
  /********
  *       *
  * LOGIN *
  *       *
  ********/
  post ("/api/login") {
    val json = map_of_json ()
    val user = json.get ("user") match {case Some (x) => x case _ => ""}
    val pass = json.get ("pass") match {case Some (x) => x case _ => ""}
    objects.Credentials.try_login (user, pass) match {
      case Some (s) => s
      case _ => status (403); ""
    }
  }
  
  /*********
  *        *
  * LOGOUT *
  *        *
  *********/
  post ("/api/logout") {
    println ("logout")
    println (cookies)
  }
  
  /**********
  *         *
  * DEFAULT *
  *         * 
  **********/
  val webapp_folder = new File ("").getAbsolutePath () + "\\target\\webapp\\webapp"
  val separator = File.separatorChar
  val types = 
    List (
      "html",
      "css",
      "js",
      "jpg"
    )
  
  val files = 
    List ("/") ++
    types.flatMap (x => new File (webapp_folder + '\\' + x).listFiles)
      .map (_.toString ())
      .map (x => x.substring (x.lastIndexOf ('\\') + 1))
      .map ('/' + _)
  
  files.foreach (uri =>
    get (uri) {
      try {
        val name = if (uri == "/") "index.html" else uri.substring (1)
        val file_type = name.substring (name.lastIndexOf (".") + 1)
        val path = 
          webapp_folder + java.io.File.separatorChar + 
          file_type + java.io.File.separatorChar + 
          name
        val get_file_text = () => {
          var (src: Source) = null
          try {
            src = Source.fromFile (path)
            src.mkString
          }
          finally if (src != null) src.close ()
        }
        file_type match {
          case "html" => scala.xml.Unparsed (get_file_text ())
          case "js" => get_file_text ()
          case "css" => get_file_text ()
          case "jpg" => Files.readAllBytes (Paths.get (path))
          case _ => status (400); ""
        }
      }
      catch {
        case e: Throwable =>
          e.printStackTrace ()
          status (400); ""
      }
  })
}
