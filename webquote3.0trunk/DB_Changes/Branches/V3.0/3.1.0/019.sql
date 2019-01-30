UPDATE "WEBQUOTE"."APP_FUNCTION_ROLE_MAPPING" SET ROLE_ID = (select id from role where name='ROLE_CENTRAL_PRICING') WHERE FUNCTION_ID = (select id from app_function where name='RfqSubmission Create Prospective Customer Button');
UPDATE "WEBQUOTE"."APP_FUNCTION_ROLE_MAPPING" SET ROLE_ID = (select id from role where name='ROLE_CENTRAL_PRICING') WHERE FUNCTION_ID = (select id from app_function where name='WebPromo Create Prospective Customer Button');
COMMIT;
