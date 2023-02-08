CREATE TABLE `emailAuth` (
    `id` int(11) NOT NULL,
    `email` varchar(255) NOT NULL,
    `verification_code` varchar(6) NOT NULL,
    `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `emailAuth` (`id`, `email`, `verification_code`, `timestamp`) VALUES
    (1, 'expired_verification_code@yunnanhistory.com', '114514', '2023-02-08 15:00:00');

-- --------------------------------------------------------

CREATE TABLE `emailTimer` (
    `id` int(11) NOT NULL,
    `email` varchar(255) NOT NULL,
    `action` varchar(50) NOT NULL,
    `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

CREATE TABLE `user` (
    `id` int(11) NOT NULL,
    `username` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `email` varchar(255) NOT NULL,
    `user_rights` int(11) NOT NULL DEFAULT 0 COMMENT '0=user,1=admin',
    `lang` varchar(3) NOT NULL DEFAULT 'zh',
    `points` int(5) NOT NULL DEFAULT 0,
    `register_type` int(11) NOT NULL DEFAULT 0 COMMENT '0=email,1=google'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `user` (`id`, `username`, `password`, `email`, `user_rights`, `lang`, `points`, `register_type`) VALUES
     (1, 'test_data', 'test_data', 'email_already_registered@yunnanhistory.com', 0, 'zh', 0, 0),
     (2, 'test_data', 'test_data', 'google_registered@yunnanhistory.com', 0, 'zh', 0, 1),
     (3, 'test_data', 'test_data', 'invalid_email@yunnanhistory.c', 0, 'zh', 0, 0);

-- --------------------------------------------------------

ALTER TABLE `emailAuth`
    ADD PRIMARY KEY (`id`);

ALTER TABLE `emailTimer`
    ADD PRIMARY KEY (`id`);

ALTER TABLE `user`
    ADD PRIMARY KEY (`id`);

ALTER TABLE `emailAuth`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=273;

ALTER TABLE `emailTimer`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=217;

ALTER TABLE `user`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=336;