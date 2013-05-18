package database.api

import com.avaje.ebean.Ebean

import database.models.Subscriber
import database.models.EmailAccount
import database.models.FullAccount
import javax.persistence.Entity
import javax.persistence.Table
import services.api.Security

object AccountsAPI {
	
	/**
	 * Create a new subscriber, providing the email is not already subscribed
	 */
	def createSubscriber (_email: String): Subscriber = {
		if (!Security.isValidEmailAddress(_email))
			throw new IllegalArgumentException("An invalid email address was supplied.")
		
		var email = _email.trim();
			
		var subscriber = Ebean.find(classOf[Subscriber]).where().eq("emailAccount.email", email).findUnique()
		if (subscriber == null) {
			subscriber = new Subscriber(this.getEmailAccountByEmail(email, true))
			Ebean.save(subscriber)
			subscriber
		}
		else
			throw new IllegalArgumentException("This email has already been subscribed to our mailing list.")
	}
	
	/**
	 * Retrieves an email-linked account using email, possibly creating one if it doesn't exist
	 */
	def getEmailAccountByEmail (_email: String, autoCreate: Boolean = false): EmailAccount = {
		if (!Security.isValidEmailAddress(_email))
			throw new IllegalArgumentException("An invalid email address was supplied.")
		
		var email = _email.trim()
		var account = Ebean.find(classOf[EmailAccount]).where().eq("email", email).findUnique()
		
		if (account == null) {
			if (autoCreate)
				account = this.createEmailAccount(email)
			else
				throw new InstantiationException("The email address: " + _email + " does not exist.")
		}
		account
	}
	
	/**
	 * Creates a new email-linked account
	 */
	def createEmailAccount (email: String): EmailAccount = {
		if (!Security.isValidEmailAddress(email))
			throw new IllegalArgumentException(email + " - is not a valid email.")
			
		var account = new EmailAccount()
		account.setEmail(email)
		Ebean.save(account)
		account
	}
	
	def createFullAccount (email: String, password: String): FullAccount = {
		if (!Security.isValidPassword(password))
			throw new IllegalArgumentException("The entered password is invalid.")
		
		var emailAccount = getEmailAccountByEmail(email, true)
		var account = Ebean.find(classOf[FullAccount]).where().eq("emailAccount", emailAccount).findUnique()
		
		if (account == null)
			account = new FullAccount()
		else
			throw new InstantiationException("An account with this email address already exists.")
		
		account.setEmailAccount(emailAccount)
		account.setPassword(password)
		
		Ebean.save(account)
		
		account
	}
	
	/**
	 * Authenticates a full user using a username and password
	 */
	def authenticate (username: String, password: String): FullAccount = {
		var account = Ebean.find(classOf[FullAccount]).where().eq("emailAccount.email", username).eq("password", password).findUnique()
		if (account == null)
			throw new SecurityException("The entered username or password is incorrect.")
		account
	}

	/**
	 * Retrieves a full account using user ID, most likely to be used in sessions
	 */
	def getFullAccountById (id: Long): FullAccount = {
		var ret = Ebean.find(classOf[FullAccount], id)
		if (ret == null)
			throw new IllegalArgumentException("That user ID does not correspond with an existing account.")
		ret
	}
	
	/**
	 * Update a full account with new details
	 */
	def updateFullAccount (account: FullAccount) = {
		Ebean.update(account)
	}
	
	def updateEmailAccount (account: EmailAccount) = {
		Ebean.update(account)
	}
	
}