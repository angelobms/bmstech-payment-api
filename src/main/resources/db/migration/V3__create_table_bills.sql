CREATE TABLE payment.bills (
    id UUID PRIMARY KEY,
    payment_date DATE,
    due_date DATE NOT NULL,
    amount FLOAT NOT NULL,
    description VARCHAR NOT NULL,
    situation VARCHAR
);