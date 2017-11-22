package com.bcv.kagami.runtime.verb.xlsreader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.record.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcv.kagami.runtime.verb.xlsreader.BaseBlockReader;
import com.bcv.kagami.runtime.verb.xlsreader.ExpressionCollectionParser;
import com.bcv.kagami.runtime.verb.xlsreader.ReaderConfig;
import com.bcv.kagami.runtime.verb.xlsreader.SectionCheck;
import com.bcv.kagami.runtime.verb.xlsreader.SimpleBlockReaderImpl;
import com.bcv.kagami.runtime.verb.xlsreader.XLSBlockReader;
import com.bcv.kagami.runtime.verb.xlsreader.XLSDataReadException;
import com.bcv.kagami.runtime.verb.xlsreader.XLSForEachBlockReaderImpl;
import com.bcv.kagami.runtime.verb.xlsreader.XLSLoopBlockReader;
import com.bcv.kagami.runtime.verb.xlsreader.XLSReadMessage;
import com.bcv.kagami.runtime.verb.xlsreader.XLSReadStatus;
import com.bcv.kagami.runtime.verb.xlsreader.XLSRowCursor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Leonid Vysochyn
 */
@Service
public class XLSForEachBlockReaderImpl extends BaseBlockReader implements XLSLoopBlockReader {
    protected final Log log = LogFactory.getLog(getClass());

    String items;
    String var;
    Class varType;	
    List innerBlockReaders = new ArrayList();
    static boolean firsttimeflag =true;
    static int d=0;
    static int count=50;
    static Class type=null;
    static Collection itemsCollection1=null;
   
    static String varreflectio=null;
    static boolean checkingmultivalue=false;
    static boolean countrowscheck=true;
    static int multiatttributecounter=0;
    static int multicounter=0;
    static Set<String> restrictCollectionForOneToOne = new HashSet<String>();

    SectionCheck loopBreakCheck;
    
    public XLSForEachBlockReaderImpl() {
    }
    
    public void setNoOfWeaks(int var)
	{
    	multiatttributecounter=var;
	}
    
    public void makeEmbeddeddTrue()
	{
    	 countrowscheck=true;
	}
    public void makeEmpty()
	{
		type=null;
		varreflectio=null;
		checkingmultivalue=true;
	}

	public void makeCheckCondition(){
		checkingmultivalue=false;
	}
	public Map readkk(XLSRowCursor cursor, Map beans,Collection itemsCollection ) {
		readStatus.clear();
		createNewCollectionItem1(itemsCollection, beans);
			return beans;
	}
	public void restrictCollection(Set<String> mySet)
	{
		for(String WeakEntity:mySet)
		{
			restrictCollectionForOneToOne.add(WeakEntity);
		}
		
	}
	private void createNewCollectionItem1(Collection itemsCollection, Map beans) {
		Object obj = null;
		try {
			obj = type.newInstance();
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				Annotation[] annotations = field.getAnnotations();
				for (Annotation annotation : annotations) {
					//random number for getting different instance at each time
					if (annotation.annotationType() == JsonProperty.class && field.getName().equalsIgnoreCase("id"))
					{	
						Random r = new Random();
						field.setAccessible(true);
						//field.set(obj, Integer.toString(r.nextInt(1000)));
						field.set(obj, String.valueOf(count));
						Integer result = Integer.valueOf(count);
						count=++result;
					}
				}
			}
		} catch (Exception e) {
			String message = "Can't create a new collection item for " + items + ". varType = " + varType.getName();
			readStatus.addMessage(new XLSReadMessage(message));
			if (!ReaderConfig.getInstance().isSkipErrors()) {
				readStatus.setStatusOK(false);
				throw new XLSDataReadException(message, readStatus, e);
			}
			if (log.isWarnEnabled()) {
				log.warn(message);
			}
		}
		
		itemsCollection.add(obj);
		beans.put(varreflectio, obj);
	}


    public XLSForEachBlockReaderImpl(int startRow, int endRow, String items, String var, Class varType) {
        this.startRow = startRow;
        this.endRow = endRow;
        this.items = items;
        this.var = var;
        this.varType = varType;
    }

    public XLSReadStatus read(XLSRowCursor cursor, Map beans) {
        readStatus.clear();
        JexlContext context = new MapContext(beans);
        ExpressionCollectionParser parser = new ExpressionCollectionParser(context, items + ";", true);
        Collection itemsCollection = parser.getCollection();
        while (!loopBreakCheck.isCheckSuccessful(cursor)) {
            createNewCollectionItem(itemsCollection, beans);
            readInnerBlocks(cursor, beans);
        }
        cursor.moveBackward();
        return readStatus;
    }
	public XLSReadStatus read1(XLSRowCursor cursor, Map beans,String offset,Collection itemsCollection1) {
		readStatus.clear();
		JexlContext context = new MapContext(beans);
		ExpressionCollectionParser parser = new ExpressionCollectionParser(context, items + ";", true);
		Collection itemsCollection = parser.getCollection();
		while (!loopBreakCheck.isCheckSuccessful(cursor)) {
			createNewCollectionItem(itemsCollection, beans);
			readInnerBlocks1(cursor, beans,offset,itemsCollection);
		}
		return readStatus;
	}

	private void createNewCollectionItem(Collection itemsCollection, Map beans) {
		Object obj = null;
		try {
			obj = varType.newInstance();
			type=varType;
			Field[] fields = varType.getDeclaredFields();
			for (Field field : fields) {
				Annotation[] annotations = field.getAnnotations();
				for (Annotation annotation : annotations) {
					//random number for getting different instance at each time
					if (annotation.annotationType() == JsonProperty.class && field.getName().equalsIgnoreCase("id"))
					{	
						Random r = new Random();
						field.setAccessible(true);
						//field.set(obj, Integer.toString(r.nextInt(1000)));
						field.set(obj, String.valueOf(count));
						Integer result = Integer.valueOf(count);
						count=++result;
					}
				}
			}
		} catch (Exception e) {
			String message = "Can't create a new collection item for " + items + ". varType = " + varType.getName();
			readStatus.addMessage(new XLSReadMessage(message));
			if (!ReaderConfig.getInstance().isSkipErrors()) {
				readStatus.setStatusOK(false);
				throw new XLSDataReadException(message, readStatus, e);
			}
			if (log.isWarnEnabled()) {
				log.warn(message);
			}
		}
		String entity=var;
		if(entity.contains("DTO"))
		{
		 entity =entity.substring(0, var.length() - 3);
		}
		SimpleBlockReaderImpl check=new SimpleBlockReaderImpl();
		check.checkingOneToOneEntities(entity);
		if(!restrictCollectionForOneToOne.contains(var))
		{
		itemsCollection.add(obj);
		}
		beans.put(var, obj);
		varreflectio=var;
	}

    private void readInnerBlocks(XLSRowCursor cursor, Map beans) {
        for (int i = 0; i < innerBlockReaders.size(); i++) {
            XLSBlockReader xlsBlockReader = (XLSBlockReader) innerBlockReaders.get(i);
            readStatus.mergeReadStatus(xlsBlockReader.read(cursor, beans));
            cursor.moveForward();
        }
    }
    
	private void readInnerBlocks1(XLSRowCursor cursor, Map beans,String offset,Collection itemsCollection) {
		if (firsttimeflag) {
			d = innerBlockReaders.size();
			firsttimeflag = false;
		}
		if(multicounter>multiatttributecounter) //1>2  next//2>2  next 3>2
		{
			countrowscheck=true;
			multicounter=0;
		}
		multicounter++;
		for (int i = 0; i < innerBlockReaders.size(); i++) {
			if (i >= 2 && (i == 2 || i <= d)) {
				cursor.setCurrentRowNum(cursor.getCurrentRowNum() + 1);
			}
			XLSBlockReader xlsBlockReader = (XLSBlockReader) innerBlockReaders.get(i);
			if(countrowscheck){    //first time execution
			if(xlsBlockReader instanceof SimpleBlockReaderImpl)
			{
				int a[]={2,2};
				//multiatttributecounter=2;
				SimpleBlockReaderImpl bb=new SimpleBlockReaderImpl();
				readStatus.mergeReadStatus(xlsBlockReader.read3(cursor, beans,offset,itemsCollection));
				//multiatttributecounter=bb.checkMasterOrTransactionInEmbeddedRelations(cursor, beans,offset, itemsCollection);
				//int counter=0
				bb.checkMasterOrTransactionInEmbeddedRelations(var);
				countrowscheck=false;
			}
			}
			if(multiatttributecounter!=0){
			if(xlsBlockReader instanceof XLSForEachBlockReaderImpl)
			{
				SimpleBlockReaderImpl ancv=new SimpleBlockReaderImpl();
				checkingmultivalue=ancv.test();
				if(checkingmultivalue==true)
				{
					cursor.setCurrentRowNum(cursor.getCurrentRowNum() - 2);
				}
			}}
			readStatus.mergeReadStatus(xlsBlockReader.read1(cursor, beans,offset,itemsCollection));
			if(i<d && d-i==1){
				SimpleBlockReaderImpl bb=new SimpleBlockReaderImpl();
				bb.makingawesome();
			}
		}
		cursor.moveForward();
	}

    public void addBlockReader(XLSBlockReader reader) {
        innerBlockReaders.add(reader);
    }

    public List getBlockReaders() {
        return innerBlockReaders;
    }

    public SectionCheck getLoopBreakCondition() {
        return loopBreakCheck;
    }

    public void setLoopBreakCondition(SectionCheck sectionCheck) {
        this.loopBreakCheck = sectionCheck;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setVarType(Class varType) {
        this.varType = varType;
    }

    public String getItems() {
        return items;
    }

    public String getVar() {
        return var;
    }

    public Class getVarType() {
        return varType;
    }
	@Override
	public XLSReadStatus read3(XLSRowCursor cursor, Map beans, String offset, Collection item) {
		// TODO Auto-generated method stub
		return null;
	}

}
