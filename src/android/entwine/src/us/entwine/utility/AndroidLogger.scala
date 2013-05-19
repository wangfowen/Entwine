package us.entwine.utility

import java.util.Date

import android.util.Log

object AndroidLogger {
	private val STR_LENGTH = -1
	private val MSG_NAME = "EatIn Android Logger"
	private def truncate(str: String): String = {
		if (STR_LENGTH != -1 && str.length() > STR_LENGTH)
			str.substring(0, STR_LENGTH) + "..."
		else
			str
	}
	
	def debug(str: String, showDate: Boolean = false)(implicit name: String = MSG_NAME) =
		Log.d(name, s"${truncate(str)}")
		
	def error(str: String, showDate: Boolean = false)(implicit name: String = MSG_NAME) =
		Log.e(name, s"${truncate(str)}")
		
	def info(str: String, showDate: Boolean = false)(implicit name: String = MSG_NAME) =
		Log.i(name, s"${truncate(str)}")
		
	def warn(str: String, showDate: Boolean = false)(implicit name: String = MSG_NAME) =
		Log.w(name, s"${truncate(str)}")
}