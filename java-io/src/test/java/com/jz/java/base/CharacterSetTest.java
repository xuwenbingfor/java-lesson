package com.jz.java.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author xuwenbingfor
 * @version 2020/11/14 22:11
 * @description
 */
@Slf4j
public class CharacterSetTest {

    @Test
    public void test1() {
        //4E00-9FA5是基本汉字,只占一个字符，也就是一个char，也就是2字节，也就是16位
        char c1 = '一';
        char c2 = '龥';
        // 𠀀即\uD840\uDC00。由于char只能存储两个字节。𠀀 Unicode下超过两个字节，所以编译不通过
//        char c3='\uD840\uDC00';
    }

}
