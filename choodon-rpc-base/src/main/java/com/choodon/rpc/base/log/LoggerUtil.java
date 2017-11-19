package com.choodon.rpc.base.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {
    static {
        System.setProperty("DLog4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }

    public static void trace(String msg) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.trace(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg);
    }

    public static void debug(String msg) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.debug(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg);
    }

    public static void debug(String format, Object... argArray) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.debug(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + format, argArray);
    }

    public static void debug(String msg, Throwable t) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.debug(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg, t);
    }

    public static void info(String msg) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.info(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg);
    }

    public static void info(String format, Object... argArray) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.info(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + format, argArray);
    }

    public static void info(String msg, Throwable t) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.info(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg, t);
    }

    public static void warn(String msg) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.warn(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg);
    }

    public static void warn(String format, Object... argArray) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.warn(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + format, argArray);
    }

    public static void warn(String msg, Throwable t) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.warn(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg, t);
    }

    public static void error(String msg) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.error(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg);
    }

    public static void error(String format, Object... argArray) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.error(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + format, argArray);
    }

    public static void error(String msg, Throwable t) {
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
        Logger logger = LogManager.getLogger(stackTraceElement.getClass());
        logger.error(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(" +stackTraceElement.getFileName()+":"+ stackTraceElement.getLineNumber() + ")-- " + msg, t);
    }


}
