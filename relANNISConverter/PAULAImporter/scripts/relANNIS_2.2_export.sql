COPY meta_attribute TO E'e:/db_test/neu/meta_attribute_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY anno_attribute TO E'e:/db_test/neu/anno_attribute_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY anno TO E'e:/db_test/neu/anno_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY rank_anno TO E'e:/db_test/neu/rank_anno_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY rank TO E'e:/db_test/neu/rank_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY doc_2_korp TO E'e:/db_test/neu/doc_2_korp_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY struct TO E'e:/db_test/neu/struct_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY text TO E'e:/db_test/neu/text_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY korpus TO E'e:/db_test/neu/korpus_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY col_rank TO E'e:/db_test/neu/col_rank_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 
COPY collection TO E'e:/db_test/neu/collection_bulk.tab' USING DELIMITERS E'\t' WITH NULL AS 'NULL'; 