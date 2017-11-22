package com.bcv.kagami.runtime.verb.xlsreader;

import org.apache.poi.ss.usermodel.Row;

import com.bcv.kagami.runtime.verb.xlsreader.OffsetCellCheck;
import com.bcv.kagami.runtime.verb.xlsreader.XLSRowCursor;

/**
 * @author Leonid Vysochyn
 */
public interface OffsetRowCheck {
    int getOffset();
    void setOffset(int offset);
    boolean isCheckSuccessful(Row row);
    boolean isCheckSuccessful(XLSRowCursor cursor);
    void addCellCheck(OffsetCellCheck cellCheck);
}
