--Create function to set 'status_date' for new products
CREATE OR REPLACE FUNCTION created_at_product_detail_fn()
RETURNS TRIGGER AS $$
BEGIN
    NEW.created_at = now();
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';
--Apply the trigger
CREATE TRIGGER created_at_product_detail BEFORE INSERT ON product_detail FOR EACH ROW EXECUTE PROCEDURE created_at_product_detail_fn();

--Create function to set 'updated_date' for modified products
CREATE OR REPLACE FUNCTION updated_at_product_detail_fn()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_date = now();
    RETURN NEW;
END;
$$ language 'plpgsql';
--Apply the trigger
CREATE TRIGGER updated_at_product_detail BEFORE UPDATE ON product_detail FOR EACH ROW EXECUTE PROCEDURE updated_at_product_detail_fn();