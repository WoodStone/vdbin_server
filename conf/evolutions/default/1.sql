# Bin SCHEMA

# --- !Ups
CREATE TABLE Bin (
  id TEXT PRIMARY KEY,
  timestamp TEXT NOT NULL,
  data TEXT NOT NULL,
  src TEXT NOT NULL
);

# --- !Downs
DROP TABLE Bin
