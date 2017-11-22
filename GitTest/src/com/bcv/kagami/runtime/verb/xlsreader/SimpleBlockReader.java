package com.bcv.kagami.runtime.verb.xlsreader;

import java.util.List;

import com.bcv.kagami.runtime.verb.xlsreader.BeanCellMapping;
import com.bcv.kagami.runtime.verb.xlsreader.XLSBlockReader;

/**
 * Interface to read simple block of excel rows
 * @author Leonid Vysochyn
 */
public interface SimpleBlockReader extends XLSBlockReader{
    void addMapping(BeanCellMapping mapping);

    List getMappings();
}