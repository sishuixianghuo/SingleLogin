package com.example;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;



public class TestJava {
    public static void main(String[] args) throws Exception {

        File file = new File("/Users/liushenghan/Downloads/test.jar");
        URL url = file.toURI().toURL();
        ClassLoader loader = new URLClassLoader(new URL[]{url});

        Class<?> cls = loader.loadClass("com.test.Input");
        Object ob = cls.newInstance();
        Method method = cls.getMethod("ab");
        method.invoke(cls);
    }
}
