ALTER TABLE products
    ADD CONSTRAINT chk_products_status
        CHECK (status IN ('FOR_SALE', 'STOP_SALE', 'OUT_OF_STOCK'));
