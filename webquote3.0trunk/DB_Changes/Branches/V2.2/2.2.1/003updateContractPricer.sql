-- if have CONTRACT_PRICE_UK1 CONSTRAINT, run this sql
ALTER TABLE CONTRACT_PRICE DROP CONSTRAINT "CONTRACT_PRICE_UK1" ;
-- if have CONTRACT_PRICE_UK1 index, run this sql
DROP index CONTRACT_PRICE_UK1;
ALTER TABLE  CONTRACT_PRICE ADD CONSTRAINT "CONTRACT_PRICE_UK1" UNIQUE ("MATERIAL_ID","COST_INDICATOR_ID", "SOLD_CUSTOMER_ID", "END_CUSTOMER_ID", "BIZ_UNIT_ID", "QUOTATION_EFFECTIVE_DATE");
