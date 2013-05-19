package com.linkedin.eatin

import scala.concurrent.ExecutionContext.Implicits.global;
import us.entwine.models.User
import us.entwine.utility.HttpHelpers
import scala.concurrent.Future
import us.entwine.utility.AndroidConstants
import org.json.JSONObject

object EntwineApplication {
    private var currentUser: Option[User] = None
    
    def getCurrentUser() = currentUser
    
    def login(email: String, password: String): Future[User] = {
        HttpHelpers.post(AndroidConstants.URL_API_LOGIN, Map(
                AndroidConstants.PARAM_KEY_EMAIL -> email,
                AndroidConstants.PARAM_KEY_PASSWORD -> password)) map { results =>
            val json = new JSONObject(results)
            val ret = new User(
                    json.getLong("id"),
                    json.getString("email"),
                    json.getString("firstName"),
                    json.getString("lastName"))
            currentUser = Some(ret)
            ret
        }
    } 
}