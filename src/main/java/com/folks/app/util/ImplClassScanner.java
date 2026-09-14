package com.folks.app.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ImplClassScanner {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(ImplClassScanner.class);
    
    public static List<Class<?>> findImplementingClasses(Class<?> sup, String[] packageNames) {
        List<Class<?>> impl = new ArrayList<>();
        
        try {
            List<Class> classes = scan(packageNames);
            for (Class<?> clazz : classes) {
                if (! Modifier.isAbstract(clazz.getModifiers())
                        && ! Modifier.isInterface(clazz.getModifiers())
                        && Modifier.isPublic(clazz.getModifiers())
                        && sup.isAssignableFrom(clazz)) {
                    
                    impl.add(clazz);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Error finding implementing classes of " + sup, e);
        }
        return impl;
    }
    
    public static List<Class> scan(String[] packageNames) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<Class> classes = new ArrayList();
        
        for (String packageName : packageNames) {
            String path = (packageName = packageName.trim()).replace('.', '/');

            Enumeration resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList();
            boolean jar = true;
            
            while (resources.hasMoreElements()) {
                URL resource = (URL)resources.nextElement();
                URLConnection urlConn = resource.openConnection();
                
                // For FAT jar.
                if (urlConn instanceof JarURLConnection) {
                    final JarFile jarFile = ((JarURLConnection)urlConn).getJarFile();
                    final Enumeration<JarEntry> entries = jarFile.entries();
                    String name = null;
                    
                    for (JarEntry jarEntry = null; entries.hasMoreElements()
                            && ((jarEntry = entries.nextElement()) != null);) {
                        
                        name = jarEntry.getName();

                        if (name.contains(".class")) {
                            name = name.substring(0, name.length() - 6).replace('/', '.');
                            if (name.contains(packageName)) {
                                classes.add(Class.forName(name));
                            }
                        }
                    }
                }
                else {
                    // Assuming it's a File URLConnection.
                    jar = false;
                    dirs.add(new File(resource.getFile()));
                }
            }
            if (! jar) {
                for (File directory : dirs) {
                    classes.addAll(findClasses(directory, packageName));
                }
            }
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Scanner scanned total {} classes from package: {}"
                , classes.size(), Arrays.toString(packageNames));
        }
        return classes;
    }
    
    /**
     * Recursive method used to find all classes in a given directory and sub directories.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class")) {
                Class<?> clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                classes.add(clazz);
            }
        }
        return classes;
    }
}
