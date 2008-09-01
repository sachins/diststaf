/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2001                                              */
/*                                                                           */
/* This software is licensed under the Common Public License (CPL) V1.0.     */
/*****************************************************************************/

package com.ibm.staf;

import java.util.Map;

// STAFQueueMessage - This class is used to inspect and manipulate a message
//                    retrieved from the STAF Queue service

public class STAFQueueMessage
{
    public STAFQueueMessage(String qMessage)
    {
        // Unmarshall the result from a QUEUE GET request

        mc = STAFMarshallingContext.unmarshall(qMessage);

        Map queueMap = (Map)mc.getRootObject();

        try
        {
            priority = Integer.parseInt((String)queueMap.get("priority"));
        }
        catch (NumberFormatException e)
        {
            priority =  -1;
        }

        timestamp  = (String)queueMap.get("timestamp");
        machine    = (String)queueMap.get("machine");
        handleName = (String)queueMap.get("handleName");
            
        try
        {
            handle = Integer.parseInt((String)queueMap.get("handle"));
        }
        catch (NumberFormatException e)
        {
            handle =  -1;
        }

        type = (String)queueMap.get("type");
        message = queueMap.get("message");
    }

    public int    priority;
    public String timestamp;
    public String machine;
    public String handleName;
    public int    handle;
    public String type = null;
    public Object message;
    public STAFMarshallingContext mc;
}
