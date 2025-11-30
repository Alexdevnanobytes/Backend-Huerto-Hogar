-- =========================
-- CATEGORÍAS
-- =========================
INSERT INTO categorias (nombre, codigo) VALUES
('Verduras', 'VR'),
('Frutas', 'FR'),
('Productos orgánicos', 'PO');

-- =========================
-- PRODUCTOS
-- =========================
INSERT INTO productos (nombre, descripcion, precio, stock, codigo, imagen_url, categoria_id) VALUES
('Pimentón rojo',
 'Pimentón fresco y crujiente, ideal para ensaladas',
 1200,
 100,
 'VR01',
 '/img/pimenton-rojo.png',
 1),

('Pimentón amarillo',
 'Pimentón fresco y crujiente, ideal para ensaladas',
 1000,
 80,
 'VR02',
 '/img/pimenton-amarillo.png',
 1),

('Pimentón verde',
 'Pimentón fresco y crujiente, ideal para ensaladas',
 500,
 120,
 'VR03',
 '/img/pimenton-verde.png',
 1),

('Lechuga hidropónica',
 'Lechuga fresca y crujiente para ensaladas saludables',
 300,
 150,
 'VR04',
 '/img/lechuga-hidroponica.png',
 1),

('Betarraga 3un',
 'Betarraga fresca y dulce, perfecta para ensaladas',
 700,
 90,
 'VR05',
 '/img/betarraga.png',
 1),

('Miel 1KG',
 'Miel fresca y dulce, perfecta para endulzar tus platos',
 5000,
 40,
 'PO01',
 '/img/miel.png',
 3),

('Plátano KG',
 'Plátanos frescos y dulces, perfectos para batidos',
 1250,
 100,
 'FR01',
 '/img/platano.png',
 2),

('Quinoa KG',
 'Quinoa fresca y nutritiva, perfecta para ensaladas',
 6500,
 60,
 'PO02',
 '/img/quinoa.png',
 3),

('Naranja KG',
 'Naranjas frescas y jugosas, perfectas para el desayuno',
 850,
 110,
 'FR02',
 '/img/naranja.png',
 2),

('Leche 1L',
 'Leche fresca y cremosa, perfecta para el desayuno',
 1050,
 70,
 'PO03',
 '/img/leche.png',
 3),

('Manzana KG',
 'Manzanas frescas y crujientes, perfectas para el desayuno',
 850,
 95,
 'FR03',
 '/img/manzana.png',
 2);
-- =========================
-- ROLES
-- =========================
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema'),
('VENDEDOR', 'Puede gestionar productos y órdenes'),
('CLIENTE', 'Cliente que compra en la tienda');
