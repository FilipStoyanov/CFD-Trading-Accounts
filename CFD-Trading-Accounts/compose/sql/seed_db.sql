use cfd;
DELIMITER $$
CREATE PROCEDURE SEED_USERS()
BEGIN

	declare username_cnt INT;
    declare cnt INT;
	declare email_cnt  INT;
	declare national_cnt INT;

    declare username VARCHAR(255);
	declare email VARCHAR(255);
	declare national_id VARCHAR(30);

    set cnt = 1;
    set username_cnt = 1;
    set email_cnt = 1;
    set national_cnt = 1;
	loop_label:  LOOP
		IF  cnt > 20 THEN
			LEAVE  loop_label;
		END  IF;
        SET username = ELT(username_cnt, "ivan",
											"peter",
											"dragan",
											"todor",
											"ivan12",
											"george",
											"filip",
											"maria",
											"petya",
											"yordan",
											"petyo",
											"yordan2000",
											"stefan1902",
											"radina",
											"daniela",
											"krasimir",
											"daniela1",
											"lubomir0403",
											"miroslav",
											"nikolay"
										);
        SET email = ELT(email_cnt, 'johndoe@gmail.com', 'janeroe@gmail.com', 'michaelscott@gmail.com', 'jenniferlopez@gmail.com', 'kevinbacon@gmail.com', 'peter@gmail.com', 'richard@abv.bg',
        'tomhanks@gmail.com', 'sarahjones@gmail.com', 'jasonbourne@gmail.com', 'jennifersmith@gmail.com', 'chrisrock@gmail.com', 'jennifergarner@gmail.com', 'mattdamon@gmail.com', 'jenniferaniston@gmail.com', 'bradpitt@gmail.com', 'angelinajolie@gmail.com',
        'benaffleck@gmail.com', 'jenniferlawrence@gmail.com', 'robertdeniro@gmail.com');

        SET national_id = ELT(national_cnt, "8705122358", "9307193842", "8008260890", "7304137089", "8209156087", "9104058219", "7407065180",
        "8902116739", "7807194857", "9001173005", "8605132057", "9408017098", "7910175283", "7605284091", "8302045786", "9209053811", "7504078069", "9007098732", "7708135086", "9101084205");

        INSERT INTO
          users (username, password_hash, email, national_id)
          VALUES (username, "$2a$12$MCGaZ5XoJHEtf5yUTJ5fb.C3I4ohRMLDe6TOqvsoRMRT1JII8RmfG", email, national_id);

        INSERT INTO
          account_cash (user_id, balance)
          VALUES (cnt, 20000);

		set  username_cnt = username_cnt + 1;
        set  email_cnt = email_cnt + 1;
        set  national_cnt = national_cnt + 1;
        set  cnt=cnt+1;

	END LOOP;
END$$


DELIMITER $$ 
CREATE PROCEDURE SEED_INSTRUMENT_TYPES() 
BEGIN 
	declare cnt INT;
    declare instrument_type VARCHAR(255);
	set cnt = 1;
	loop_label: LOOP 
		IF cnt > 5 THEN 
			LEAVE loop_label;
		END IF;
		SET instrument_type = ELT(
			 cnt,
			"stock",
			"index",
			"crypto",
			"currency",
			"commodities"
		);
		INSERT INTO
			instrument_type (name)
			VALUES (instrument_type);
		set cnt = cnt + 1;
	END LOOP;
END$$ 

DELIMITER $$
CREATE PROCEDURE SEED_INSTRUMENTS()
BEGIN

	declare name_cnt INT;
    declare cnt INT;
	DECLARE fullname_cnt  INT;
    declare market_name_cnt INT;
	declare min_quantity_cnt INT;
	declare leverage_cnt INT;
	declare ticker_cnt INT;

    declare name VARCHAR(255);
	declare fullname VARCHAR(255);
    declare market_name VARCHAR(255);
    declare min_quantity DECIMAL(19,2);
    declare leverage DECIMAL(19,2);
    declare ticker VARCHAR(255);

    SET cnt = 1;
    set name_cnt = 1;
    set fullname_cnt = 1;
    set market_name_cnt = 1;
    set min_quantity_cnt = 1;
    set leverage_cnt = 1;
    set ticker_cnt = 1;
	loop_label:  LOOP
		IF  cnt > 13 THEN
			LEAVE  loop_label;
		END  IF;
        SET name = ELT(name_cnt, "Broadcom", "Gold", "EUR/USD", "USD/JPY", "Tesla", "Apple", "Meta Platforms", "Netflix", "Amazon", "Alphabet(Class C)",
        "Virgin Galactic","Smile Direct Club","Airbnb");
        SET fullname = ELT(fullname_cnt, "Broadcom Inc.", "Gold", "EUR/USD", "USD/JPY", "Tesla Inc.",
        "Apple Inc", "Meta Platforms Inc.", "Netflix Inc.", "Amazon.com Inc", "Alphabet Inc", "Virgin Galactic Holdings,Inc.", "Smile Direct Club, Inc.",
        "Airbnb Inc.");
        SET market_name = ELT(market_name_cnt, "Commodities", "Commodities", "Forex", "Forex", "NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ", "NYSE","NASDAQ", "NASDAQ");
        SET min_quantity = ELT(min_quantity_cnt, 2, 1, 10, 7, 5, 4, 0.1, 0.1, 0.2, 2, 3, 0.1, 0.1);
        SET leverage = ELT(leverage_cnt, 0.1, 0.05, 0.333, 0.333, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2);
        SET ticker = ELT(ticker_cnt, "Broadcom", "Gold", "EUR/USD", "USD/JPY", "Tesla", "Apple", "Meta Platforms","Netflix", "Amazon", "Alphabet(Class C)", "Virgin Galactic",
        "Smile Direct Club", "Airbnb Inc");
        INSERT INTO
          `instruments` (name, fullname, ticker, market_name, type_id, min_quantity, leverage)
          VALUES (name, fullname, ticker, market_name, FLOOR(RAND() * 5) + 1, min_quantity, leverage);

        INSERT INTO
          `instrument_prices` (ticker, buy, sell)
          VALUES (ticker, RAND() * (193 - 188) + 188, RAND() * (193 - 188) + 188);

		set  name_cnt = name_cnt + 1;
        set  fullname_cnt = fullname_cnt + 1;
		set  market_name_cnt = market_name_cnt + 1;
        set min_quantity_cnt = min_quantity_cnt + 1;
        set leverage_cnt = leverage_cnt + 1;
        set ticker_cnt = ticker_cnt + 1;
        set  cnt=cnt+1;

	END LOOP;
END$$

CALL SEED_USERS();
CALL SEED_INSTRUMENT_TYPES();
CALL SEED_INSTRUMENTS();

