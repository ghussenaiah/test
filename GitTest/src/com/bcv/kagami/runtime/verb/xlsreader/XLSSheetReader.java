package com.bcv.kagami.runtime.verb.xlsreader;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import com.bcv.kagami.runtime.verb.xlsreader.XLSBlockReader;
import com.bcv.kagami.runtime.verb.xlsreader.XLSReadStatus;

/**
 * Interface to parse XLS sheet 
 * @author Leonid Vysochyn
 */
public interface XLSSheetReader {
    /**
     * Method to read data from excel sheet and populate objects
     * @param sheet - {@link Sheet} object
     * @param beans - {@link Map} of beans to populate
     * @return {@link XLSReadStatus} object with info about read status
     */
    XLSReadStatus read(Sheet sheet, Map beans);
    XLSReadStatus read1(Sheet sheet, Map beans,String offset);

    List getBlockReaders();
    void setBlockReaders(List blockReaders);
    void addBlockReader(XLSBlockReader blockReader);

    String getSheetName();
    void setSheetName(String sheetName);

    int getSheetIdx();
    void setSheetIdx(int idx);

}
