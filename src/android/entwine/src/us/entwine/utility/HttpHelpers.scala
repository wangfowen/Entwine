package us.entwine.utility

import java.io.ByteArrayOutputStream
import java.io.IOException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.future

import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject

import android.net.Uri

object HttpHelpers {
    private implicit val MSG_NAME = "Http Helper"
    private lazy val client = new DefaultHttpClient()
    
    private def readResponse(response: HttpResponse): String = {
        val entity = response.getEntity()
        val out = new ByteArrayOutputStream()
        response.getEntity().writeTo(out)
        out.close()
        response.getEntity().consumeContent()
        
        val output = out.toString()
        
        response.getStatusLine().getStatusCode() match {
            case HttpStatus.SC_OK => output
            case HttpStatus.SC_UNAUTHORIZED => {
                AndroidLogger.error(s"${response.getStatusLine().getReasonPhrase()}: $output")
                throw new IllegalAccessException(output)
            }
            case HttpStatus.SC_BAD_REQUEST => {
                AndroidLogger.error(s"${response.getStatusLine().getReasonPhrase()}: $output")
                throw new IllegalArgumentException(output)
            }
            case _ => {
                AndroidLogger.error(s"${response.getStatusLine().getReasonPhrase()}: $output")
                throw new IOException(output)
            }
        }
    }
    
    private def bodyRequest(base: HttpEntityEnclosingRequestBase, params: Map[String, String]): Future[String] = {
        val obj = new JSONObject()
        params.foreach { value => obj.put(value._1, value._2) }
        bodyRequest(base, obj)
    }

    private def bodyRequest(base: HttpEntityEnclosingRequestBase, params: JSONObject): Future[String] = {
        val se = new StringEntity(params.toString())
        se.setContentType("application/json")
        AndroidLogger.debug(s"Sending body request to URI: ${base.getURI()} with params: $params")
        future {
            base.setEntity(se)
            this.synchronized {
                readResponse(client.execute(base))
            }
        }
    }
    
    def get(url: String, params: Map[String, String]): Future[String] = this.synchronized {
        val uri = Uri.parse(url).buildUpon()
        AndroidLogger.debug(s"Sending get request to URI: $uri with params: $params")
        future {
            
            params.foreach { value => uri.appendQueryParameter(value._1, value._2)}
            this.synchronized {
                readResponse(client.execute(new HttpGet(uri.toString())))
            }
        }
    }
    
    def post(url: String, params: Map[String, String]): Future[String] = bodyRequest(new HttpPost(url), params)
    def post(url: String, params: JSONObject): Future[String] =  bodyRequest(new HttpPost(url), params)
    def post(url: String): Future[String] =  bodyRequest(new HttpPost(url), new JSONObject())
    def put(url: String, params: Map[String, String]): Future[String] = bodyRequest(new HttpPut(url), params)
    def put(url: String, params: JSONObject): Future[String] =  bodyRequest(new HttpPut(url), params)
}