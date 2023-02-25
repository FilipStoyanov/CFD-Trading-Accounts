use cfd;
DELIMITER $$
CREATE PROCEDURE SEED_POSITIONS()
BEGIN
    declare cnt INT;
    declare user_cnt INT;
    declare instrument_cnt INT;
    set cnt = 1;
    set user_cnt = 1;
    set instrument_cnt = 1;
    loop_label:  LOOP
    		IF  cnt > 20 THEN
    			LEAVE  loop_label;
    		END  IF;
    		INSERT INTO
                  account_positions(user_id, instrument_id, quantity, type, buy_price, sell_price)
                  VALUES (user_cnt, FLOOR(RAND()*13 + 1), FLOOR(RAND()*1000 + 1) , "LONG", RAND() * (500 - 100) + 100, FLOOR(RAND()*10 + 1));
            set user_cnt = user_cnt + 1;
            set cnt = cnt + 1;
    END LOOP;

    set cnt = 1;
    set user_cnt = 1;
END$$

CALL SEED_POSITIONS();