CREATE TABLE car (
	id bigint NOT NULL,
	make varchar NOT NULL,
	model varchar NOT NULL,
	reg_plate varchar NOT NULL,
    CONSTRAINT car_pk PRIMARY KEY (id)
);

CREATE TABLE client (
	id bigint NOT NULL,
	phone varchar NULL,
	CONSTRAINT client_pk PRIMARY KEY (id)
);

CREATE TABLE driver (
	id bigint NOT NULL,
	"name" varchar NOT NULL,
	driver_license varchar NOT NULL,
	date_started timestamp NOT NULL,
	date_finished timestamp NULL,
	car bigint NOT NULL,
	CONSTRAINT driver_pk PRIMARY KEY (id)
);

CREATE TABLE "order" (
	id bigint NOT NULL,
	time_accepted timestamp NOT NULL,
	time_car_assigned timestamp NULL,
	time_car_arrived timestamp NULL,
	time_ride_started timestamp NULL,
	time_ride_finished timestamp NULL,
	route_start varchar NULL,
	route_finish varchar NULL,
	driver bigint NULL,
	client bigint NULL,
	CONSTRAINT order_pk PRIMARY KEY (id)
);