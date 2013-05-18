package database.models

import javax.persistence._
import scala.collection.mutable._
import play.db.ebean.Model

@Entity
@Table (name="subscriber")
class Subscriber (_emailAccount: EmailAccount) {

	@Id
	private var id: Long = _
	
	@OneToOne
	@JoinColumn (name="email_account_id", nullable=false)
	private var emailAccount: EmailAccount = _emailAccount
	
	def this () = this(null)

	
	def setId (id: Long) = this.id = id
	def setEmailAccount (emailAccount: EmailAccount) = this.emailAccount = emailAccount
	
	def getId (): Long = this.id
	def getEmailAccount (): EmailAccount = return emailAccount
	
}