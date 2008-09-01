/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2001                                              */
/*                                                                           */
/* This software is licensed under the Common Public License (CPL) V1.0.     */
/*****************************************************************************/

package com.ibm.staf;

// STAFResult - This class is used to hold the return code / result string
//              pair from a STAFHandle.submit2 call.  It also contains the
//              return code constants.

public class STAFResult
{
    public STAFResult() { rc = STAFResult.Ok; result = new String(); }
    public STAFResult(int theRC) { rc = theRC; result = new String(); }
    public STAFResult(int theRC, String theResult)
    {
        rc = theRC;
        result = theResult;
    }

    public int rc;
    public String result;

    public static final int Ok = 0;
    public static final int InvalidAPI = 1;
    public static final int UnknownService = 2;
    public static final int InvalidHandle = 3;
    public static final int HandleAlreadyExists = 4;
    public static final int HandleDoesNotExist = 5;
    public static final int UnknownError = 6;
    public static final int InvalidRequestString = 7;
    public static final int InvalidServiceResult = 8;
    public static final int REXXError = 9;
    public static final int BaseOSError = 10;
    public static final int ProcessAlreadyComplete = 11;
    public static final int ProcessNotComplete = 12;
    public static final int VariableDoesNotExist = 13;
    public static final int UnResolvableString = 14;
    public static final int InvalidResolveString = 15;
    public static final int NoPathToMachine = 16;
    public static final int FileOpenError = 17;
    public static final int FileReadError = 18;
    public static final int FileWriteError = 19;
    public static final int FileDeleteError = 20;
    public static final int STAFNotRunning = 21;
    public static final int CommunicationError = 22;
    public static final int TrusteeDoesNotExist = 23;
    public static final int InvalidTrustLevel = 24;
    public static final int AccessDenied = 25;
    public static final int STAFRegistrationError = 26;
    public static final int ServiceConfigurationError = 27;
    public static final int QueueFull = 28;
    public static final int NoQueueElement = 29;
    public static final int NotifieeDoesNotExist = 30;
    public static final int InvalidAPILevel = 31;
    public static final int ServiceNotUnregisterable = 32;
    public static final int ServiceNotAvailable = 33;
    public static final int SemaphoreDoesNotExist = 34;
    public static final int NotSemaphoreOwner = 35;
    public static final int SemaphoreHasPendingRequests = 36;
    public static final int Timeout = 37;
    public static final int JavaError = 38;
    public static final int ConverterError = 39;
    public static final int NotUsed = 40;
    public static final int InvalidObject = 41;
    public static final int InvalidParm = 42;
    public static final int RequestNumberNotFound = 43;
    public static final int InvalidAsynchOption = 44;
    public static final int RequestNotComplete = 45;
    public static final int ProcessAuthenticationDenied = 46;
    public static final int InvalidValue = 47;
    public static final int DoesNotExist = 48;
    public static final int AlreadyExists = 49;
    public static final int DirectoryNotEmpty = 50;
    public static final int DirectoryCopyError = 51;
    public static final int DiagnosticsNotEnabled = 52;
    public static final int HandleAuthenticationDenied = 53;
    public static final int HandleAlreadyAuthenticated = 54;
    public static final int InvalidSTAFVersion = 55;
    public static final int RequestCancelled = 56;

    public static final int UserDefined = 4000;
}
