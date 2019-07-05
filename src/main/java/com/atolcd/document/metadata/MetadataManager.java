package com.atolcd.document.metadata;

import java.io.InputStream;
import java.io.OutputStream;

import com.sun.istack.NotNull;

public interface MetadataManager {
	
	public Metadata parse(@NotNull InputStream inputStream) throws Exception;
	public void write(@NotNull Metadata metadata, @NotNull InputStream inputStream, @NotNull OutputStream outputStream, boolean eraseCorePropertyWithNull, boolean cleanCustomProperties) throws Exception;

}
