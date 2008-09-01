/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2001                                              */
/*                                                                           */
/* This software is licensed under the Common Public License (CPL) V1.0.     */
/*****************************************************************************/

package com.ibm.staf;

// STAFHandle - This class provides the Java interface to STAF.
//
// A Java application using STAF would create a STAFHandle object.  This
// object is then used to submit requests to STAF.  The submit() method
// acts in typical Java fashion and throws a STAFException if it encounters
// an error.  The submit2() method does not throw an exception.  It returns
// an instance of the STAFResult class, which contains the actual STAF
// return code as well as any additional information returned by the STAF
// service request.  Before exiting, the Java application should call the
// unRegister() method to unregister with STAF.
//
// Note: The STAFHandle class is fully re-entrant.  Only one STAFHandle
//       object should be created for use by the entire Java application.

public class STAFHandle
{
    public static final int ReqSync = 0;
    public static final int ReqFireAndForget = 1; 
    public static final int ReqQueue = 2;
    public static final int ReqRetain = 3;
    public static final int ReqQueueRetain = 4;

    // Constructors
    public STAFHandle(String handleName) throws STAFException
    {
        STAFRegister(handleName);
    }

    public STAFHandle(int staticHandleNumber)
    {
        handle = staticHandleNumber;
    }

    // Submit a request to STAF
    public String submit(String where, String service, String request)
                  throws STAFException
    {
        return STAFSubmit(ReqSync, where, service, request);
    }

    public String submit(int syncOption, String where, 
                         String service, String request)
                  throws STAFException
    {
        return STAFSubmit(syncOption, where, service, request);
    }

    public STAFResult submit2(String where, String service, String request)
    {
        return STAFSubmit2(ReqSync, where, service, request);
    }

    public STAFResult submit2(int syncOption, String where, 
                              String service, String request)
    {
        return STAFSubmit2(syncOption, where, service, request);
    }

    // Unregister this handle with STAF
    public void unRegister() throws STAFException
    {
        STAFUnRegister();
    }


    // Retrieve the internal handle value
    public int getHandle() { return handle; }


    // Instance variable to keep the STAF Handle for this class
    private int handle;

    /************************/
    /* All the native stuff */
    /************************/

    private static native void initialize();
    private native void STAFRegister(String handleName);
    private native void STAFUnRegister();
    private native String STAFSubmit(int syncOption, String where, 
                                     String service, String request);
    private native STAFResult STAFSubmit2(int syncOption, String where, 
                                          String service, String request);

    // Static initializer - called first time class is loaded.
    static
    {
        if (System.getProperty("os.name").toLowerCase().indexOf("aix") == 0)
            System.loadLibrary("STAF");

        System.loadLibrary("JSTAF");
        initialize();
    }
}
