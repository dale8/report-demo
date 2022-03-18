INSERT INTO car
      (id, make, model, reg_plate)
VALUES(1, 'Kon', 'Plotka', 'KR 75TM'),
      (2, 'Ogier', 'Tulipan', 'KR 83TL');


INSERT INTO client
      (id, phone)
VALUES(1, '+48500123456'),
      (2, '+48500456789');


INSERT INTO driver
      (id, "name", driver_license, date_started, date_finished, car)
VALUES(1, 'Geralt z Rivii', '42222/12/0118', '2018-01-01 00:00:00.000', NULL, 1),
      (2, 'Jaskier', '43333/11/0229', '2019-01-01 00:00:00.000', NULL, 2);


INSERT INTO "order"
    (id, time_accepted, time_car_assigned, time_car_arrived, time_ride_started, time_ride_finished, route_start, route_finish, driver, client)
VALUES
    (1, '2022-03-16 15:15:54.000', '2022-03-16 15:16:54.000', '2022-03-16 15:18:54.000', '2022-03-16 15:19:54.000', '2022-03-16 15:25:54.000', 'Oxenfurt', 'Wyzima', 2, 1),
    (2, '2022-03-16 15:22:16.000', '2022-03-16 15:23:16.000', '2022-03-16 15:25:16.000', '2022-03-16 15:26:16.000', '2022-03-16 15:32:16.000', 'Sodden', 'Kaer Morhen', 1, 2),
    (3, '2022-03-16 15:29:26.000', '2022-03-16 15:30:26.000', '2022-03-16 15:32:26.000', '2022-03-16 15:33:26.000', '2022-03-16 15:39:26.000', 'Dol Blathanna', 'Vengerberg', 1, 1);
