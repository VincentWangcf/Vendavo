SET DEFINE OFF;
Insert into APP_PROPERTY (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('TEMPLATE_PATH','Excel template path, ','/app/eap70/webquote/share/template_files','1');
Insert into APP_PROPERTY (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('CACHE_PAGE_SIZE','Page Size For Cache in pagination','10','1');
Insert into APP_PROPERTY (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('LOCALE_RELOAD_ja',null,'true','0');
Insert into APP_PROPERTY (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('LOCALE_RELOAD_en',null,'true','0');
Insert into APP_PROPERTY (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('LOCALE_RELOAD',null,'true','0');
Insert into APP_PROPERTY (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('LOCALE_RELOAD_TIME','Check  reload time for locale','30000','1');
Insert into APP_PROPERTY (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('LOCALE_RELOAD_ko',null,'true','0');
Insert into APP_PROPERTY (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('LOCALE_RELOAD_zh',null,'false','0');
---add a path for apach poi ,by Damon
---Insert into app_property (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('POI_TEMP_PATH_DIR','apche poi uses this folder for generating excel report','/app/eap62/webquote/poitempdir','1');
--try to update  /app/eap70/webquote/poitempdir
Insert into app_property (PROP_KEY,PROP_DESC,PROP_VALUE,PROP_RESERVED) values ('POI_TEMP_PATH_DIR','apche poi uses this folder for generating excel report','/app/eap70/webquote/poitempdir','1');

commit;
