package us.entwine.utility

import scala.concurrent.ExecutionContext.Implicits.global
import us.entwine.models.Event
import scala.concurrent.Future
import us.entwine.models.User
import com.linkedin.eatin.EntwineApplication
import org.json.JSONArray

object HttpApis {
    def getEvents(): Future[List[Event]] = {
        HttpHelpers.get(AndroidConstants.URL_API_EVENT, Map(
                AndroidConstants.PARAM_KEY_OWNER_ID -> EntwineApplication.getCurrentUser().get.toString )) map { results =>
            
            val jsarray = new JSONArray(results)
            (for (i <- 0 to jsarray.length().toInt) yield {
                val json = jsarray.getJSONObject(i)
                new Event(
                        json.getLong("event_id"),
                        json.getString("name"),
                        json.getString("description"),
                        null)
            }).toList
        }
    }
}