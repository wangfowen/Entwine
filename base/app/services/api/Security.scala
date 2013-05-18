package services.api

import play.data.validation.Constraints.EmailValidator

object Security {
	
	def minPasswordLength = 6
	
	private var emailValidator: EmailValidator = null
	
	def getEmailValidator (): EmailValidator = {
		if (this.emailValidator == null)
			this.emailValidator = new EmailValidator()
		this.emailValidator
	}
	
	def isValidEmailAddress (email: String): Boolean = {
		if (email != null && email.length() > 0)
			this.getEmailValidator().isValid(email)
		else
			false
	}
	
	def isValidPassword (password: String): Boolean = {
		if (password.length() < minPasswordLength)
			throw new IllegalArgumentException("The password length: " + password.length() + " is shorter than required length: " + minPasswordLength + ".")
		true
	}
	
}