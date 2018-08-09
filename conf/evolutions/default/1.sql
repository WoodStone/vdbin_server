# Bin SCHEMA

# --- !Ups
CREATE TABLE Bin (
  id TEXT PRIMARY KEY,
  timestamp TEXT NOT NULL,
  data BLOB NOT NULL,
  src TEXT NOT NULL,
  ip TEXT NOT NULL
);

# --- !Downs
DROP TABLE Bin
