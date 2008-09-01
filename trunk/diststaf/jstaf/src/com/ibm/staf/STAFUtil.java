/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2001, 2005                                        */
/*                                                                           */
/* This software is licensed under the Common Public License (CPL) V1.0.     */
/*****************************************************************************/

package com.ibm.staf;
import com.ibm.staf.service.*;

// STAFUtil - This class provides STAF utility functions

public class STAFUtil
{
    public static String privacyDelimiter1 = "!!@";
    public static String privacyDelimiter2 = "@!!";
    public static String escapedPrivacyDelimiter1 = "^!!@";
    public static String escapedPrivacyDelimiter2 = "^@!!";
    public static String privacyDelimiterEscape = "^";

    /***********************************************************************/
    /* Description:                                                        */
    /*   This method returns length delimited representation of the string */
    /*   (aka the colon length colon format).                              */
    /*                                                                     */
    /* Input:  A string.   For example:  "Hi there"                        */
    /*                                                                     */
    /* Returns:                                                            */
    /*   String in the colon lengthh colon format.                         */
    /*                     For example:  ":8:Hi there"                     */
    /***********************************************************************/
    public static String wrapData(String data)
    {
        return ":" + data.length() + ":" + data;
    }

    /***********************************************************************/
    /* Description:                                                        */
    /*   This method returns the string without the colon length colon     */
    /*   prefix.                                                           */
    /*                                                                     */
    /* Input:  String which is in colon length colon format                */
    /*                    For example:  ":8:Hi there"                      */
    /*                                                                     */
    /* Returns:                                                            */
    /*   String without the colon length colon. For example: "Hi there"    */
    /***********************************************************************/
    public static String unwrapData(String data)
    {
        if (data != null)
        {
            int colon1Pos = data.indexOf(":");

            if (colon1Pos == 0)
            {
                int colon2Pos = data.indexOf(":", 1);

                if (colon2Pos > -1)
                {
                    try
                    {
                        // Verify that an integer was specified between the two
                        // colons to make sure the value has a colonLengthColon
                        // format, and just doesn't happen to contain two colons

                        int length = (new Integer(
                            data.substring(1, colon2Pos))).intValue();

                        String newValue = data.substring(colon2Pos + 1);

                        if (length == newValue.length())
                            return newValue;
                    }
                    catch (NumberFormatException e)
                    {
                        // Not a CLC format
                    }
                }
            }
        }

        return data;
    }

    /***********************************************************************/
    /* Description:                                                        */
    /*   This method returns the endpoint without the port (strips @nnnn   */
    /*   from the end of the endpoint                                      */
    /*                                                                     */
    /* Input:  Endpoint   <Interface>://<System identifier>[@<Port>]       */
    /*                    For example:  tcp://client1.company.com@6500     */
    /*                                                                     */
    /* Returns:                                                            */
    /*   Endpoint without the port. For example: tcp://client1.company.com */
    /***********************************************************************/
    public static String stripPortFromEndpoint(String endpoint)
    {
        // Strip the port from the endpoint, if present.

        String endpointNoPort = endpoint;
        int portIndex = endpoint.lastIndexOf("@");

        if (portIndex != -1)
        {
            // If the characters following the "@" are numeric, then assume
            // it's a valid port and strip the @ and the port number from
            // the endpoint.

            try
            {
                int port = new Integer(endpoint.substring(portIndex + 1)).
                    intValue();
                endpointNoPort = endpoint.substring(0, portIndex);
            }
            catch (NumberFormatException e)
            {
                // Do nothing - Not valid port so don't remove from endpoint
            }
        }

        return endpointNoPort;
    }
    
    /***********************************************************************/
    /* Description:                                                        */
    /*   This method validates that the requesting machine has the         */
    /*   required trust to submit a service request.                       */
    /*                                                                     */
    /* Input:  Required trust level                                        */
    /*         Service name                                                */
    /*         Request string (submitted to the service)                   */
    /*         Name of the service machine (it's machine identifier) which */
    /*           is used in the error message if has insufficient trust    */
    /*         Request information                                         */
    /*                                                                     */
    /* Returns:                                                            */
    /*   STAFResult.rc = the return code (STAFResult.Ok if successful)     */
    /*   STAFResult.result = blank if successful                           */
    /*                       a detailed error message if not successful    */
    /*                                                                     */
    /* Note:  Each time a new service interface level is added, must add   */
    /*        another validateTrust method overloaded to support the new   */
    /*        service interface level.                                     */
    /***********************************************************************/
    public static STAFResult validateTrust(
        int requiredTrustLevel, String service, String request,
        String localMachine, STAFServiceInterfaceLevel30.RequestInfo info)
    {
        if (info.trustLevel < requiredTrustLevel)
        {
            // Strip the port from the machine's endpoint, if present.

            String endpoint = stripPortFromEndpoint(info.endpoint);

            return new STAFResult(
                STAFResult.AccessDenied,
                "Trust level " + requiredTrustLevel + " required for the " +
                service + " service's " + request + " request\n" +
                "Requester has trust level " + info.trustLevel +
                " on machine " + localMachine + "\nRequesting machine: " +
                 endpoint + " (" + info.physicalInterfaceID + ")" +
                "\nRequesting user   : " + info.user);
        }
    
        return new STAFResult(STAFResult.Ok);
    }
    
    /***********************************************************************/
    /* Description:                                                        */
    /*   This method resolves any STAF variables that are contained within */
    /*   the string passed in by submitting a                              */
    /*       RESOLVE REQUEST <request#> STRING <value>                     */
    /*   request to the VAR service on the local system.                   */
    /*   The variables will be resolved using the originating handle's pool*/
    /*   associated with the specified request number, the local system's  */
    /*   shared pool, and the local system's system pool.                  */
    /*                                                                     */
    /* Input:  String that may contain STAF variables to be resolved       */
    /*         STAF handle                                                 */
    /*         Request number                                              */
    /*                                                                     */
    /* Returns:                                                            */
    /*   STAFResult.rc = the return code (STAFResult.Ok if successful)     */
    /*   STAFResult.result = the resolved value if successful              */
    /*                       an error message if not successful            */
    /***********************************************************************/
    public static STAFResult resolveRequestVar(
        String value, STAFHandle handle, int requestNumber)
    {
        if (value.indexOf("{") != -1)
        {
            // The string may contains STAF variables
            
            STAFResult resolvedResult = handle.submit2(
                "local", "VAR", "RESOLVE REQUEST " + requestNumber +
                " STRING " + STAFUtil.wrapData(value));

            return resolvedResult;
        }

        return new STAFResult(STAFResult.Ok, value);
    }
    
    /***********************************************************************/
    /* Description:                                                        */
    /*   This method resolves any STAF variables that are contained within */
    /*   the string passed in by submitting a                              */
    /*       RESOLVE REQUEST <request#> STRING <value>                     */
    /*   request to the VAR service on the local system.                   */
    /*   The variables will be resolved using the originating handle's pool*/
    /*   associated with the specified request number, the local system's  */
    /*   shared pool, and the local system's system pool.                  */
    /*   Then it checks if the resolved value is an integer and returns an */
    /*   error if it's not an integer.                                     */
    /*                                                                     */
    /* Input:  Option name whose value is being resolved                   */
    /*         Value that may contain STAF variables to be resolved        */
    /*         STAF handle                                                 */
    /*         Request number                                              */
    /*                                                                     */
    /* Returns:                                                            */
    /*   STAFResult.rc = the return code (STAFResult.Ok if successful)     */
    /*   STAFResult.result = the resolved option value if successful       */
    /*                       an error message if not successful            */
    /***********************************************************************/
    public static STAFResult resolveRequestVarAndCheckInt(
        String option, String value, STAFHandle handle, int requestNumber)
    {
        STAFResult resolvedValue = resolveRequestVar(
            value, handle, requestNumber);

        if (resolvedValue.rc != STAFResult.Ok) return resolvedValue;

        try
        {
            Integer.parseInt(resolvedValue.result);
        }
        catch (NumberFormatException e)
        {
            return new STAFResult(
                STAFResult.InvalidValue, option +
                " value must be an Integer.  " +
                option + "=" + resolvedValue.result);
        }

        return resolvedValue;
    }

    /***********************************************************************/
    /* Description:                                                        */
    /*   This method resolves any STAF variables that are contained within */
    /*   the string passed in by submitting a                              */
    /*       RESOLVE STRING <value>                                        */
    /* request to the VAR service on the local system.                     */
    /*                                                                     */
    /* Input:  String that may contain STAF variables to be resolved       */
    /*         STAF handle                                                 */
    /*                                                                     */
    /* Returns:                                                            */
    /*   STAFResult.rc = the return code (STAFResult.Ok if successful)     */
    /*   STAFResult.result = the resolved string if successful             */
    /*                       an error message if not successful            */
    /***********************************************************************/
    public static STAFResult resolveInitVar(String value, STAFHandle handle)
    {
        if (value.indexOf("{") != -1)
        {
            // The string may contains STAF variables

            STAFResult resolvedResult = handle.submit2(
                "local", "VAR", "RESOLVE STRING " + STAFUtil.wrapData(value));

            return resolvedResult;
        }

        return new STAFResult(STAFResult.Ok, value);
    }

    /***********************************************************************/
    /* Description:                                                        */
    /*   This method resolves any STAF variables that are contained within */
    /*   the string passed in by submitting a                              */
    /*       RESOLVE STRING <value>                                        */
    /*   request to the VAR service on the local system.                   */
    /*   Then it checks if the resolved value is an integer and returns an */
    /*   error if it's not an integer.                                     */
    /*                                                                     */
    /* Input:  Option name whose value is being resolved                   */
    /*         Value for the option that may contain STAF variables        */
    /*         STAF handle                                                 */
    /*                                                                     */
    /* Returns:                                                            */
    /*   STAFResult.rc = the return code (STAFResult.Ok if successful)     */
    /*   STAFResult.result = the resolved option value if successful       */
    /*                       an error message if not successful            */
    /***********************************************************************/
    public static STAFResult resolveInitVarAndCheckInt(
        String option, String value, STAFHandle handle)
    {
        STAFResult resolvedValue = resolveInitVar(value, handle);

        if (resolvedValue.rc != STAFResult.Ok) return resolvedValue;

        try
        {
            Integer.parseInt(resolvedValue.result);
        }
        catch (NumberFormatException e)
        {
            return new STAFResult(
                STAFResult.InvalidValue, option + " value must be numeric.  " +
                option + "=" + resolvedValue.result);
        }

        return resolvedValue;
    }

    /***********************************************************************/
    /* Description:                                                        */
    /*   This method gets the version of STAF (or of a STAF service)       */
    /*   running on a specified machine by submitting a VERSION request to */
    /*   either the MISC service (to get the version of STAF) or to another*/
    /*   STAF service on the specified machine.  Then it checks if the     */
    /*   version meets the minimum required version specified by the       */
    /*   minRequiredVersion argument.  If the version is at or above the   */
    /*   required version, STAFResult.Ok is returned in the rc and the     */
    /*   version running on the specified machine is returned in the       */
    /*   result.  If the version is lower than the mininum required        */
    /*   version, STAFResult.InvalidSTAFVersion is returned in the rc with */
    /*   an error message returned in the result.  If another error occurs */
    /*   (e.g. RC 16 if the machine is not currently running STAF, etc.),  */
    /*   that error will be returned.                                      */
    /*                                                                     */
    /* Input:  1) machine: endpoint of the machine whose STAF or STAF      */
    /*            service version is to be compared                        */
    /*         2) handle: STAF handle to use to submit the request         */
    /*         3) minRequiredVersion:  The minimum required version.       */
    /*            The version must have the following format unless it's   */
    /*            blank or "<N/A>", which equates to "no version" and is   */
    /*            internally represented as 0.0.0.0                        */
    /*                                                                     */
    /*              a[.b[.c[.d]]] [text]                                   */
    /*                                                                     */
    /*            where:                                                   */
    /*            - a, b, c, and d (if specified) are numeric              */
    /*            - text is separated by one or more spaces from the       */
    /*              version numbers                                        */
    /*                                                                     */
    /*           Versions are compared as follows:                         */
    /*           a) The numeric versions (a[.b[.c[.d]]]) are numerically   */
    /*              compared.                                              */
    /*           b) If the numeric versions are "equal", then the [text]   */
    /*              values are compared using a case-insensitive string    */
    /*              compare.  Except, note that no text is considered      */
    /*              GREATER than any text (e.g."3.1.0" > "3.1.0 Beta 1").  */
    /*                                                                     */
    /*           Examples:                                                 */
    /*             "3" = "3.0" = "3.0.0" = "3.0.0.0"                       */
    /*             "3.0.0" < "3.1.0"                                       */
    /*             "3.0.2" < "3.0.3"                                       */
    /*             "3.0.0" < "3.1"                                         */
    /*             "3.0.9" < "3.0.10"                                      */
    /*             "3.1.0 Beta 1" < "3.1.0"                                */
    /*             "3.1.0 Alpha 1" < "3.1.0 Beta 1"                        */
    /*                                                                     */
    /*         4) service: Name of the service for which you want the      */
    /*            version of.  Optional.  Defaults to "MISC" which means   */
    /*            that you want to compare the version of STAF running on  */
    /*            the machine. Or, you can specify the name of a STAF      */
    /*            service (such as STAX, Event, Cron, etc.) that implements*/
    /*            a VERSION request and follows the STAF version format    */
    /*            requirements.                                            */
    /*                                                                     */
    /* Returns:                                                            */
    /*   STAFResult.rc = the return code (STAFResult.Ok if successful)     */
    /*   STAFResult.result = the resolved option value if successful       */
    /*                       an error message if not successful            */
    /***********************************************************************/
    public static STAFResult compareSTAFVersion(
        String machine, STAFHandle handle, String minRequiredVersion)
    {
        // The default is to check the version of STAF running (which is
        // done by submitting a VERSION request to the MISC service)
        return compareSTAFVersion(
            machine, handle, minRequiredVersion, "MISC");
    }

    public static STAFResult compareSTAFVersion(
        String machine, STAFHandle handle, String minRequiredVersion,
        String service)
    {
        STAFResult res = handle.submit2(
            machine, service, "VERSION");

        if (res.rc != STAFResult.Ok)
        {
            return new STAFResult(
                res.rc,
                "Request VERSION submitted to the " + service +
                " service on machine " + machine + " failed." +
                "  Additional info: " + res.result);
        }
        else
        {
            STAFVersion version;
            STAFVersion minReqVersion;

            try
            {
                version = new STAFVersion(res.result);
            }
            catch (NumberFormatException e)
            {
                // Should never happen
                return new STAFResult(
                    STAFResult.InvalidValue,
                    "Invalid value specified for the version: " +
                    res.result + ", Exception info: " + e.toString());
            }

            try
            {
                minReqVersion = new STAFVersion(minRequiredVersion);
            }
            catch (NumberFormatException e)
            {
                return new STAFResult(
                    STAFResult.InvalidValue,
                    "Invalid value specified for the minimum required " +
                    "version: " + minRequiredVersion +
                    ", Exception info: " + e.toString());
            }

            if (version.compareTo(minReqVersion) < 0)
            {
                String servMsg = service + " service";

                if (service.equalsIgnoreCase("MISC"))
                    servMsg = "STAF";

                return new STAFResult(
                    STAFResult.InvalidSTAFVersion,
                    "Machine " + machine + " is running " + servMsg +
                    " Version " + version + ".  Version " +
                    minRequiredVersion + " or later is required.");
            }
            else
            {
                return new STAFResult(STAFResult.Ok, version.toString());
            }
        }
    }
    
    /***********************************************************************/
    /* Description:                                                        */
    /*   This method returns the data with a privacy delimiters            */
    /*                                                                     */
    /* Input:  A string.   For example:  "secret"                          */
    /*                                                                     */
    /* Returns:                                                            */
    /*   String with privacy delimiters added.                             */
    /*                     For example:  "!!@secret@!!"                    */
    /***********************************************************************/
    public static String addPrivacyDelimiters(String data)
    {
        return STAFUtilAddPrivacyDelimiters(data);
    }
    
    /***********************************************************************/
    /* Description:                                                        */
    /*   This method removes any privacy delimiters from the data          */
    /*                                                                     */
    /* Input:  A string.   For example:  "!!@secret@!!"                    */
    /*                                                                     */
    /*         An integer containing the number of levels of privacy data  */
    /*         to remove (optional).  The default is 0 which indicates to  */
    /*         remove all levels of privacy data.                          */
    /* Returns:                                                            */
    /*   String with privacy delimiters removed.                           */
    /*                     For example:  "secret"                          */
    /***********************************************************************/
    
    public static String removePrivacyDelimiters(String data)
    {
        return STAFUtil.removePrivacyDelimiters(data, 0);
    }
    
    public static String removePrivacyDelimiters(String data, int numLevels)
    {
        return STAFUtilRemovePrivacyDelimiters(data, numLevels);
    }
    
    /***********************************************************************/
    /* Description:                                                        */
    /*   This method masks any private data indicated by the privacy       */
    /*   delimiters by replacing the private data with asterisks.          */
    /*                                                                     */
    /* Input:  A string.   For example:  "!!@secret@!!"                    */
    /*                                                                     */
    /* Returns:                                                            */
    /*   String with privacy prefixes removed.                             */
    /*                     For example:  "************"                    */
    /***********************************************************************/
    public static String maskPrivateData(String data)
    {
        return STAFUtilMaskPrivateData(data);
    }
    
    /***********************************************************************/
    /* Description:                                                        */
    /*   This method escapes all privacy delimiters found in the data and  */
    /*   returns the updated data.                                         */
    /*                                                                     */
    /* Input:  A string.   For example:  "!!@secret@!!"                    */
    /*                                                                     */
    /* Returns:                                                            */
    /*         A string with all privacy delimiters escaped.               */
    /*         For example:  "^!!@secret^@!!"                              */
    /***********************************************************************/
    public static String escapePrivacyDelimiters(String data)
    {
        return STAFUtilEscapePrivacyDelimiters(data);
    }

    /************************/
    /* All the native stuff */
    /************************/

    private static native void initialize();
    private static native String STAFUtilAddPrivacyDelimiters(String data);
    private static native String STAFUtilRemovePrivacyDelimiters(
        String data, int numLevels);
    private static native String STAFUtilMaskPrivateData(String data);
    private static native String STAFUtilEscapePrivacyDelimiters(String data);

    // Static initializer - called first time class is loaded.
    static
    {
        System.loadLibrary("STAF");
        System.loadLibrary("JSTAF");
        initialize();
    }
}
