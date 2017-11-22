package com.bcv.kagami.runtime.verb.xlsreader;

import java.util.List;

import com.bcv.kagami.runtime.verb.xlsreader.SectionCheck;
import com.bcv.kagami.runtime.verb.xlsreader.XLSBlockReader;

/**
 * Interface to read repetitive block of excel rows
 * @author Leonid Vysochyn
 */
public interface XLSLoopBlockReader extends XLSBlockReader {
    void setLoopBreakCondition(SectionCheck condition);
    SectionCheck getLoopBreakCondition();
    void addBlockReader(XLSBlockReader reader);
    List getBlockReaders();
}
