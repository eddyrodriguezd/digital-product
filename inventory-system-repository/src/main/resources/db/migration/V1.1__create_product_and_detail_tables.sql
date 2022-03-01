--Create 'product' table
CREATE TABLE product (
    product_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    sku VARCHAR(20) NOT NULL,
    description VARCHAR(100) NOT NULL,
    detail_id UUID NOT NULL,
    PRIMARY KEY (product_id)
);

--Create 'product_detail' table
CREATE TABLE product_detail (
    product_detail_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    visible BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (product_detail_id)
);

--Add foreign key between 'product' and 'product_detail' tables
ALTER TABLE product ADD CONSTRAINT fk_product_product_detail
	FOREIGN KEY (detail_id)
	REFERENCES product_detail (product_detail_id);