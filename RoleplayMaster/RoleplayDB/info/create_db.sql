CREATE DATABASE IF NOT EXISTS roleplaydb DEFAULT CHARACTER SET = "UTF8";

CONNECT roleplaydb;

CREATE TABLE IF NOT EXISTS SOLOROLEPLAY (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL UNIQUE,
  STATUS VARCHAR(20) NOT NULL,
  OWNER VARCHAR(255) NOT NULL,
  SOLOTEXT MEDIUMTEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  CREATIONDATE DATETIME, 
  LASTUPDATE DATETIME
);

CREATE TABLE IF NOT EXISTS USERPERSIST (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  USERID VARCHAR(255) NOT NULL UNIQUE,
  ROLE VARCHAR(32),
  DATA TEXT NOT NULL,
  CREATIONDATE DATETIME,
  PERSISTTIME DATETIME NOT NULL
);

CREATE USER roleplaydbusr IDENTIFIED BY 'geheim';

GRANT ALL ON roleplaydb.* TO roleplaydbusr@'%';


-- INSERT INTO SOLOROLEPLAY(ID, NAME, STATUS, OWNER, SOLOTEXT, CREATIONDATE, LASTUPDATE) 
-- 	VALUES (1,'Beispiel','PUBLISHED','feri',
-- 	'TITEL:Beispiel\r\n[BEGINN]\r\nLos gehts.\r\nWEITER:[1]\r\n[1]\r\nHier fehlt noch Text.\r\nJANEINENTSCHEIDUNG:\r\nM�chtest du aufh�ren?\r\nJA:[ENDE]\r\nNEIN:[1]\r\n[ENDE]\r\nGeschafft!\r\n\r\n',
-- '2017-09-23 12:59:58','2017-09-23 13:00:40');

-- UPDATE SOLOROLEPLAY dest, (SELECT * FROM SOLOROLEPLAY where id=54) src SET dest.SOLOTEXT = src.SOLOTEXT where dest.ID=50;
