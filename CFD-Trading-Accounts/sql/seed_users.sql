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
          VALUES (cnt, 300);

		set  username_cnt = username_cnt + 1;
        set  email_cnt = email_cnt + 1;
        set  national_cnt = national_cnt + 1;
        set  cnt=cnt+1;

	END LOOP;
END$$

CALL SEED_USERS();


