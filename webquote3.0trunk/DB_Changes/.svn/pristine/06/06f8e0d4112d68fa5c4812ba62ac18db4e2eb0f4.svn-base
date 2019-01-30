/**Check how many records to be processed*/
select count(*) from quote_item t where t.REFERENCE_MARGIN is null and  t.RESALE_MARGIN is not null
and  t.cost is not null and t.QUOTED_PRICE is not null  and stage = 'FINISH';

/**if the quote which cannot find the reference margin setting, set reference quote margin = quoted margin.*/
update quote_item t set REFERENCE_MARGIN =RESALE_MARGIN  where  t.REFERENCE_MARGIN is null and  t.RESALE_MARGIN is not null
and  t.cost is not null and t.QUOTED_PRICE is not null  and stage = 'FINISH';
commit;

/**Check processed is right?*/
select count(*) from quote_item t where t.REFERENCE_MARGIN is null and  t.RESALE_MARGIN is not null
and  t.cost is not null and t.QUOTED_PRICE is not null  and stage = 'FINISH';