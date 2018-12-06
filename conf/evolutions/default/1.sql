# Bin SCHEMA

# --- !Ups
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE bin (
  uuid UUID DEFAULT uuid_generate_v4(),
  timestamp VARCHAR(19) NOT NULL,
  data text NOT NULL,
  src VARCHAR(32) NOT NULL,
  ip TEXT,
  PRIMARY KEY (uuid)
);

# --- !Downs
DROP TABLE bin
