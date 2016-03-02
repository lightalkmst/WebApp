package objects

object Credentials { 
  val sid_len = 999
  val user_regex = "" // remember to check for ^ and $
  val pass_regex = ""
  
  case class User (
    val user: String, 
    val pass: String,
    val name: String
  )
  
  var user_list: List[User] = List ()
  
  val user_to_sid = new java.util.HashMap[String, String] ()
  val sid_to_user = new java.util.HashMap[String, String] ()
  
  var hash_pass = (pass: String) => pass
  
  var generate_sid = () =>
    List.fill (sid_len) {util.Random.nextInt (10)}
      .map (_.toString ())
      .reduce (_ + _)
  
  var is_user_valid = (u: User) =>
    u.user.matches (user_regex) && u.pass.matches (pass_regex) &&
      user_list.exists (h => h.user == u.user && h.pass == hash_pass (u.pass))
  
  var register_user = (u: User) =>
    // if all checks pass, add user
    if (is_user_valid (u)) {
      login_user (u)
      true
    }
    else
      false
  
  var is_login_valid = (user: String, pass: String) =>
    user_list.find (h => h.user == user && h.pass == hash_pass (pass))
  
  var login_user = (u: User) => {
    // deal with sid here
    user_list = u :: user_list
    generate_sid ()
  }
  
  var try_login = (user: String, pass: String) =>
    is_login_valid (user, pass) match {
      case Some (u: User) => Some (login_user (u))
      case _ => None
    }
  
}