-- init.sql

-- 1. Insertar Roles (Si ya existen, no hace nada)
INSERT INTO rol (rol_id, rol_name) VALUES (1, 'ADMIN') ON CONFLICT (rol_id) DO NOTHING;
INSERT INTO rol (rol_id, rol_name) VALUES (2, 'EMPLOYEE') ON CONFLICT (rol_id) DO NOTHING;

-- Ajustar la secuencia para evitar errores futuros de ID
SELECT setval('rol_rol_id_seq', (SELECT MAX(rol_id) FROM rol));

-- 2. Insertar Tienda
WITH new_store AS (
    INSERT INTO store (store_id, name, address, daily_fine) 
    VALUES (1, 'Tienda Principal', 'Av. Siempre Viva 123', 1000)
    ON CONFLICT (store_id) DO NOTHING
    RETURNING store_id
)
SELECT count(*) FROM new_store; -- Dummy select para cerrar el WITH

SELECT setval('store_store_id_seq', (SELECT MAX(store_id) FROM store));

-- 3. Insertar Usuario
WITH new_user AS (
    INSERT INTO users (user_id, name, mail) 
    VALUES (1, 'Benja', 'benja@gmail.com')
    ON CONFLICT (user_id) DO NOTHING
    RETURNING user_id
)
SELECT count(*) FROM new_user;

SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));

-- 4. Insertar Trabajador
INSERT INTO worker (user_id, password, store_id, keycloak_id)
VALUES (1, '12345678', 1, 'a1b2c3d4-e5f6-7890-1234-56789abcdef0')
ON CONFLICT (user_id) DO NOTHING;

-- 5. Asignar Roles al Trabajador (Relación ManyToMany)
INSERT INTO worker_rol (worker_entity_user_id, rol_rol_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO worker_rol (worker_entity_user_id, rol_rol_id) VALUES (1, 2) ON CONFLICT DO NOTHING;