package com.choodon.rpc.base.service;

public class CompilerManager {
    private static JdkCompiler compiler = new JdkCompiler();

    public static Class<?> compiler(String code) {
        return compiler.compile(code, CompilerManager.class.getClassLoader());
    }

}
