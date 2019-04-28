package com.example.johnanna.study;

import org.junit.Test;

import java.util.Date;

/**
 * Created by li.zhipeng on 2019/4/22.
 */
public class AlgorithmTest {

    /**
     * 二分法
     */
    @Test
    public void twoSlideTest() {
        int[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int result : data) {
            twoSlide(result);
        }
    }

    private void twoSlide(int result) {
        int[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        // 已查询的最大索引
        int highIndex = data.length - 1;
        int lowIndex = 0;
        int index;

        int resultIndex = -1;
        while (lowIndex <= highIndex) {
            index = lowIndex + (highIndex - lowIndex) / 2;
//            System.out.println("这一轮index：" + index);
            int value = data[index];
            if (value == result) {
                resultIndex = index;
                System.out.println(index);
                break;
            }
            // 向后查找
            else if (value < result) {
                // 保存最小坐标
                lowIndex = index + 1;
            }
            // 向前查找
            else {
                highIndex = index - 1;
            }
        }
        if (resultIndex == -1){
            System.out.println("未找到：" + result);
        }

    }

    @Test
    public void maopaoTest(){
        int[] data = {1, 30, 9, 8, 11, 15, 21, 6, 3, 31};
        maopao(data);
        for(int item : data){
            System.out.println(item);
        }
    }

    private void maopao(int[] data){
        for (int i = 0 ; i < data.length; i ++){

            for (int j = i + 1; j < data.length; j ++){

                int itemI = data[i];
                int itemJ = data[j];
                // 如果i比j要大，调换数据的位置
                if (itemI > itemJ){
                    int temp = itemI;
                    data[i] = itemJ;
                    data[j] = temp;
                }

            }

        }
    }

    @Test
    public void quickSortTest(){
        int[] data = {1, 30, 9, 8, 11, 15, 21, 6, 3, 31};
        quickSort(data, 0 , data.length - 1);
        for(int item : data){
            System.out.println(item);
        }
    }

    private void quickSort(int[] data, int start, int end){
        if (start >= end){
            return;
        }

        int i = start;
        int j = end;
        // 保存基准值
        int key = data[i];
        // 开始循环
        while (i < j){
            // 从后开始找比基准值小的位置
            while (i < j && data[j] >= key){
                j--;
            }
            if (i < j){
                data[i++] = data[j];
            }

            // 从前开始找比基准值大的位置
            while (i < j && data[i] <= key){
                i ++;
            }
            if (i < j){
                data[j--] = data[i];
            }
        }
        data[i] = key;
        quickSort(data, start, i - 1);
        quickSort(data, j + 1, end);

    }

}
