package com.bcv.kagami.runtime.verb.xlsreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import com.bcv.kagami.runtime.verb.xlsreader.XLSReadStatus;
import com.bcv.kagami.runtime.verb.xlsreader.XLSReader;
import com.bcv.kagami.runtime.verb.xlsreader.XLSSheetReader;

/**
 * Basic implementation of {@link XLSReader} interface
 *
 * @author Leonid Vysochyn
 */
@Service
public class XLSReaderImpl implements XLSReader{
    protected final Log log = LogFactory.getLog(getClass());

    Map sheetReaders = new HashMap();
    Map sheetReadersByIdx = new HashMap();


    XLSReadStatus readStatus = new XLSReadStatus();


    public XLSReadStatus read(InputStream inputXLS, Map beans,String offset) throws IOException, InvalidFormatException{
        readStatus.clear();
        Workbook workbook = WorkbookFactory.create(inputXLS);
        for(int sheetNo = 0; sheetNo < workbook.getNumberOfSheets(); sheetNo++){
            readStatus.mergeReadStatus(readSheet(workbook, sheetNo, beans,offset));
        }
        return readStatus;
    }

    private XLSReadStatus readSheet(Workbook workbook, int sheetNo, Map beans,String offset){
        Sheet sheet = workbook.getSheetAt(sheetNo);
        String sheetName = workbook.getSheetName(sheetNo);
        if(log.isInfoEnabled()){
            log.info("Processing sheet " + sheetName);
        }
        XLSSheetReader sheetReader;
        if(sheetReaders.containsKey(sheetName)){
            sheetReader = (XLSSheetReader) sheetReaders.get(sheetName);
            return readSheet(sheetReader, sheet, sheetName, beans,offset);
        } else if(sheetReadersByIdx.containsKey(new Integer(sheetNo))){
            sheetReader = (XLSSheetReader) sheetReadersByIdx.get(new Integer(sheetNo));
            return readSheet(sheetReader, sheet, sheetName, beans,offset);
        } else{
            return null;
        }
    }

    private XLSReadStatus readSheet(XLSSheetReader sheetReader, Sheet sheet, String sheetName, Map beans,String offset){
        sheetReader.setSheetName(sheetName);
        return sheetReader.read1(sheet, beans,offset);
    }

    public Map getSheetReaders(){
        return sheetReaders;
    }

    public Map getSheetReadersByIdx(){
        return sheetReadersByIdx;
    }

    public void setSheetReadersByIdx(Map sheetReaders){
        sheetReadersByIdx = sheetReaders;
    }

    public void addSheetReader(String sheetName, XLSSheetReader reader){
        sheetReaders.put(sheetName, reader);
    }

    public void addSheetReader(Integer idx, XLSSheetReader reader){
        sheetReadersByIdx.put(idx, reader);
    }

    public void addSheetReader(XLSSheetReader reader){
        addSheetReader(reader.getSheetName(), reader);
        if( reader.getSheetIdx() >= 0 ){
            addSheetReader(new Integer(reader.getSheetIdx()), reader);
        }
    }

    public void setSheetReaders(Map sheetReaders){
        this.sheetReaders = sheetReaders;
    }
}
