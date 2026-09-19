-- uk_product_options_product_name 이 만드는 (product_id, name) B-tree 인덱스가
-- 선두 컬럼 product_id 조회를 감당하므로 같은 컬럼의 단일 인덱스를 둘 이유가 없다
DROP INDEX IF EXISTS idx_product_options_product_id;
