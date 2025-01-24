CREATE TABLE trainer (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE location (
    id VARCHAR(50) PRIMARY KEY,
    franchise VARCHAR(2) NOT NULL,
    room VARCHAR(4) NOT NULL,
    slot VARCHAR(15) NOT NULL
);

CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    remaining_usage INTEGER NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);

CREATE TABLE purchase (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customer(id),
    payment_complete BOOLEAN NOT NULL,
    package_status TEXT NOT NULL,
    description TEXT,
    lesson_count INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    amount_due DECIMAL(10,2) NOT NULL
);

CREATE TABLE appointment (
    id SERIAL PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    customer_id INTEGER NOT NULL REFERENCES customer(id),
    trainer_id INTEGER REFERENCES trainer(id),
    location_id VARCHAR(50) REFERENCES location(id),
    appointment_status TEXT NOT NULL,
    purchase_id INTEGER NOT NULL REFERENCES purchase(id),
    description TEXT
);

CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    purchase_id INTEGER NOT NULL REFERENCES purchase(id),
    payment_date DATE NOT NULL,                      
    amount DECIMAL(10, 2) NOT NULL,                  
    payment_method VARCHAR(50) NOT NULL,             
    total_paid_amount DECIMAL(10, 2) NOT NULL,       
    amount_due DECIMAL(10, 2) NOT NULL,
    customer_id INTEGER NOT NULL REFERENCES customer(id)         
);