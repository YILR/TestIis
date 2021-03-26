DROP TABLE IF EXISTS department;

CREATE TABLE department
(ID int PRIMARY KEY AUTO_INCREMENT,
DepCode varchar (20) NOT NULL,
DepJob varchar (100) NOT NULL,
Description varchar (255) NOT NULL,
UNIQUE (DepCode, DepJob));

INSERT INTO department(DepCode, DepJob, Description)
VALUES ('105', 'Senior', 'Java'),
('205', 'Junior', 'Tester'),
('305', 'Middle', 'Java');