use cfd;
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
        SET name = ELT(name_cnt, "Crude-Oil-01March23", "Gold", "EUR/USD", "USD/JPY", "Tesla", "Apple", "Meta Platforms", "Netflix", "Amazon", "Alphabet(Class C)",
        "Virgin Galactic","Smile Direct Club","Airbnb");
        SET fullname = ELT(fullname_cnt, "Crude Oil expiring 01 March 2023", "Gold", "EUR/USD", "USD/JPY", "Tesla Inc.",
        "Apple Inc", "Meta Platforms Inc.", "Netflix Inc.", "Amazon.com Inc", "Alphabet Inc", "Virgin Galactic Holdings,Inc.", "Smile Direct Club, Inc.",
        "Airbnb Inc.");
        SET market_name = ELT(market_name_cnt, "Commodities", "Commodities", "Forex", "Forex", "NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ", "NYSE","NASDAQ", "NASDAQ");
        SET min_quantity = ELT(min_quantity_cnt, 0.1, 0.1, 500, 500, 0.1, 0.1, 0.1, 0.1, 0.2, 0.1, 0.1, 0.1, 0.1);
        SET leverage = ELT(leverage_cnt, 0.1, 0.05, 0.333, 0.333, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2);
        SET ticker = ELT(ticker_cnt, "Crude-Oil-01March23", "Gold", "EUR/USD", "USD/JPY", "Tesla", "Apple", "Meta Platforms","Netflix", "Amazon", "Alphabet(Class C)", "Virgin Galactic",
        "Smile Direct Club", "Airbnb Inc");
        INSERT INTO
          `instruments` (name, fullname, ticker, market_name, min_quantity, leverage)
          VALUES (name, fullname, ticker, market_name, min_quantity, leverage);

        INSERT INTO
          `instrument_prices` (instrument_id, buy, sell)
          VALUES (cnt, RAND() * (500 - 50) + 50, RAND() * (500 - 50) + 50);

		set  name_cnt = name_cnt + 1;
        set  fullname_cnt = fullname_cnt + 1;
		set  market_name_cnt = market_name_cnt + 1;
        set min_quantity_cnt = min_quantity_cnt + 1;
        set leverage_cnt = leverage_cnt + 1;
        set ticker_cnt = ticker_cnt + 1;
        set  cnt=cnt+1;

	END LOOP;
END$$

CALL SEED_INSTRUMENTS();

