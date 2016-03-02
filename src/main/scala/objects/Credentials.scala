package objects

object Credentials { 
  // todo: change to be database driven
  val sid_len = 999
  val user_regex = "^[a-zA-Z]*$" // remember to check for ^ and $
  val pass_regex = "^[a-zA-Z]*$"
  
  case class User (
    val user: String, 
    val pass: String,
    val fname: String,
    val lname: String
  )
  
  var user_list: List[User] = List ()
  
  val user_to_sid = new java.util.HashMap[String, String] ()
  val sid_to_user = new java.util.HashMap[String, String] ()
  
  val hash_pass = (pass: String) => pass
  
  val generate_sid = () =>
    List.fill (sid_len) {util.Random.nextInt (10)}
      .map (_.toString ())
      .reduce (_ + _)
  
  val is_login_valid = (user: String, pass: String) =>
    user_list.find (h => h.user == user && h.pass == hash_pass (pass))
  
  val login_user = (u: User) => {
    val sid = generate_sid ()
    
    sid
  }
  
  val try_login = (user: String, pass: String) =>
    is_login_valid (user, pass) match {
      case Some (u: User) => Some (login_user (u))
      case _ => None
    }
  
  def ??[T] (x: Option[T]) (y: T) = x match {case Some (z) => z case _ => y}
  
  val is_user_valid = (u: User) =>
    u.user.matches (user_regex) && u.pass.matches (pass_regex) &&
      user_list.forall (_.user != u.user)
  
  val try_register = (map: Map[String, String]) => {
    val u = User (
      ?? (map.get ("user")) (""), 
      ?? (map.get ("pass")) (""),
      ?? (map.get ("fname")) (""),
      ?? (map.get ("lname")) ("")
    )
    // if all checks pass, add user
    if (is_user_valid (u)) {
      user_list = u :: user_list
      Some (login_user (u))
    }
    else
      None
  }
}