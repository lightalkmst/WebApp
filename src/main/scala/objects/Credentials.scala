package objects

object Credentials { 
  val sid_len = 999
  val user_regex = "" // remember to check for ^ and $
  val pass_regex = ""
  
  case class User (
    val user: String, 
    val pass: String
  )
  
  var user_list: List[User] = List ()
  
  val user_to_sid = new java.util.HashMap[String, String] ()
  val sid_to_user = new java.util.HashMap[String, String] ()
  
  def hash_pass (pass: String): String = pass
  
  def generate_sid () = 
    List.fill (sid_len) {util.Random.nextInt (10)}
      .map (_.toString ())
      .reduce (_ + _)
  
  def is_user_valid (user: String, pass: String): Boolean =
    user.matches (user_regex) && pass.matches (pass_regex) &&
      user_list.exists (h => h.user == user && hash_pass (h.pass) == hash_pass (pass))
  
  def try_login (user: String, pass: String): Option[String] =
    if (is_user_valid (user, pass)) {
      Some (generate_sid ())
    }
    else {
      None
    }
}