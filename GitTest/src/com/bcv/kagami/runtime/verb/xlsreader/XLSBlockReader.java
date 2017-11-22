package com.bcv.kagami.runtime.verb.xlsreader;

import java.util.Collection;
import java.util.Map;

import com.bcv.kagami.runtime.verb.xlsreader.XLSReadStatus;
import com.bcv.kagami.runtime.verb.xlsreader.XLSRowCursor;

/**
 * Interface to read block of excel rows
 * @author Leonid Vysochyn
 */
public interface XLSBlockReader {
    XLSReadStatus read(XLSRowCursor cursor, Map beans);
    XLSReadStatus read1(XLSRowCursor cursor, Map beans,String offset,Collection item);
    XLSReadStatus read3(XLSRowCursor cursor, Map beans,String offset,Collection item);
    
    int getStartRow();

    void setStartRow(int startRow);

    int getEndRow();

    void setEndRow(int endRow);

}
