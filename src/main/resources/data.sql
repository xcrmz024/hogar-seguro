-- initial admin (demo):
INSERT IGNORE INTO users (username, password, role) VALUES
('admin', '$2a$10$jrmPeaXWaV/LEP0cF2Fn9ethtTlqw8xvC.TTneuzbuBxIlInJD1Y2', 'ROLE_ADMIN');

-- initial donations:
INSERT IGNORE INTO donations (donor_name, email, amount, donation_date, message) VALUES
('Carlos Ramírez', 'carlos.ramirez@email.com', 250.00, '2026-03-01 10:30:00', 'Gracias por su gran labor.'),
('Ana López', 'ana.lopez@email.com', 500.50, '2026-03-02 14:45:00', 'Con mucho cariño para apoyar la causa.'),
('María González', 'maria.gonzalez@email.com', 1000.00, '2026-03-03 09:15:00', 'Espero que esto ayude a muchos.');


-- initial residents:
INSERT IGNORE INTO residents (resident_name, species, story, photo_url, available, help_type, active) VALUES
('Jairo', 'Cabra', 'Jairo llego al santuario en 2016. Estaba malherido y desnutrido, los primeros días fueron los más difíciles, pues requirio de bastantes meses para llegar a un buen estado de salud. Hoy en día, ¡Jairo es uno de los habitantes más alegres y vivaces del santuario! Le encanta jugar, dormir y comer.', 'imagenes/jairo.jpg', true, 'AMADRINAR', true),
('Ramón', 'Perro', 'Ramón fue rescatado en 2020 cuando apenas era un cachorro. A pesar de haber sido abandonado, con amor y cuidado, creció sano y feliz. Ahora, Ramón es un perro juguetón y lleno de energía. Le encanta correr por el santuario, jugar con sus amigos los gallos y recibir caricias de todos los voluntarios.', 'imagenes/ramon.jpg', true, 'ADOPTAR', true),
('Luz', 'Cerdo', 'Luz llegó al santuario en 2015, y estaba asustada. Necesitó varios meses para tomar confianza con el resto de habitantes, pero hoy en día, Luz es una cerda muy sociable y cariñosa. Disfruta del barro y de las siestas bajo el sol, siendo una de las favoritas entre los visitantes por su dulzura y tranquilidad.', 'imagenes/luz.jpg', true, 'AMADRINAR', true),
('Cascabel', 'Oveja', 'Cascabel fue rescatada en 2019 de una zona peligrosa. Durante meses se mantuvo oculta y temerosa, pero poco a poco fue ganando confianza con los voluntarios. Aunque ahora está más tranquila, todavía no está lista para recibir atención directa.', 'imagenes/cascabel.jpg', false,  'AMADRINAR', true);

-- initial applications:
INSERT IGNORE INTO applications (applicant_name, email, phone_number, message, application_type, created_at, resident_id) VALUES
('Juan Pérez', 'laura.mendez@email.com', '5512345678', 'Me gustaría apoyar económicamente de forma mensual.', 'AMADRINAR', '2026-03-01 11:00:00', 1),
('Jorge Castillo', 'jorge.castillo@email.com', '5523456789', 'Estoy interesado en adoptar y darle un hogar estable.', 'ADOPTAR', '2026-03-02 15:30:00', 2),
('Fernanda Ruiz', 'fernanda.ruiz@email.com', '5534567890', 'Quiero ayudar como voluntaria los fines de semana.', 'VOLUNTARIADO', '2026-03-03 09:00:00', NULL);
