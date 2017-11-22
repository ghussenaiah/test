package com.bcv.kagami.runtime.verb.xlsreader;

import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import com.bcv.kagami.core.container.contract.data.entity.EntityMetaData;
import com.bcv.kagami.core.container.contract.data.entity.StrongRelations;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bcv.kagami.runtime.verb.xlsreader.BaseBlockReader;
import com.bcv.kagami.runtime.verb.xlsreader.BeanCellMapping;
import com.bcv.kagami.runtime.verb.xlsreader.ReaderConfig;
import com.bcv.kagami.runtime.verb.xlsreader.SectionCheck;
import com.bcv.kagami.runtime.verb.xlsreader.SimpleBlockReader;
import com.bcv.kagami.runtime.verb.xlsreader.XLSDataReadException;
import com.bcv.kagami.runtime.verb.xlsreader.XLSForEachBlockReaderImpl;
import com.bcv.kagami.runtime.verb.xlsreader.XLSReadMessage;
import com.bcv.kagami.runtime.verb.xlsreader.XLSReadStatus;
import com.bcv.kagami.runtime.verb.xlsreader.XLSRowCursor;
import com.bcv.kagami.core.container.contract.data.entity.WeakRelations;
import com.bcv.kagami.runtime.context.utils.EntityContractData;
import com.bcv.kagami.runtime.verb.utils.XlsReaderWriter;

/**
 * @author Leonid Vysochyn
 */

public class SimpleBlockReaderImpl extends BaseBlockReader implements
		SimpleBlockReader {
	protected final Log log = LogFactory.getLog(getClass());

	List beanCellMappings = new ArrayList();
	SectionCheck sectionCheck;
	static boolean checkmulti=false;
	static int counterformulticheck=0;
	static boolean checkfalg=true;
	static int counteriteration=0;
	static boolean secondcheck=false;
	static boolean testingflag=true;
	static Set<String> hset = new HashSet<String>();
            

	static {
		ReaderConfig.getInstance();
		
	}
	
	
private static Map<String, String> offsetZoneIdMap;
public static String DATE = "date";
public static String DATETIME = "datetime";
public static String TIME="time";

	static {
		offsetZoneIdMap = new HashMap<>();

		for (String zoneId : ZoneId.getAvailableZoneIds()) {
			if (zoneId.contains("SystemV")) {
				continue;
			}
			ZoneId zone = ZoneId.of(zoneId);
			ZoneOffset offset = ZonedDateTime.now(zone).getOffset();
			Long offsetInHours = (long) (offset.getTotalSeconds() / 60);
			offsetZoneIdMap.put(offsetInHours.toString(), zoneId);
		}
	}

	public SimpleBlockReaderImpl() {
	}

	public SimpleBlockReaderImpl(int startRow, int endRow, List beanCellMappings) {
		this.startRow = startRow;
		this.endRow = endRow;
		this.beanCellMappings = beanCellMappings;
	}

	public SimpleBlockReaderImpl(int startRow, int endRow) {
		this.startRow = startRow;
		this.endRow = endRow;
	}

	public XLSReadStatus read(XLSRowCursor cursor, Map beans) {
	
		return readStatus;
	}
	public void addingMultiData(String a[],String data,Map beans,XLSRowCursor cursor,Collection itemsCollection)
	{
		BeanCellMapping mapping;
		for(int i=0;i<a.length;i++)
		{
			if(i==0)
			{
				String abbb=a[i];
				try {
					for (Iterator iterator = beanCellMappings.iterator(); iterator
						.hasNext();) {
					mapping = (BeanCellMapping) iterator.next();
					
					try {
						mapping.populateBean(abbb, beans,null);
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			else
			{
			
			String tt=a[i];
				//JexlContext context = new MapContext(beans);
				//ExpressionCollectionParser parser = new ExpressionCollectionParser(context, items + ";", true);
				//Collection itemsCollection = parser.getCollection();
				//createNewCollectionItem(itemsCollection, beans);
				XLSForEachBlockReaderImpl an=new XLSForEachBlockReaderImpl();
				Map beansss=an.readkk(cursor, beans,itemsCollection);
				
				for (Iterator iterator = beanCellMappings.iterator(); iterator
						.hasNext();) {
					mapping = (BeanCellMapping) iterator.next();
					
					try {
						mapping.populateBean(tt, beansss,null);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		XLSForEachBlockReaderImpl an=new XLSForEachBlockReaderImpl();
		an.makeEmpty();
	}

	public int  checknextmultivalue(XLSRowCursor cursor, Map beans,String offset,Collection itemsCollection)
	{
		readStatus.clear();
		final int currentRowNum = cursor.getCurrentRowNum();
		final int rowShift = currentRowNum - startRow;
		BeanCellMapping mapping;
		String dataString =  null;
		for (Iterator iterator = beanCellMappings.iterator(); iterator
				.hasNext();) {
			mapping = (BeanCellMapping) iterator.next();
			try {
				dataString = readCellString(cursor.getSheet(),
						mapping.getRow() + rowShift, mapping.getCol(),offset);
				//String bb=mapping.beanKey;
				
				String[] Values = dataString.split("\\,");
				
				if(StringUtils.isEmpty(dataString))
				{
					log.error("DataString is null At"+getCellName(mapping, rowShift));
					throw new Exception("Can't read cell "
							+ getCellName(mapping, rowShift) + " on "
							+ cursor.getSheetName() + "spreadsheet"+"  &&  Please Check and Correct DataTye At Column"  +mapping.getCol());
					
				}
				if(Values.length>1){
					return 0;
				}
				else
				{
					return 1;
				}
			} catch (Exception e) {
				String message = null;
				if(dataString != null)
				  message = "Can't read cell "
						+ getCellName(mapping, rowShift) + " on "
						+ cursor.getSheetName() + " spreadsheet"+" && Check Column DataType Correctly";
				else
					 message= e.getMessage();
				readStatus.addMessage(new XLSReadMessage(message, e));
				if (ReaderConfig.getInstance().isSkipErrors()) {
					if (log.isWarnEnabled()) {
						log.warn(message);
					}
				} else {
					readStatus.setStatusOK(false);
					throw new XLSDataReadException("Can't read cell "
							+ getCellName(mapping, rowShift) + " on "
							+ cursor.getSheetName() + "spreadsheet"+" && Provide Correct DataTye At Column" +mapping.getCol(),
						 e);
				}
				
			}
			
		}
		return 0;
	}
	
	/*public int checkMasterOrTransactionInEmbeddedRelations(XLSRowCursor cursor, Map beans,String offset,Collection itemsCollection)
	{
		readStatus.clear();
		final int currentRowNum = cursor.getCurrentRowNum();
		final int rowShift = currentRowNum - startRow;
		BeanCellMapping mapping;
		String dataString =  null;
		for (Iterator iterator = beanCellMappings.iterator(); iterator
				.hasNext();) {
			mapping = (BeanCellMapping) iterator.next();
		
			try {
				XlsReaderWriter xls=new XlsReaderWriter();
				String entity= mapping.beanKey;
				counterformulticheck=xls.getTotalNoofWeakRelations(entity);
			}catch(Exception e)
			{
				 e.printStackTrace();
			}
		}
			
			return counterformulticheck;
				
	}*/
	//public XLSReadStatus read3(XLSRowCursor cursor, Map beans,String offset,Collection itemsCollection) {
	public void checkMasterOrTransactionInEmbeddedRelations(String var){
		//public XLSReadStatus read3(XLSRowCursor cursor, Map beans,String offset,Collection itemsCollection) {
	/*	readStatus.clear();
		final int currentRowNum = cursor.getCurrentRowNum();
		final int rowShift = currentRowNum - startRow;
		BeanCellMapping mapping;
		String dataString =  null;
		String entity;
		Iterator iterator = beanCellMappings.iterator(); 
		iterator.hasNext();
			mapping = (BeanCellMapping) iterator.next();*/
			  int totalweak=0;
			try {
				//entity= mapping.beanKey;
				if(var.contains("DTO"))
				{
					var = var.substring(0, var.length() - 3);
					 //DTO nothing but Transactin Entity No need to check Master entities at this time
					 //xlsforeach.makeEmbeddeddTrue();
					 //return readStatus;
				}
				EntityContractData mm=new EntityContractData();
				List<EntityMetaData> yy=mm.getEntityContractData();
				
				for(EntityMetaData mmmm:yy)
				{
					if(var.equalsIgnoreCase(mmmm.getEntityId()))
					{
						List<WeakRelations> weakReldesign = mmmm.getRelationships().getWeakRelationship();
						if (!CollectionUtils.isEmpty(weakReldesign)) {
							
							for(WeakRelations weakrelation:weakReldesign)
							{
								String relationType=weakrelation.getRelationshipType();
								
								if(relationType.equalsIgnoreCase("OneToMany"))
								{
									totalweak++;
								}
									
							}
						}
					}
				}
				counterformulticheck=totalweak;
				XLSForEachBlockReaderImpl an=new XLSForEachBlockReaderImpl();
				an.setNoOfWeaks(totalweak);
			}catch(Exception e)
			{
				 e.printStackTrace();
			}
			
	}
	public void checkingOneToOneEntities(String parentEntity)
	{
		
		EntityContractData mm=new EntityContractData();
		List<EntityMetaData> yy=mm.getEntityContractData();
		
		for(EntityMetaData mmmm:yy)
		{
			if(parentEntity.equalsIgnoreCase(mmmm.getEntityId()))
			{
				List<StrongRelations> strongRelations = mmmm.getRelationships().getStrongRelationship();
				if (!CollectionUtils.isEmpty(strongRelations)) {
					
					for(StrongRelations strongRelation:strongRelations)
					{
						if(strongRelation.getRelationshipType().equalsIgnoreCase("OneToOne"))
						{
							String weakrelationEntity=strongRelation.getEntityId();
							hset.add(weakrelationEntity);
							XLSForEachBlockReaderImpl weakentityset=new XLSForEachBlockReaderImpl();
							weakentityset.restrictCollection(hset);
						}
					}
				}
			}
		}
	}
	public XLSReadStatus read1(XLSRowCursor cursor, Map beans,String offset,Collection itemsCollection) {
		readStatus.clear();
		final int currentRowNum = cursor.getCurrentRowNum();
		final int rowShift = currentRowNum - startRow;
		BeanCellMapping mapping;
		String dataString =  null;
		int count=0;
		for (Iterator iterator = beanCellMappings.iterator(); iterator
				.hasNext();) {
			mapping = (BeanCellMapping) iterator.next();
			
			try {
			
				dataString = readCellString(cursor.getSheet(),
						mapping.getRow() + rowShift, mapping.getCol(),offset);
				boolean dateType=readType(cursor.getSheet(),
						mapping.getRow() + rowShift, mapping.getCol(),offset);
				String type=null;
					if(dateType==true)
						type="Date";
			
					String[] Values = dataString.split("\\,");
				if(Values.length>1){
					 checkmulti=true;
					XLSForEachBlockReaderImpl an=new XLSForEachBlockReaderImpl();
					addingMultiData(Values,dataString,beans,cursor,itemsCollection);
					//here we are iterating for creating multiple objects and assigning values to them.
				}
				else
				{
				mapping.populateBean(dataString, beans,type);
				}
				if(StringUtils.isEmpty(dataString))
				{
					log.error("DataString is null At"+getCellName(mapping, rowShift));
					throw new Exception("Can't read cell "
							+ getCellName(mapping, rowShift) + " on "
							+ cursor.getSheetName() + "spreadsheet"+"  &&  Please Check and Correct DataTye At Column"  +mapping.getCol());
					
				}
				//mapping.populateBean(dataString, beans);
			} catch (Exception e) {
				String message = null;
				if(dataString != null)
				  message = "Can't read cell "
						+ getCellName(mapping, rowShift) + " on "
						+ cursor.getSheetName() + " spreadsheet"+" && Check Column DataType Correctly";
				else
					 message= e.getMessage();
				readStatus.addMessage(new XLSReadMessage(message, e));
				if (ReaderConfig.getInstance().isSkipErrors()) {
					if (log.isWarnEnabled()) {
						log.warn(message);
					}
				} else {
					readStatus.setStatusOK(false);
					throw new XLSDataReadException("Can't read cell "
							+ getCellName(mapping, rowShift) + " on "
							+ cursor.getSheetName() + "spreadsheet"+" && Provide Correct DataTye At Column" +mapping.getCol(),
						 e);
				}
				
			}
			
		}
		if(counterformulticheck!=0){   //next counteriteration 2
			
			if(counteriteration<counterformulticheck && counteriteration!=0 && counteriteration>=1)
			{
				secondcheck=true;
			}
			if(counterformulticheck==counteriteration)
			{
				secondcheck=false;
				counteriteration=-1;
				counterformulticheck=0;
				XLSForEachBlockReaderImpl an=new XLSForEachBlockReaderImpl();
				an.setNoOfWeaks(counterformulticheck);
				
			}
			counteriteration++;   
			}
			if(testingflag)
			{
				//counteriteration;
				testingflag=false;
				counterformulticheck=0;
			}
		cursor.setCurrentRowNum(endRow + rowShift);
		return readStatus;
	}
	
	private boolean readType(Sheet sheet, int rowNum, short cellNum,String offset) {
		Cell cell = getCell(sheet, rowNum, cellNum);
	    int abc=cell.getCellType();
	    if(abc==1)
	    {
	    	String dataString = cell.getRichStringCellValue().getString();
	    	if (dataString.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})\\s([0-9]{2}):([0-9]{2})")
	    			||dataString.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")
	    			||dataString.matches("([0-9]{2}):([0-9]{2})")
	    			||dataString.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})"))
	    		return true;
	    	{
	    		
	    	}
	    }
		return false;
	}

	private String readCellString(Sheet sheet, int rowNum, short cellNum,String offset) {
		Cell cell = getCell(sheet, rowNum, cellNum);
		return getCellString(cell,offset);
	}

	private String getCellString(Cell cell,String offset) {
		String dataString = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				dataString = cell.getRichStringCellValue().getString();
				if (dataString.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})\\s([0-9]{2}):([0-9]{2})")) {
					long dateTimeZoneValue = getMillisecondsAccordingToZone(dataString, offset,DATETIME);
					dataString = ""+dateTimeZoneValue;
				}
				else if(dataString.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")||dataString.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})"))
				{
					long dateTimeZoneValue = getMillisecondsAccordingToZone(dataString, offset,DATE);
					dataString = ""+dateTimeZoneValue;
					return dataString;
				}
				else if(dataString.matches("([0-9]{2}):([0-9]{2})"))
				{
					long dateTimeZoneValue = getMillisecondsAccordingToZone(dataString, offset,TIME);
					dataString = ""+dateTimeZoneValue;
					return dataString;
				}
				
				break;
			case Cell.CELL_TYPE_NUMERIC:
				dataString = readNumericCell(cell,offset);
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				dataString = Boolean.toString(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_BLANK:
				break;
			case Cell.CELL_TYPE_ERROR:
				break;
			case Cell.CELL_TYPE_FORMULA:
				// attempt to read formula cell as numeric cell
				try {
					dataString = readNumericCell(cell,offset);
				} catch (Exception e1) {
					log.info("Failed to read formula cell as numeric. Next to try as string. Cell="
							+ cell.toString());
					try {
						dataString = cell.getRichStringCellValue().getString();
						log.info("Successfully read formula cell as string. Value="
								+ dataString);
					} catch (Exception e2) {
						log.warn("Failed to read formula cell as numeric or string. Cell="
								+ cell.toString());
					}
				}
				break;
			default:
				break;
			}
		}
		return dataString;
	}

	public long getMillisecondsAccordingToZone(String value, String offset, String dataType) {

		String dateFormatString = null;
		log.info("Inside Simple Block ReaderImpl");
		log.info("Country Offset" + offset);
		String canonicalId = offsetZoneIdMap.get(offset);
		if (canonicalId == null) {
			canonicalId = "Asia/Kolkata";
		}
		log.info("Canonical Id : " + canonicalId);

		if (dataType.equalsIgnoreCase("datetime")) {
			dateFormatString = "dd-MM-yyyy HH:mm";
		} else if(dataType.equalsIgnoreCase("date")) {
			dateFormatString = "dd-MM-yyyy";
		} else if(dataType.equalsIgnoreCase("time")) {
			dateFormatString = "HH:mm";
		} 
		DateTimeFormatter dfm = DateTimeFormat.forPattern(dateFormatString).withZone(DateTimeZone.forID(canonicalId));
		DateTime dateVlaue = dfm.parseDateTime(value.trim());
		long dateLong = dateVlaue.getMillis();
		log.info("Date generated: " + dateVlaue.toString());
		log.info("Date SimpleBlock Reader in milliseconds: " + dateVlaue.getMillis());

		return dateLong;
	}

	private String readNumericCell(Cell cell,String offset) {
		double value = 0;
		String dataString = null;
		
		if (DateUtil.isCellDateFormatted(cell)) {
			long dateTimeZoneValue = getMillisecondsAccordingToZone(dataString, offset,DATE);
			dataString = ""+dateTimeZoneValue;
			return dataString;
		} else {

			value = cell.getNumericCellValue();
		}
		if (((int) value) == value) {
			dataString = Integer.toString((int) value);
		} else {
			dataString = Double.toString(cell.getNumericCellValue());
		}
		return dataString;
	}

	private String getCellName(BeanCellMapping mapping, int rowShift) {
		CellReference currentCellRef = new CellReference(mapping.getRow()
				+ rowShift, mapping.getCol(), false, false);
		return currentCellRef.formatAsString();
	}

	public SectionCheck getLoopBreakCondition() {
		return sectionCheck;
	}

	public void setLoopBreakCondition(SectionCheck sectionCheck) {
		this.sectionCheck = sectionCheck;
	}

	public void addMapping(BeanCellMapping mapping) {
		beanCellMappings.add(mapping);
	}

	public List getMappings() {
		return beanCellMappings;
	}

	private Cell getCell(Sheet sheet, int rowNum, int cellNum) {
		Row row = sheet.getRow(rowNum);
		if (row == null) {
			return null;
		}
		return row.getCell(cellNum);
	}
	
	public boolean test()
	{
		return secondcheck;
	}
	public void makingawesome()
	{
		counteriteration=0;
	}

	@Override
	public XLSReadStatus read3(XLSRowCursor cursor, Map beans, String offset, Collection item) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
