package com.chen.algorithm.sort;

/**
 * Created by admin on 2018/3/14.
 */

public class BubbleSort {

    public static void bubbleSort() {
        int[] shuzu = {23,34,89,32,45,67,54};
        for (int i = 0; i < shuzu.length; i++) {
            for (int j = 0; j < shuzu.length - 1; j++) {
                if (shuzu[j] > shuzu[j + 1]) {
                    int temp = shuzu[j];
                    shuzu[j] = shuzu[j + 1];
                    shuzu[j + 1] = temp;
                }
            }
            for (int m = 0; m < shuzu.length; m++) {
                System.out.print(shuzu[m] + ",");
            }
            System.out.println();
        }
    }

    public static void insertSort() {
        int[] shuzu = {23,34,89,32,45,67,54};
        for (int i = 1; i < shuzu.length; i++){
            int j = i - 1;
            int temp = shuzu[i];
            while (j >= 0 && shuzu[j] > temp) {
                shuzu[j + 1] = shuzu[j];
                j--;
            }
            shuzu[j + 1] = temp;
            for (int m = 0; m < shuzu.length; m++) {
                System.out.print(shuzu[m] + ",");
            }
            System.out.println();
        }
    }
}
