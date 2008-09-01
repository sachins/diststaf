/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2001                                              */
/*                                                                           */
/* This software is licensed under the Common Public License (CPL) V1.0.     */
/*****************************************************************************/

package com.ibm.staf;

import java.util.*;

// STAFMapClassDefinition - This class provides the definition for a map class
//                          used to marshall "well-defined" map

public class STAFMapClassDefinition
{
    // Constructors
    public STAFMapClassDefinition(String name)
    {
        fMapClassDef.put("name", name);
        fMapClassDef.put("keys", new LinkedList());
    }

    STAFMapClassDefinition(Map mapClassDef)
    {
        if (mapClassDef == null)
        {
            fMapClassDef.put("name", new String());
            fMapClassDef.put("keys", new LinkedList());
        }
        else
        {
            fMapClassDef = mapClassDef;
        }
    }

    public Map createInstance()
    {
        Map mapInstance = new TreeMap();

        mapInstance.put("staf-map-class-name", fMapClassDef.get("name"));

        return mapInstance;
    }

    public void addKey(String keyName)
    {
        Map aKey = new TreeMap();

        aKey.put("key", keyName);

        List keyList = (List)fMapClassDef.get("keys");

        keyList.add(aKey);
    }

    public void addKey(String keyName, String displayName)
    {
        Map aKey = new TreeMap();

        aKey.put("key", keyName);
        aKey.put("display-name", displayName);

        List keyList = (List)fMapClassDef.get("keys");

        keyList.add(aKey);
    }

    public void setKeyProperty(String keyName, String property, String value)
    {
        for (Iterator iter = keyIterator(); iter.hasNext();)
        {
            Map thisKey = (Map)iter.next();

            if (thisKey.get("key").equals(keyName))
                thisKey.put(property, value);
        }
    }

    public Iterator keyIterator()
    {
        List keyList = (List)fMapClassDef.get("keys");

        return keyList.iterator();
    }

    public String name() { return (String)fMapClassDef.get("name"); }

    Object getMapClassDefinitionObject() { return fMapClassDef; }

    private Map fMapClassDef = new TreeMap();
}
