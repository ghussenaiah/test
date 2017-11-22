package com.bcv.kagami.runtime.verb.xlsreader;

import com.bcv.kagami.runtime.verb.xlsreader.OffsetRowCheck;
import com.bcv.kagami.runtime.verb.xlsreader.XLSRowCursor;

/**
 * @author Leonid Vysochyn
 */
public interface SectionCheck {
    boolean isCheckSuccessful(XLSRowCursor cursor);
    void addRowCheck(OffsetRowCheck offsetRowCheck);
}
