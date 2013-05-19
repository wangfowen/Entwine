package us.entwine.models

case class Event(
        id: Long,
        name: String,
        description: String,
        invitees: List[User])