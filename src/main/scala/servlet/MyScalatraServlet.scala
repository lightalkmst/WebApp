package servlet

import org.scalatra._
import java.io._
import scala.io.Source
import java.nio.file.{Files, Paths, Path}

class MyScalatraServlet extends WebappStack {
  /********
  *       *
  * LOGIN *
  *       *
  ********/
  post ("/api/login") {
    val json = scala.util.parsing.json.JSON.parseFull (request.body)
    json match {
      case Some (e: Map[String, String]) => {
        val user = e.get ("user") match {case Some (x) => x case _ => ""}
        val pass = e.get ("pass") match {case Some (x) => x case _ => ""}
        objects.Credentials.try_login (user, pass) match {
          case Some (s) => s
          case _ => status (400); ""
        }
      }
      case _ => ""
    }
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
          val src = Source.fromFile (path)
          try src.mkString finally src.close ()
        }
        file_type match {
          case "html" => scala.xml.Unparsed (get_file_text ())
          case "js" => get_file_text ()
          case "css" => get_file_text ()
          case "jpg" => Files.readAllBytes (Paths.get (path))
          case _ => throw new Exception ("There was an internal server error: invalid file type.")
        }
      }
      catch {
        case e: Throwable =>
          e.printStackTrace ()
          "There was an internal server error: the page could not be loaded."
      }
  })
}
