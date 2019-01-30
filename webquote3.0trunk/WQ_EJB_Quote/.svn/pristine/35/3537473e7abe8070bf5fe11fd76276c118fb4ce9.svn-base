package com.avnet.emasia.webquote.rowdata;

import java.io.InputStream;
import java.util.List;

import com.avnet.emasia.webquote.rowdata.datasource.ResolveInfo;

public interface RowDataConverter<T extends Bean> {
	List<T> convertAndValidate (InputStream is, FileType type, ResolveInfo resovleInfo, Class<T> clazz);
}
