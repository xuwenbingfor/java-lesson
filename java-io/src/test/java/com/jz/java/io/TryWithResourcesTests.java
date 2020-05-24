package com.jz.java.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author xuwenbingfor
 * @version 2020/5/24 21:25
 * @description
 */
@Slf4j
public class TryWithResourcesTests {
    /* 输出为：
	MyAutoCloaseA: test()
	MyAutoCloseB: on close
	MyAutoCloseA: on close()
	Main: exception
	MyAutoCloaseA: test() IOException
	MyAutoCloaseB: close() ClassNotFoundException
	MyAutoCloaseA: close() ClassNotFoundException
    */
    static class MyAutoCloseA implements AutoCloseable {

        public void test() throws IOException {
            System.out.println("MyAutoCloaseA: test()");
            throw new IOException("MyAutoCloaseA: test() IOException");
        }

        @Override
        public void close() throws Exception {
            System.out.println("MyAutoCloseA: on close()");
            throw new ClassNotFoundException("MyAutoCloaseA: close() ClassNotFoundException");
        }
    }

    static class MyAutoCloseB implements AutoCloseable {

        public void test() throws IOException {
            System.out.println("MyAutoCloaseB: test()");
            throw new IOException("MyAutoCloaseB: test() IOException");
        }

        @Override
        public void close() throws Exception {
            System.out.println("MyAutoCloseB: on close");
            throw new ClassNotFoundException("MyAutoCloaseB: close() ClassNotFoundException");
        }
    }

    @Test
    public void test() {
        this.startTest();
    }

    public void startTest() {
        try (MyAutoCloseA a = new MyAutoCloseA();
             MyAutoCloseB b = new MyAutoCloseB()) {
            a.test();
            b.test();
        } catch (Exception e) {
            System.out.println("Main: exception");
            System.out.println(e.getMessage());
            Throwable[] suppressed = e.getSuppressed();
            for (int i = 0; i < suppressed.length; i++)
                System.out.println(suppressed[i].getMessage());
        }
    }

    /**
     * startTest()编译后的代码
     */
    public void startTestAfterCompile() {
        try {
            MyAutoCloseA a = new MyAutoCloseA();
            Throwable var33 = null;

            try {
                MyAutoCloseB b = new MyAutoCloseB();
                Throwable var3 = null;

                try { // 我们定义的 try 块
                    a.test();
                    b.test();
                } catch (Throwable var28) { // try 块中抛出的异常
                    var3 = var28;
                    throw var28;
                } finally {
                    if (b != null) {
                        // 如果 try 块中抛出异常，就将 close 中的异常（如果有）附加为压制异常
                        if (var3 != null) {
                            try {
                                b.close();
                            } catch (Throwable var27) {
                                var3.addSuppressed(var27);
                            }
                        } else { // 如果 try 块没有抛出异常，就直接关闭，可能会抛出关闭异常
                            b.close();
                        }
                    }

                }
            } catch (Throwable var30) {
                var33 = var30;
                throw var30;
            } finally {
                if (a != null) {
                    if (var33 != null) {
                        try {
                            a.close();
                        } catch (Throwable var26) {
                            var33.addSuppressed(var26);
                        }
                    } else {
                        a.close();
                    }
                }

            }
            // 所有的异常在这里交给 catch 块处理
        } catch (Exception var32) { // 我们定义的 catch 块
            System.out.println("Main: exception");
            System.out.println(var32.getMessage());
            Throwable[] suppressed = var32.getSuppressed();

            for (int i = 0; i < suppressed.length; ++i) {
                System.out.println(suppressed[i].getMessage());
            }
        }

    }
}
