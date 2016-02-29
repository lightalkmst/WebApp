package objects

object Credentials {
  case class User (
    val user: String, 
    val pass: String
  )
  
  var user_list = List ()
  
  val user_to_sid = new java.util.HashMap[String, String] ()
  val sid_to_user = new java.util.HashMap[String, String] ()
}