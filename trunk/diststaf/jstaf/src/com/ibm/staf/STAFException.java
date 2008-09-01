/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2001                                              */
/*                                                                           */
/* This software is licensed under the Common Public License (CPL) V1.0.     */
/*****************************************************************************/

package com.ibm.staf;

// STAFException - This class is an exception class that is used/thrown by
//                 the STAFHandle class.  The rc variable allows you to
//                 access the actual return code issued by STAF.  You can
//                 use the standard throwable method GetMessage() to
//                 retrieve any extra data regarding the exception.

public class STAFException extends Exception
{
    public STAFException() { super(); rc = 0; }
    public STAFException(int theRC) { super(); rc = theRC; }
    public STAFException(int theRC, String s) { super(s); rc = theRC; }

    public int rc;
}
