# --- !Ups
CREATE TABLE User(
  userId INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  firstName VARCHAR(255) NOT NULL,
  lastName VARCHAR(255) NOT NULL,
  createdDate DATETIME NOT NULL,
  joinedDate DATETIME,
  UNIQUE (userId)
);


# --- !Downs
DROP TABLE User;