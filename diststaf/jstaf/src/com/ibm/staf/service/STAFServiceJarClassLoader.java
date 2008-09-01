/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2001                                              */
/*                                                                           */
/* This software is licensed under the Common Public License (CPL) V1.0.     */
/*****************************************************************************/

package com.ibm.staf.service;

import com.ibm.staf.*;
import java.util.jar.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.net.URL;
 
public class STAFServiceJarClassLoader extends ClassLoader
{
    static public final boolean DEBUG = false;

    STAFServiceJarClassLoader(JarFile jarFile, String jarLoc)
    {
        this.jarFile = jarFile;

        Manifest manifest = null;

        try
        {
            manifest = jarFile.getManifest();
        }
        catch (IOException e)
        {
            // If we can't get the manifest, that's ok.  We shouldn't actually
            // get here, as STAFServiceHelper will generate an error before
            // instantiating us.
        }

        if (manifest == null) return;

        Attributes attrs;

        if (manifest.getEntries().containsKey(STAFServiceHelper.STAF_ENTRY3))
        {
            attrs = manifest.getAttributes(STAFServiceHelper.STAF_ENTRY3);
        }
        else if (manifest.getEntries().containsKey(STAFServiceHelper.STAF_ENTRY))
        {
            attrs = manifest.getAttributes(STAFServiceHelper.STAF_ENTRY);
        }
        else
        {
            return;
        }

        if (!attrs.containsKey(new Attributes.Name(
            STAFServiceHelper.STAF_SERVICE_JARS)))
        {
            return;
        }

        String theJars = attrs.getValue(STAFServiceHelper.STAF_SERVICE_JARS);
        StringTokenizer jarTokenizer = new StringTokenizer(theJars);

        while (jarTokenizer.hasMoreTokens())
        {
            String thisJar = jarTokenizer.nextToken();

            if (!thisJar.toLowerCase().endsWith(".jar"))
                thisJar = thisJar + ".jar";

            JarEntry thisEntry = jarFile.getJarEntry("STAF-INF/jars/" +
                                                     thisJar);

            if (thisEntry != null)
            {
                try
                {
                    InputStream jarStream = jarFile.getInputStream(thisEntry);
                    byte [] buffer = new byte[1024];
                    FileOutputStream fileStream =
                        new FileOutputStream(jarLoc + "/" + thisJar);

                    for (int bytesRead = 0; bytesRead != -1;)
                    {
                        bytesRead = jarStream.read(buffer, 0, 1024);

                        if (bytesRead != -1)
                            fileStream.write(buffer, 0, bytesRead);
                    }

                    fileStream.close();
                    jarStream.close();

                    JarFile thisJarFile = new JarFile(jarLoc + "/" + thisJar);

                    subordinateJars.add(thisJarFile);
                }
                catch (IOException e)
                {
                    if (DEBUG)
                    {
                        System.out.println("Caught IOException processing " +
                                           "subordinate jar file:");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Class findClass(String name) throws ClassNotFoundException
    {
        if (DEBUG) System.out.println("Finding class: " + name);

        Class theClass = null;

        try
        {
            JarEntry classEntry = jarFile.getJarEntry("STAF-INF/classes/" +
                                                      name.replace('.', '/') +
                                                      ".class");
            if (classEntry != null)
            {
                InputStream classStream = jarFile.getInputStream(classEntry);
                byte [] classData = new byte[(int)classEntry.getSize()];
                int bytesToRead = classData.length;

                for (int bytesRead = 0; bytesToRead > 0;
                     bytesToRead -= bytesRead)
                {
                    bytesRead = classStream.read(classData,
                                                 classData.length - bytesToRead,
                                                 bytesToRead);
                }

                classStream.close();

                theClass = defineClass(name, classData, 0, classData.length);
            }
        }
        catch (IOException e)
        {
            if (DEBUG)
            {
                System.out.println("Caught IOException reading from " +
                                   "master jar file:");
                e.printStackTrace();
            }
        }

        Iterator iter = subordinateJars.iterator();

        while ((theClass == null) && iter.hasNext())
        {
            JarFile subJarFile = (JarFile)iter.next();

            if (DEBUG)
            {
                System.out.println("Trying subordinate jar: " +
                                   subJarFile.getName());
            }

            try
            {
                JarEntry classEntry = subJarFile.getJarEntry(
                                          name.replace('.', '/') + ".class");

                if (classEntry != null)
                {
                    InputStream classStream =
                                subJarFile.getInputStream(classEntry);
                    byte [] classData = new byte[(int)classEntry.getSize()];
                    int bytesToRead = classData.length;

                    for (int bytesRead = 0; bytesToRead > 0;
                         bytesToRead -= bytesRead)
                    {
                        bytesRead = classStream.read(classData,
                                                     classData.length -
                                                         bytesToRead,
                                                     bytesToRead);
                    }

                    classStream.close();

                    theClass = defineClass(name, classData, 0, classData.length);
                }
            }
            catch (IOException e)
            {
                if (DEBUG)
                {
                    System.out.println("Caught IOException reading from " +
                                       "subordinate jar file:");
                    e.printStackTrace();
                }
            }
        }

        if (theClass == null) throw new ClassNotFoundException(name);

        return theClass;
    }

    // Finds the resource with the given name. Class loader implementations
    // should override this method to specify where to find resources.
    // Parameters:  name - the resource name
    // Returns:     a URL for reading the resource, or null if the resource
    //              could not be found

    protected URL findResource(String name)
    {
        if (DEBUG) System.out.println("Finding Resource: " + name);

        JarEntry propEntry = jarFile.getJarEntry("STAF-INF/classes/" + name);

        if (propEntry != null)
        { 
                try
                {
                    return new URL("jar:file:///" +
                                   jarFile.getName().replace('\\', '/') +
                                   "!/STAF-INF/classes/" + name);
                }
                catch (java.net.MalformedURLException e)
                {
                    // Ignore this
                }
        }

        Iterator iter = subordinateJars.iterator();

        while (iter.hasNext())
        {
            JarFile subJarFile = (JarFile)iter.next();

            if (DEBUG)
            {
                System.out.println("Trying subordinate jar: " +
                                   subJarFile.getName());
            }

            propEntry = subJarFile.getJarEntry(name);

            if (propEntry != null)
            {
                try
                {
                    return new URL("jar:file:///" +
                                   subJarFile.getName().replace('\\', '/') +
                                   "!/" + name);
                }
                catch (java.net.MalformedURLException e)
                {
                    // Ignore this
                }
            }
        }

        return null;
    }

    JarFile jarFile = null;
    List subordinateJars = new ArrayList();
}

