package com.youxiang.shiro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: RiversLau
 * Date: 2018/1/3 11:37
 */
public class ClassUtils {

    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

    private static final ClassLoaderAccessor THREAD_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
        @Override
        protected ClassLoader doGetClassLoader() throws Throwable {
            return Thread.currentThread().getContextClassLoader();
        }
    };

    private static final ClassLoaderAccessor CLASS_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
        @Override
        protected ClassLoader doGetClassLoader() throws Throwable {
            return ClassUtils.class.getClassLoader();
        }
    };

    private static final ClassLoaderAccessor SYSTEM_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
        @Override
        protected ClassLoader doGetClassLoader() throws Throwable {
            return ClassLoader.getSystemClassLoader();
        }
    };

    /**
     * 按照以下顺序，当前线程ClassLoader、当前类ClassLoader、系统或应用ClassLoader来加载指定
     * 资源
     * @param name
     * @return
     */
    public static InputStream getResourceAsStream(String name) {
        InputStream is = THREAD_CL_ACCESSOR.getResourceStream(name);
        if (is == null) {
            log.trace("Resource [" + name + "] was not found via the thread context ClassLoader. " +
                    "Trying the current ClassLoader...");
            is = CLASS_CL_ACCESSOR.getResourceStream(name);
        }

        if (is == null) {
            log.trace("Resource [" + name + "] was not found via the current ClassLoader. " +
                    "Trying the system/application ClassLoader...");
            is = SYSTEM_CL_ACCESSOR.getResourceStream(name);
        }

        if (is == null) {
            log.trace("Resource [" + name + "] was not found via thread context, current, or " +
                    "system/application ClassLoader. All heuristics has been exhausted. Returning null.");
        }
        return is;
    }

    public static Class forName(String fqcn) throws UnknownClassException {

        Class clazz = THREAD_CL_ACCESSOR.loadClass(fqcn);
        if (clazz == null) {
            log.trace("Unable to load class [" + fqcn + "] from the thread context ClassLoader." +
                    "Trying the current ClassLoader...");
            clazz = CLASS_CL_ACCESSOR.loadClass(fqcn);
        }

        if (clazz == null) {
            log.trace("Unable to load class [" + fqcn + "] from the current ClassLoader. " +
                    "Trying the system/application ClassLoader...");
            clazz = SYSTEM_CL_ACCESSOR.loadClass(fqcn);
        }

        if (clazz == null) {
            String msg = "Unable to load class [" + fqcn + "] from the thread context, current, " +
                    "system/application ClassLoader. All heuristtics has been exhausted. Class could not be found.";
            throw new UnknownClassException(msg);
        }

        return clazz;
    }

    public static boolean isVailable(String fullyQualifiedClassName) {
        try {
            forName(fullyQualifiedClassName);
            return true;
        } catch (UnknownClassException e) {
            return false;
        }
    }

    public static Object newInstance(String name) {
        return newInstance(forName(name));
    }

    public static Object newInstance(String name, Object... args) {
        return newInstance(forName(name), args);
    }

    public static Object newInstance(Class clazz) {
        if (clazz == null) {
            String msg = "Class method parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new InstantiationException("Unable to instantiate class [" + clazz.getName() + "]", e);
        }
    }

    public static Object newInstance(Class clazz, Object... args) {
        Class[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        Constructor constructor = getConstructor(clazz, argTypes);
        return instantiate(constructor, args);
    }

    public static Constructor getConstructor(Class clazz, Class[] args) {
        try {
            return clazz.getConstructor(args);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException();
        }
    }

    public static Object instantiate(Constructor constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            String msg = "Unable to instantiate Permission instance with constructor [" + constructor + "]";
            throw new InstantiationException(msg);
        }
    }

    public static List<Method> getAnnotatedMethods(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> clazz = type;
        while (!Object.class.equals(clazz)) {
            Method[] currentClassMethods = clazz.getDeclaredMethods();
            for (final Method method : currentClassMethods) {
                if (annotation == null || method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    private interface ClassLoaderAccessor {
        Class loadClass(String fqcn);
        InputStream getResourceStream(String name);
    }

    private static abstract class ExceptionIgnoringAccessor implements ClassLoaderAccessor {

        public Class loadClass(String fqcn) {
            Class clazz = null;
            ClassLoader cl = getClassLoader();
            if (cl != null) {
                try {
                    clazz = cl.loadClass(fqcn);
                } catch (ClassNotFoundException e) {
                    log.warn("Unable to load clazz name [" + fqcn + "] from class loader [" + cl + "]");
                }
            }
            return clazz;
        }

        public InputStream getResourceStream(String name) {
            InputStream is = null;
            ClassLoader cl = getClassLoader();
            if (cl != null) {
                is = cl.getResourceAsStream(name);
            }
            return is;
        }

        protected final ClassLoader getClassLoader() {
            try {
                return doGetClassLoader();
            } catch (Throwable throwable) {
                log.debug("Unable to acquire ClassLoader.", throwable);
            }
            return null;
        }

        protected abstract ClassLoader doGetClassLoader() throws Throwable;
    }
}
