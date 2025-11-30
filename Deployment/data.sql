-- init.sql

-- 1. Insertar Roles (usando snake_case estándar de Hibernate)
INSERT INTO rol (rol_id, rol_name) VALUES (1, 'ADMIN') ON CONFLICT (rol_id) DO NOTHING;
INSERT INTO rol (rol_id, rol_name) VALUES (2, 'EMPLOYEE') ON CONFLICT (rol_id) DO NOTHING;

-- Transacción CTE para crear tienda, usuario y trabajador vinculados
WITH new_store AS (
    INSERT INTO store (name, address, daily_fine) 
    VALUES ('Tienda Principal', 'Av. Siempre Viva 123', 1000)
    RETURNING store_id
),
new_user AS (
    INSERT INTO users (name, mail) 
    VALUES ('Benja', 'benja@gmail.com')
    RETURNING user_id
),
new_worker AS (
    -- AQUI USAMOS EL ID FIJO QUE PUSIMOS EN EL JSON DE KEYCLOAK
    INSERT INTO worker (user_id, password, store_id, keycloak_id)
    SELECT 
        u.user_id, 
        '12345678', 
        s.store_id,
        'a1b2c3d4-e5f6-7890-1234-56789abcdef0' -- <--- MISMO UUID QUE EN EL JSON
    FROM new_user u, new_store s
    RETURNING user_id
)
-- 5. Insertar en la tabla intermedia 'worker_rol'
-- Nombres generados por Hibernate: worker_entity_user_id y rol_rol_id
INSERT INTO worker_rol (worker_entity_user_id, rol_rol_id)
SELECT w.user_id, r.rol_id
FROM new_worker w
CROSS JOIN rol r
WHERE r.rol_id IN (1, 2);