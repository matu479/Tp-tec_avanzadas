-- =====================================================================
-- Datos de prueba. Se ejecuta en cada arranque: "ON CONFLICT DO NOTHING"
-- evita duplicados si la base ya tiene los datos (volumen persistente).
--
-- Usuarios (contraseña en texto plano solo acá y en el README):
--   admin@tp.com      / Admin123!
--   clinica@tp.com    / Medico123!     (profesional, clínica médica)
--   cardio@tp.com     / Medico123!     (profesional, cardiología)
--   ana.gomez@tp.com, juan.perez@tp.com, lucia.diaz@tp.com / Paciente123!
-- =====================================================================

-- ---------- USUARIO (1 admin, 2 profesionales, 3 pacientes) ----------
INSERT INTO usuario (id_usuario, email, password, apellido, nombre, rol) VALUES
 (1, 'admin@tp.com',      '$2a$10$cGAv4QXOflEdpj8NulwGkONdjSvpbrYyS.3wsEJAwOxIU3oBJ/2fm', 'Sistema', 'Admin', 'ADMIN'),
 (2, 'clinica@tp.com',    '$2a$10$yQxLSpWu3usfZCqY7/0Ghe/d1r/g3UYhP4XrFkghSlELi6xJDzkGG', 'Fernández', 'Laura',  'PROFESIONAL'),
 (3, 'cardio@tp.com',     '$2a$10$yQxLSpWu3usfZCqY7/0Ghe/d1r/g3UYhP4XrFkghSlELi6xJDzkGG', 'Martínez',  'Carlos', 'PROFESIONAL'),
 (4, 'ana.gomez@tp.com',  '$2a$10$WD639WLZuaDDL2.D.5fBG.qeyqgBh/TQsn.fQcWszB87/aIh8wXW6', 'Gómez', 'Ana',   'PACIENTE'),
 (5, 'juan.perez@tp.com', '$2a$10$WD639WLZuaDDL2.D.5fBG.qeyqgBh/TQsn.fQcWszB87/aIh8wXW6', 'Pérez', 'Juan',  'PACIENTE'),
 (6, 'lucia.diaz@tp.com', '$2a$10$WD639WLZuaDDL2.D.5fBG.qeyqgBh/TQsn.fQcWszB87/aIh8wXW6', 'Díaz',  'Lucía', 'PACIENTE')
ON CONFLICT DO NOTHING;

-- ---------- PROFESIONAL ----------
INSERT INTO profesional (id_profesional, matricula, especialidad) VALUES
 (2, 'MN-10234', 'Clínica médica'),
 (3, 'MN-20871', 'Cardiología')
ON CONFLICT DO NOTHING;

-- ---------- PACIENTE ----------
INSERT INTO paciente (id_paciente, dni, fecha_nacimiento) VALUES
 (4, '30111222', '1983-04-12'),
 (5, '28444555', '1979-09-30'),
 (6, '40777888', '1997-01-21')
ON CONFLICT DO NOTHING;

-- ---------- EXPEDIENTE_CLINICO (uno por paciente) ----------
INSERT INTO expediente_clinico (id_expediente, id_paciente, antecedentes, fecha_creacion, observaciones_generales) VALUES
 (1, 4, 'Hipertensión arterial. Alergia a la penicilina.', '2024-03-01', 'Controles cardiológicos cada 6 meses.'),
 (2, 5, 'Diabetes tipo 2.',                                '2024-05-15', 'Usa tensiómetro y termómetro en su casa.'),
 (3, 6, 'Sin antecedentes relevantes.',                    '2025-02-10', 'Asma leve en la infancia.')
ON CONFLICT DO NOTHING;

-- ---------- CITA (reservadas, canceladas y atendidas) ----------
-- Las fechas son relativas al día en que se crea la base.
INSERT INTO cita (id_cita, id_profesional, id_paciente, estado, motivo, fecha_hora) VALUES
 (1, 3, 4, 'RESERVADA', 'Control de presión',        date_trunc('day', NOW()) + INTERVAL '2 days 10 hours'),
 (2, 2, 5, 'RESERVADA', 'Control de glucemia',       date_trunc('day', NOW()) + INTERVAL '3 days 11 hours'),
 (3, 2, 6, 'CANCELADA', 'Chequeo general',           date_trunc('day', NOW()) + INTERVAL '1 day 9 hours'),
 (4, 3, 4, 'ATENDIDA',  'Palpitaciones',             date_trunc('day', NOW()) - INTERVAL '10 days' + INTERVAL '15 hours'),
 (5, 2, 5, 'ATENDIDA',  'Fiebre y dolor de garganta', date_trunc('day', NOW()) - INTERVAL '5 days' + INTERVAL '16 hours'),
 (6, 3, 6, 'RESERVADA', 'Dolor en el pecho al correr', date_trunc('day', NOW()) + INTERVAL '4 days 12 hours')
ON CONFLICT DO NOTHING;

-- ---------- CONSULTA (de las citas atendidas) ----------
INSERT INTO consulta (id_consulta, id_cita, id_profesional, id_expediente, observaciones, fecha_consulta, indicaciones) VALUES
 (1, 4, 3, 1, 'Ritmo regular. Presión 150/95.', date_trunc('day', NOW()) - INTERVAL '10 days' + INTERVAL '15 hours 20 minutes', 'Enalapril 10 mg por día. Usar oxímetro y pulsera.'),
 (2, 5, 2, 2, 'Faringe enrojecida. 38,2 °C.',   date_trunc('day', NOW()) - INTERVAL '5 days' + INTERVAL '16 hours 15 minutes', 'Ibuprofeno 400 mg cada 8 h. Reposo.')
ON CONFLICT DO NOTHING;

-- ---------- DIAGNOSTICO (0..1 por consulta, S2) ----------
INSERT INTO diagnostico (id_diagnostico, id_consulta, descripcion, fecha) VALUES
 (1, 1, 'Hipertensión arterial estadio 1', CURRENT_DATE - 10),
 (2, 2, 'Faringitis aguda',                CURRENT_DATE - 5)
ON CONFLICT DO NOTHING;

-- ---------- DISPOSITIVO_IOT (1 o 2 por paciente, todos ACTIVO) ----------
INSERT INTO dispositivo_iot (id_dispositivo, id_paciente, tipo, estado, ultima_actividad) VALUES
 (1, 4, 'OXIMETRO',    'ACTIVO', NULL),
 (2, 4, 'PULSERA',     'ACTIVO', NULL),
 (3, 5, 'TENSIOMETRO', 'ACTIVO', NULL),
 (4, 5, 'TERMOMETRO',  'ACTIVO', NULL),
 (5, 6, 'OXIMETRO',    'ACTIVO', NULL)
ON CONFLICT DO NOTHING;

-- ---------- Ajuste de las secuencias (porque los ids se cargaron a mano) ----------
SELECT setval(pg_get_serial_sequence('usuario', 'id_usuario'), COALESCE((SELECT MAX(id_usuario) FROM usuario), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('expediente_clinico', 'id_expediente'), COALESCE((SELECT MAX(id_expediente) FROM expediente_clinico), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('cita', 'id_cita'), COALESCE((SELECT MAX(id_cita) FROM cita), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('consulta', 'id_consulta'), COALESCE((SELECT MAX(id_consulta) FROM consulta), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('diagnostico', 'id_diagnostico'), COALESCE((SELECT MAX(id_diagnostico) FROM diagnostico), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('dispositivo_iot', 'id_dispositivo'), COALESCE((SELECT MAX(id_dispositivo) FROM dispositivo_iot), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('alerta_medica', 'id_alerta'), COALESCE((SELECT MAX(id_alerta) FROM alerta_medica), 0) + 1, false);
