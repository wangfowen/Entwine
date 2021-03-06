# --- !Ups
CREATE TABLE User(
  userId BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255),
  firstName VARCHAR(255),
  lastName VARCHAR(255),
  createdDate BIGINT NOT NULL,
  joinedDate BIGINT,

  PRIMARY KEY (userId)
);

CREATE TABLE Contact(
  userId BIGINT NOT NULL,
  contactId BIGINT NOT NULL,

  CONSTRAINT pk_Contact PRIMARY KEY (userId, contactId),
  FOREIGN KEY (userId) REFERENCES User(userId),
  FOREIGN KEY (contactId) REFERENCES User(userId)
);

CREATE TABLE Event(
  eventId BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  location VARCHAR(255) NOT NULL,
  ownerId BIGINT NOT NULL,
  createdDate BIGINT NOT NULL,

  PRIMARY KEY (eventId),
  FOREIGN KEY (ownerId) REFERENCES User(userId)
);

CREATE TABLE Participation(
  participationId BIGINT NOT NULL AUTO_INCREMENT,
  status INT NOT NULL,
  role INT NOT NULL,
  participantId BIGINT NOT NULL,
  eventId BIGINT NOT NULL,
  respondedDate BIGINT,

  PRIMARY KEY (participationId),
  FOREIGN KEY (participantId) REFERENCES User(userId),
  FOREIGN KEY (eventId) REFERENCES Event(eventId)
);

CREATE TABLE TimeBlock(
  timeBlockId BIGINT NOT NULL AUTO_INCREMENT,
  participationId BIGINT NOT NULL,
  startTime BIGINT NOT NULL,
  endTime BIGINT NOT NULL,

  PRIMARY KEY (timeBlockId),
  FOREIGN KEY (participationId) REFERENCES Participation(participationId)
);


# --- !Downs
DROP TABLE User;

DROP TABLE Contact;

DROP TABLE Event;

DROP TABLE Participation;

DROP TABLE TimeBlock;