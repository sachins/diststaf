
/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2004                                              */
/*                                                                           */
/* This software is licensed under the Common Public License (CPL) V1.0.     */
/*****************************************************************************/

package com.ibm.staf;

import java.util.*;

public class STAFMarshallingContext
{
    public static final int UNMARSHALLING_DEFAULTS = 0;
    public static final int IGNORE_INDIRECT_OBJECTS = 1;

    public static boolean isMarshalledData(String someData)
    {
        return someData.startsWith("@SDT/");
    }

    public STAFMarshallingContext()
    { /* Do nothing */ }

    public STAFMarshallingContext(Object obj)
    {
        rootObject = obj;
    }

    STAFMarshallingContext(Object obj, Map mapClassMap)
    {
        rootObject = obj;
        this.mapClassMap = mapClassMap;
    }

    public void setMapClassDefinition(STAFMapClassDefinition mapClassDef)
    {
        mapClassMap.put(mapClassDef.name(),
                        mapClassDef.getMapClassDefinitionObject());
    }

    public STAFMapClassDefinition getMapClassDefinition(String mapClassName)
    {
        return new STAFMapClassDefinition((Map)mapClassMap.get(mapClassName));
    }

    public boolean hasMapClassDefinition(String mapClassName)
    {
        return mapClassMap.containsKey(mapClassName);
    }

    Map getMapClassMap()
    {
        return Collections.unmodifiableMap(mapClassMap);
    }

    public Iterator mapClassDefinitionIterator()
    {
        return mapClassMap.keySet().iterator();
    }

    public void setRootObject(Object rootObject)
    {
        this.rootObject = rootObject;
    }

    public Object getRootObject()
    {
        return rootObject;
    }

    public Object getPrimaryObject()
    {
        if (mapClassMap.size() == 0) return rootObject;

        return this;
    }

    public String marshall()
    {
        return marshall(this, this);
    }

    public static String marshall(Object object, STAFMarshallingContext context)
    {
        if (object == null)
        {
            return NONE_MARKER;
        }
        if (object instanceof List)
        {
            List list = (List)object;
            Iterator iter = list.iterator();
            StringBuffer listData = new StringBuffer();

            while (iter.hasNext())
                listData.append(marshall(iter.next(), context));

            return LIST_MARKER + list.size() + ":" + listData.length() + ":" +
                   listData.toString();
        }
        else if (object instanceof Map)
        {
            Map map = (Map)object;

            // If a staf-map-class-name key exists in the map, make sure that
            // it's map class definition is provided in the marshalling context.
            // If it's not, then treat the map as a plain map object.

            boolean isMapClass = false;
            String mapClassName = "";

            if ((context != null) &&
                (context instanceof STAFMarshallingContext) &&
                (map.containsKey(MAP_CLASS_NAME_KEY)))
            {
                mapClassName = (String)map.get(MAP_CLASS_NAME_KEY);

                if (context.hasMapClassDefinition(mapClassName))
                {
                    isMapClass = true;
                }
            }

            if (isMapClass)
            {
                STAFMapClassDefinition mapClass =
                    context.getMapClassDefinition(mapClassName);
                Iterator iter = mapClass.keyIterator();
                StringBuffer result = new StringBuffer(
                    ":" + mapClassName.length() + ":" + mapClassName);

                while (iter.hasNext())
                {
                    Map key = (Map)iter.next();
                    result.append(marshall(map.get(key.get("key")), context));
                }

                return MC_INSTANCE_MARKER + ":" + result.length() + ":" +
                       result.toString();
            }
            else
            {
                Iterator iter = map.keySet().iterator();
                StringBuffer mapData = new StringBuffer();

                while (iter.hasNext())
                {
                    Object key = iter.next();
                    mapData.append(":" + key.toString().length() + ":" +
                        key.toString() + marshall(map.get(key), context));
                }

                return MAP_MARKER + ":" + mapData.length() + ":" +
                       mapData.toString();
            }
        }
        else if (object instanceof STAFMarshallingContext)
        {
            STAFMarshallingContext mc = (STAFMarshallingContext)object;
            Map classMap = (Map)mc.getMapClassMap();

            if (classMap.size() == 0)
            {
                return marshall(mc.getRootObject(), context);
            }
            else
            {
                Map contextMap = new HashMap();

                contextMap.put(MAP_CLASS_MAP_KEY, classMap);

                // Note: We can't simply put the root object as a map key like
                //       "root-object" and then marshall the whole map, as in
                //       the unmarshalling routines, we need to be able to
                //       unmarshall the root object in the context of the
                //       map-class-map.

                String data = marshall(contextMap, context) +
                              marshall(mc.getRootObject(),
                                       (STAFMarshallingContext)object);

                return CONTEXT_MARKER + ":" + data.length() + ":" + data;
            }
        }
        // else if (object has method "stafMarshall")

        String objString = object.toString();

        return "@SDT/$S:" + objString.length() + ":" + objString;
    }

    public static STAFMarshallingContext unmarshall(String marshalledObject)
    {
        return unmarshall(marshalledObject, new STAFMarshallingContext(),
                          UNMARSHALLING_DEFAULTS);
    }

    public static STAFMarshallingContext unmarshall(String marshalledObject,
                                                    int flags)
    {
        return unmarshall(marshalledObject, new STAFMarshallingContext(), flags);
    }

    public static STAFMarshallingContext unmarshall(
        String data, STAFMarshallingContext context)
    {
        return unmarshall(data, context, UNMARSHALLING_DEFAULTS);
    }

    public static STAFMarshallingContext unmarshall(
        String data, STAFMarshallingContext context, int flags)
    {
        if (data.startsWith(NONE_MARKER))
        {
            return new STAFMarshallingContext();
        }
        else if (data.startsWith(SCALAR_MARKER))
        {
            int colonIndex = data.indexOf(':');
            colonIndex = data.indexOf(':', colonIndex + 1);

            String theString = data.substring(colonIndex + 1);

            if (theString.startsWith(MARSHALLED_DATA_MARKER) &&
                ((flags & IGNORE_INDIRECT_OBJECTS) != IGNORE_INDIRECT_OBJECTS))
            {
                return unmarshall(theString, context, flags);
            }
            else
            {
                return new STAFMarshallingContext(
                    data.substring(colonIndex + 1));
            }
        }
        else if (data.startsWith(LIST_MARKER))
        {
            int colonIndex = data.indexOf(':');
            int numItems = parseInt(data.substring(6, colonIndex), 0);
            int dataIndex = data.indexOf(':', colonIndex + 1) + 1;
            List list = new LinkedList();

            for (int i = 0; i < numItems; ++i)
            {
                int colonIndex1 = data.indexOf(':', dataIndex);
                int colonIndex2 = data.indexOf(':', colonIndex1 + 1);
                int itemLength = parseInt(data.substring(colonIndex1 + 1,
                                                         colonIndex2), 0);
                list.add(unmarshall(
                            data.substring(dataIndex,
                                           colonIndex2 + itemLength + 1),
                            context, flags)
                         .getPrimaryObject());

                dataIndex = colonIndex2 + itemLength + 1;
            }

            return new STAFMarshallingContext(list);
        }
        else if (data.startsWith(MAP_MARKER))
        {
            int colonIndex = data.indexOf(':');
            int dataIndex = data.indexOf(':', colonIndex + 1) + 1;
            Map map = new HashMap();

            while (dataIndex < data.length())
            {
                // Get key first

                int keyColonIndex1 = data.indexOf(':', dataIndex);
                int keyColonIndex2 = data.indexOf(':', keyColonIndex1 + 1);
                int keyLength = parseInt(data.substring(keyColonIndex1 + 1,
                                                        keyColonIndex2), 0);
                String key = data.substring(keyColonIndex2 + 1,
                                            keyColonIndex2 + 1 + keyLength);

                dataIndex = keyColonIndex2 + 1 + keyLength;

                // Now, get the object

                int colonIndex1 = data.indexOf(':', dataIndex);
                int colonIndex2 = data.indexOf(':', colonIndex1 + 1);
                int itemLength = parseInt(data.substring(colonIndex1 + 1,
                                                         colonIndex2), 0);
                map.put(key, unmarshall(
                                 data.substring(dataIndex,
                                                colonIndex2 + itemLength + 1),
                                 context, flags)
                             .getPrimaryObject());

                dataIndex = colonIndex2 + itemLength + 1;
            }

            return new STAFMarshallingContext(map);
        }
        else if (data.startsWith(MC_INSTANCE_MARKER))
        {
            int colonIndex = data.indexOf(':');
            int colonIndex2 = data.indexOf(':', colonIndex + 1);

            colonIndex = data.indexOf(':', colonIndex2 + 1);
            colonIndex2 = data.indexOf(':', colonIndex + 1);

            int mapClassNameLength = parseInt(data.substring(colonIndex + 1,
                                                             colonIndex2), 0);
            String mapClassName = data.substring(colonIndex2 + 1,
                                      colonIndex2 + 1 + mapClassNameLength);
            int dataIndex = colonIndex2 + 1 + mapClassNameLength;
            Map map = new HashMap();

            map.put(MAP_CLASS_NAME_KEY, mapClassName);

            STAFMapClassDefinition mapClass =
                context.getMapClassDefinition(mapClassName);
            Iterator iter = mapClass.keyIterator();

            while (dataIndex < data.length())
            {
                colonIndex = data.indexOf(':', dataIndex);
                colonIndex2 = data.indexOf(':', colonIndex + 1);

                int itemLength = parseInt(data.substring(colonIndex + 1,
                                                         colonIndex2), 0);
                map.put(((Map)iter.next()).get("key"),
                        unmarshall(
                                 data.substring(dataIndex,
                                                colonIndex2 + itemLength + 1),
                                 context, flags)
                             .getPrimaryObject());

                dataIndex = colonIndex2 + itemLength + 1;
            }

            return new STAFMarshallingContext(map);
        }
        else if (data.startsWith(CONTEXT_MARKER))
        {
            int colonIndex = data.indexOf(':');
            int contextIndex = data.indexOf(':', colonIndex + 1) + 1;
            int contextLength = parseInt(data.substring(colonIndex + 1,
                                                        contextIndex - 1), 0);

            colonIndex = data.indexOf(':', contextIndex);
            int mapIndex = contextIndex;
            int mapDataIndex = data.indexOf(':', colonIndex + 1) + 1;
            int mapLength = parseInt(data.substring(colonIndex + 1,
                                                    mapDataIndex - 1), 0);
            Map contextMap =
                (Map)unmarshall(data.substring(mapIndex,
                                               mapDataIndex + mapLength),
                                context, flags)
                     .getPrimaryObject();
            Map mapClassMap = (Map)contextMap.get(MAP_CLASS_MAP_KEY);
            STAFMarshallingContext newContext =
                               new STAFMarshallingContext(null, mapClassMap);

            colonIndex = data.indexOf(':', mapDataIndex + mapLength);

            int rootObjIndex = mapDataIndex + mapLength;
            int rootObjDataIndex = data.indexOf(':', colonIndex + 1) + 1;
            int rootObjLength = parseInt(data.substring(colonIndex + 1,
                                    rootObjDataIndex - 1), 0);

            newContext.setRootObject(
                unmarshall(data.substring(rootObjIndex,
                                          rootObjDataIndex + rootObjLength),
                           newContext, flags)
                .getPrimaryObject());

            return newContext;
        }
        else if (data.startsWith(MARSHALLED_DATA_MARKER))
        {
            // Here, we don't know what the type is

            return new STAFMarshallingContext(new String(data));
        }
        else
        {
            return new STAFMarshallingContext(new String(data));
        }
    }

    private static int parseInt(String data, int theDefault)
    {
        int theValue = theDefault;

        try
        {
            theValue = Integer.parseInt(data);
        }
        catch (NumberFormatException nfe)
        { /* Do Nothing */ }

        return theValue;
    }

    private static String quoteString(String input)
    {
        if (input.indexOf("'") == -1)
            return "'" + input + "'";

        if (input.indexOf("\"") == -1)
            return "\"" + input + "\"";

        StringTokenizer tokens = new StringTokenizer(input, "'");

        String output = "'" + tokens.nextToken();

        while (tokens.hasMoreTokens())
            output = output + "\'" + tokens.nextToken();

        return output + "'";
    }

    public String toString()
    {
        return formatObject(rootObject, this, 0, 0);
    }

    public String toString(int flags)
    {
        return formatObject(rootObject, this, 0, flags);
    }

    public static String formatObject(Object obj)
    {
        return formatObject(obj, null, 0, 0);
    }

    public static String formatObject(Object obj, int flags)
    {
        return formatObject(obj, null, 0, flags);
    }

    static String formatObject(Object obj, STAFMarshallingContext context,
                               int indentLevel, int flags)
    {
        String lineSep = System.getProperty("line.separator");
        StringBuffer output = new StringBuffer();

        if (obj instanceof List)
        {
            List list = (List)obj;

            output.append("[");

            ++indentLevel;

            if (list.size() > 0) output.append(lineSep);

            // Format each object

            for (Iterator iter = list.iterator(); iter.hasNext();)
            {
                Object thisObj = iter.next();

                if ((thisObj instanceof List) ||
                    (thisObj instanceof Map) ||
                    (thisObj instanceof STAFMarshallingContext))
                {
                    output.append(
                        SPACES.substring(0, indentLevel * INDENT_DELTA));

                    output.append(formatObject(thisObj, context, indentLevel,
                                               flags));
                }
                else
                {
                    output.append(
                        SPACES.substring(0, indentLevel * INDENT_DELTA));

                    if (thisObj == null)
                        output.append(NONE_STRING);
                    else
                        output.append(thisObj.toString());
                }

                if (iter.hasNext()) output.append(ENTRY_SEPARATOR);

                output.append(lineSep);
            }

            --indentLevel;

            if (list.size() > 0)
                output.append(SPACES.substring(0, indentLevel * INDENT_DELTA));

            output.append("]");
        }
        else if (obj instanceof Map)
        {
            Map map = (Map)obj;

            output.append("{");

            ++indentLevel;

            if (map.size() > 0) output.append(lineSep);

            // Check if the map object has a map class key and if the context
            // is valid and contains a map class definition for this map class.
            // If not, treat as a plain map class.

            if (map.containsKey(MAP_CLASS_NAME_KEY) &&
                (context != null) &&
                (context instanceof STAFMarshallingContext) &&
                (context.hasMapClassDefinition(
                    (String)map.get(MAP_CLASS_NAME_KEY))))
            {
                STAFMapClassDefinition mapClass =
                    context.getMapClassDefinition(
                        (String)map.get(MAP_CLASS_NAME_KEY));

                // Determine maximum key length

                int maxKeyLength = 0;

                for (Iterator iter = mapClass.keyIterator(); iter.hasNext();)
                {
                    Map theKey = (Map)iter.next();
                    String theKeyString = (String)theKey.get("key");

                    if (theKey.containsKey(DISPLAY_NAME_KEY))
                        theKeyString = (String)theKey.get(DISPLAY_NAME_KEY);

                    if (theKeyString.length() > maxKeyLength)
                        maxKeyLength = theKeyString.length();
                }

                // Now print each object in the map

                for (Iterator iter = mapClass.keyIterator(); iter.hasNext();)
                {
                    Map theKey = (Map)iter.next();
                    String theKeyString = (String)theKey.get("key");

                    if (theKey.containsKey(DISPLAY_NAME_KEY))
                        theKeyString = (String)theKey.get(DISPLAY_NAME_KEY);

                    output.append(SPACES.substring(0,
                                                   indentLevel * INDENT_DELTA))
                          .append(theKeyString)
                          .append(SPACES.substring(0, maxKeyLength -
                                                   theKeyString.length()))
                          .append(": ");

                    Object thisObj = map.get(theKey.get("key"));

                    if ((thisObj instanceof List) ||
                        (thisObj instanceof Map) ||
                        (thisObj instanceof STAFMarshallingContext))
                    {
                        output.append(
                            formatObject(thisObj, context, indentLevel, flags));
                    }
                    else if (thisObj == null)
                    {
                        output.append(NONE_STRING);
                    }
                    else
                    {
                        output.append(thisObj.toString());
                    }

                    if (iter.hasNext()) output.append(ENTRY_SEPARATOR);

                    output.append(lineSep);
                }
            }
            else
            {
                // Determine maximum key length

                int maxKeyLength = 0;

                for (Iterator iter = map.keySet().iterator(); iter.hasNext();)
                {
                    String theKeyString = (String)iter.next();

                    if (theKeyString.length() > maxKeyLength)
                        maxKeyLength = theKeyString.length();
                }

                // Now print each object in the map

                for (Iterator iter = map.keySet().iterator(); iter.hasNext();)
                {
                    String theKeyString = (String)iter.next();

                    output.append(SPACES.substring(0,
                                                   indentLevel * INDENT_DELTA))
                          .append(theKeyString)
                          .append(SPACES.substring(0, maxKeyLength -
                                                   theKeyString.length()))
                          .append(": ");

                    Object thisObj = map.get(theKeyString);

                    if ((thisObj instanceof List) ||
                        (thisObj instanceof Map) ||
                        (thisObj instanceof STAFMarshallingContext))
                    {
                        output.append(
                            formatObject(thisObj, context, indentLevel, flags));
                    }
                    else if (thisObj == null)
                    {
                        output.append(NONE_STRING);
                    }
                    else
                    {
                        output.append(thisObj.toString());
                    }

                    if (iter.hasNext()) output.append(ENTRY_SEPARATOR);

                    output.append(lineSep);
                }
            }

            --indentLevel;

            if (map.size() > 0)
                output.append(SPACES.substring(0, indentLevel * INDENT_DELTA));

            output.append("}");
        }
        else if (obj instanceof STAFMarshallingContext)
        {
            STAFMarshallingContext inputContext = (STAFMarshallingContext)obj;

            return formatObject(inputContext.getRootObject(), inputContext,
                                indentLevel, flags);
        }
        else if (obj == null) return NONE_STRING;
        else return obj.toString();

        return output.toString();
    }

    // Class data

    private static final String MARSHALLED_DATA_MARKER = new String("@SDT/");
    private static final String NONE_MARKER = new String("@SDT/$0:0:");
    private static final String SCALAR_MARKER = new String("@SDT/$");
    private static final String LIST_MARKER = new String("@SDT/[");
    private static final String MAP_MARKER = new String("@SDT/{");
    private static final String MC_INSTANCE_MARKER = new String("@SDT/%");
    private static final String CONTEXT_MARKER = new String("@SDT/*");
    private static final String NONE_STRING = new String("<None>");
    private static final String DISPLAY_NAME_KEY = new String("display-name");
    private static final String MAP_CLASS_MAP_KEY = new String("map-class-map");
    private static final String MAP_CLASS_NAME_KEY =
        new String("staf-map-class-name");
    private static final String ENTRY_SEPARATOR = new String("");
    // 80 spaces
    private static final String SPACES = new String(
        "                                         " + 
        "                                         ");
    private static final int INDENT_DELTA = 2;

    private Map mapClassMap = new HashMap();
    private Object rootObject = null;
}
