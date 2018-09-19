package com.dtstack.util;

import java.util.Random;
import java.util.List;

public class ListUtil {
    /**
     * 在list中随机取一个数字
     *
     * @param list
     * @return
     */
    public static int getRandomIndexFromList(@SuppressWarnings("rawtypes") List list) {

        Random random = new Random();
        int index = random.nextInt(list.size()) % (list.size() + 1);
        return index;
    }

}
