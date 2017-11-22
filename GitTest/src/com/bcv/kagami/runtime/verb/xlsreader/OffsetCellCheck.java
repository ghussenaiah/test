package com.bcv.kagami.runtime.verb.xlsreader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author Leonid Vysochyn
 */
public interface OffsetCellCheck {
    Object getValue();
    void setValue(Object value);
    short getOffset();
    void setOffset(short offset);
    boolean isCheckSuccessful(Cell cell);
    boolean isCheckSuccessful(Row row);
}
