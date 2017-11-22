package com.bcv.kagami.runtime.verb.xlsreader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.usermodel.Sheet;

import com.bcv.kagami.runtime.verb.xlsreader.XLSBlockReader;
import com.bcv.kagami.runtime.verb.xlsreader.XLSReadStatus;
import com.bcv.kagami.runtime.verb.xlsreader.XLSRowCursor;
import com.bcv.kagami.runtime.verb.xlsreader.XLSRowCursorImpl;
import com.bcv.kagami.runtime.verb.xlsreader.XLSSheetReader;

/**
 * @author Leonid Vysochyn
 */
public class XLSSheetReaderImpl implements XLSSheetReader {

    List blockReaders = new ArrayList();
    String sheetName;
    int sheetIdx = -1;

    XLSReadStatus readStatus = new XLSReadStatus();


    public XLSReadStatus read(Sheet sheet, Map beans) {
        readStatus.clear();
        XLSRowCursor cursor = new XLSRowCursorImpl( sheetName, sheet );
        for (int i = 0; i < blockReaders.size(); i++) {
            XLSBlockReader blockReader = (XLSBlockReader) blockReaders.get(i);
            readStatus.mergeReadStatus( blockReader.read( cursor, beans ) );
            cursor.moveForward();
        }
        
        return readStatus;
    }
    
	public XLSReadStatus read1(Sheet sheet, Map beans,String offset) {
		readStatus.clear();
		XLSRowCursor cursor = new XLSRowCursorImpl(sheetName, sheet);
		int previous_row = 0;
		XLSReadStatus readStatus = read2(sheet, beans, cursor, previous_row,offset);
		return readStatus;
	}

	public XLSReadStatus read2(Sheet sheet, Map beans, XLSRowCursor cursor, int previous_row,String offset) {
		readStatus.clear();
		int current_row=cursor.getCurrentRowNum();
		if (previous_row != 0 && previous_row == current_row) {
			return readStatus;
		} else {
			previous_row = current_row;
		}
		for (int i = 0; i < blockReaders.size(); i++) {
			XLSBlockReader blockReader = (XLSBlockReader) blockReaders.get(i);
			readStatus.mergeReadStatus(blockReader.read1(cursor, beans,offset,null));
			cursor.moveForward();
		}
		read2(sheet, beans, cursor, previous_row + 2,offset);
		return readStatus;
	}

    public String getSheetNameBySheetIdx(Sheet sheet, int idx){
        Sheet sheetAtIdx = sheet.getWorkbook().getSheetAt(idx);
        return sheetAtIdx.getSheetName();
    }

    public List getBlockReaders() {
        return blockReaders;
    }

    public void setBlockReaders(List blockReaders) {
        this.blockReaders = blockReaders;
    }

    public void addBlockReader(XLSBlockReader blockReader) {
        blockReaders.add( blockReader );
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getSheetIdx(){
        return sheetIdx;
    }

    public void setSheetIdx(int sheetIdx){
        this.sheetIdx = sheetIdx;
    }
}
