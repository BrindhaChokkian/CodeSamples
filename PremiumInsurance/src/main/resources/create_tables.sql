DROP TABLE premium_contract;
DROP TABLE premium_insurance_history;
DROP TABLE premium_month_report;

CREATE TABLE premium_contract (
  id int NOT NULL AUTO_INCREMENT,
  contract_id int DEFAULT NULL,
  current_premium_amount int DEFAULT NULL, 
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,  
   PRIMARY KEY (id)
);

CREATE TABLE premium_insurance_history (
  id int NOT NULL AUTO_INCREMENT,
  contract_id int DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  premium int DEFAULT NULL,
  date date DEFAULT NULL,  
   PRIMARY KEY (id)
);

CREATE TABLE premium_month_report (
  id int NOT NULL AUTO_INCREMENT,
  contract_id int DEFAULT NULL,
  active_flag varchar(255) DEFAULT NULL,
  current_premium_amount int DEFAULT NULL,  
  premium_month date DEFAULT NULL,
  agwp int DEFAULT NULL,
  egwp int DEFAULT NULL,  
   PRIMARY KEY (id)
);
