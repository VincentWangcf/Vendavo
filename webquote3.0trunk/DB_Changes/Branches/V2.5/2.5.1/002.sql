alter table quote_item add ATTACHMENT_FLAG char(5) default '00000' not null;
alter table quote_item_HIS add ATTACHMENT_FLAG char(5) ;

COMMENT ON COLUMN quote_item.ATTACHMENT_FLAG IS 
'1 means has attachment,0 means no,and from left to right represents the value for all type of attachments
(1.BMT_ATTACHMENT,QC_ATTACHMENT,PM_ATTACHMENT,REFRESH_ATTACHMENT,ITEM_ATTACHMENT)';


create or replace PROCEDURE  updateAttachmentFlag
IS
 oldAttachment char(5);
 newAttachment char(5);
 quoteItemId number;
BEGIN
     -- update type RFQ Item Attachment
     for idx in(select * from attachment t where t.type  = 'RFQ Item Attachment' and  t.quote_item_id is not null) loop
        quoteItemId := idx.quote_item_id;
        select ATTACHMENT_FLAG into oldAttachment from quote_item t1 where t1.id = quoteItemId;
        if oldAttachment = '' or oldAttachment is null then
          oldAttachment := '00000';
        end if;
        newAttachment := substr(oldAttachment,1,4) || '1'; 
        update quote_item set ATTACHMENT_FLAG = newAttachment where id = quoteItemId;
     end loop;
     
      -- update type Refresh Attachment
     for idx in(select * from attachment t where t.type  = 'Refresh Attachment' and  t.quote_item_id is not null) loop
        quoteItemId := idx.quote_item_id;
        select ATTACHMENT_FLAG into oldAttachment from quote_item t1 where t1.id = quoteItemId;
        if oldAttachment = '' or oldAttachment is null then
          oldAttachment := '00000';
        end if;
        newAttachment := substr(oldAttachment,1,3) || '1' || substr(oldAttachment,5,1);
        update quote_item set ATTACHMENT_FLAG = newAttachment where id = quoteItemId;
     end loop;
     
       -- update type PM Attachment
      for idx in(select * from attachment t where t.type  = 'PM Attachment' and  t.quote_item_id is not null) loop
        quoteItemId := idx.quote_item_id;
        select ATTACHMENT_FLAG into oldAttachment from quote_item t1 where t1.id = quoteItemId;
        if oldAttachment = '' or oldAttachment is null then
          oldAttachment := '00000';
        end if;
        newAttachment := substr(oldAttachment,1,2) || '1' || substr(oldAttachment,4,2);
        update quote_item set ATTACHMENT_FLAG = newAttachment where id = quoteItemId;
     end loop;
     
       -- update type PM Attachment
      for idx in(select * from attachment t where t.type  = 'QC Attachment' and  t.quote_item_id is not null) loop
        quoteItemId := idx.quote_item_id;
        select ATTACHMENT_FLAG into oldAttachment from quote_item t1 where t1.id = quoteItemId;
        if oldAttachment = '' or oldAttachment is null then
          oldAttachment := '00000';
        end if;
        newAttachment := substr(oldAttachment,1,1) || '1' || substr(oldAttachment,3,3);
        update quote_item set ATTACHMENT_FLAG = newAttachment where id = quoteItemId;
     end loop;
     
       -- update type BMT Attachment
      for idx in(select * from attachment t where t.type  = 'BMT Attachment' and  t.quote_item_id is not null) loop
        quoteItemId := idx.quote_item_id;
        select ATTACHMENT_FLAG into oldAttachment from quote_item t1 where t1.id = quoteItemId;
        if oldAttachment = '' or oldAttachment is null then
          oldAttachment := '00000';
        end if;
        newAttachment :=  '1' || substr(oldAttachment,2,4);
        update quote_item set ATTACHMENT_FLAG = newAttachment where id = quoteItemId;
     end loop;
     
     
     
    commit;
  
END updateAttachmentFlag;
