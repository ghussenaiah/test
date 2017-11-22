package com.bcv.kagami.runtime.verb.xlsreader;

import java.util.ArrayList;
import java.util.List;

import com.bcv.kagami.runtime.verb.xlsreader.XLSReadMessage;
import com.bcv.kagami.runtime.verb.xlsreader.XLSReadStatus;

/**
 * @author Leonid Vysochyn
 * @version 1.0 Jul 30, 2007
 */
public class XLSReadStatus {
    List readMessages = new ArrayList();

    boolean statusOK = true;

    public XLSReadStatus() {
    }

    void mergeReadStatus(XLSReadStatus status){
        if( status == null ){
            return;
        }
        if( !status.isStatusOK() ){
            statusOK = false;
        }
        addMessages( status.getReadMessages() );
    }

    void addMessage( XLSReadMessage errorMessage ){
        if( errorMessage != null ){
            readMessages.add( errorMessage );
        }
    }

    void addMessages( List list ){
        if( list != null ){
            readMessages.addAll( list );
        }
    }

    void clear(){
        readMessages.clear();
    }

    public boolean isStatusOK() {
        return statusOK;
    }

    public void setStatusOK(boolean statusOK) {
        this.statusOK = statusOK;
    }

    public List getReadMessages() {
        return readMessages;
    }
}
