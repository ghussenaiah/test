package com.bcv.kagami.runtime.verb.xlsreader;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.bcv.kagami.runtime.verb.xlsreader.XLSReadStatus;
import com.bcv.kagami.runtime.verb.xlsreader.XLSSheetReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Interface to read and parse excel file
 * @author Leonid Vysochyn
 */
public interface XLSReader {
    XLSReadStatus read(InputStream inputXLS, Map beans,String offset) throws IOException, InvalidFormatException;
    void setSheetReaders(Map sheetReaders);
    Map getSheetReaders();
    void addSheetReader( String sheetName, XLSSheetReader reader);
    void addSheetReader(XLSSheetReader reader);
    public void addSheetReader(Integer idx, XLSSheetReader reader);
}
