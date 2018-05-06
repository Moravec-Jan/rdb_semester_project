
# trigger je k ničemu, protože se provede rollback, neni tak možne zaroven přidat hodnotu a zrusit insert


DROP TRIGGER brana_validation;

delimiter $$
CREATE TRIGGER brana_validation BEFORE INSERT ON brana
       FOR EACH ROW
       BEGIN
	       IF (NEW.longtitude < 35 || NEW.longtitude > 70 || NEW.latitude < -25 || NEW.latitude > 105) THEN
           BEGIN
			   UPDATE Nevalidni_zaznamy SET pocet = pocet + 1 WHERE id = 3; 
               SIGNAL SQLSTATE '45000';
		   END;
		   END IF;
           
	   END;$$
       
    
   
INSERT INTO Brana VALUES('1234567890', '16.81', '42.17', '10.2', 'Satellite');



		       
		       