package com.lzp.producerandconsumer;

import org.junit.Test;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by li.zhipeng on 2019-05-21.
 */
public class ArrayTest {

    class MyArray<T> {

        T[] array;

        MyArray(Class<T> clazz, int length) {
            System.out.println(array.getClass().getComponentType());//结
//            System.out.println(getClass().getTypeParameters()[0].getName());
//            this.array = new T[length];
            this.array = (T[]) Array.newInstance(clazz, length);
//            try {
//                this.array = (T[]) Array.newInstance(Class.forName(getClass().getTypeParameters()[0].getName()), length);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
        }

    }

    @Test
    public void ArrayFX() {
        MyArray<String> array = new MyArray<>(String.class, 5);
        Type type = array.array.getClass().getComponentType();
        //将type强转成Parameterized
//        GenericArrayType pt = (GenericArrayType) type;
/*得到父类(参数化类型)中的泛型(实际类型参数)的实际类型。
getActualTypeArguments()返回一个Type数组，之所以返回Type数组,是因为一个类上有可能出现多个泛型，比如:Map<Integer,String>
*/
//        System.out.println(array.array.getClass().getComponentType());//结果:C

    }


    @Test
    public void testIntArray() {
        int[] data = {3, 5};
        int count = 0;
        for (int datum : data) {
            String str = binaryToDecimal(datum);
            System.out.println(str);
            for (int j = 0; j < str.length(); j++) {
                if (str.charAt(j) == '1') {
                    count++;
                }
            }
        }
        System.out.println(count);


    }

    private String binaryToDecimal(int n) {
        String str = "";
        while (n != 0) {
            str = n % 2 + str;
            System.out.println(str);
            n = n / 2;
        }
        return str;
    }

    @Test
    public void testArray() {
        int[] array2 = {1, 5, 9};
        int[] array1 = {2, 3, 10};

        int[] result = new int[6];

        int index1 = 0;
        int index2 = 0;

        int count = 0;

        while (index1 < array1.length) {

            int item1 = array1[index1];

            int item2 = array2[index2];

            if (item1 < item2) {
                result[index1 + index2] = item1;
                index1++;
            } else {
                result[index1 + index2] = item2;
                index2++;
            }

            if (index2 >= array2.length) {
                break;
            }

            count++;
        }

        while (index1 < array1.length) {
            result[index1 + index2] = array1[index1];
            index1++;
            count++;
        }

        while (index2 < array2.length) {
            result[index1 + index2] = array2[index2];
            index2++;
            count++;
        }

        System.out.println("计算的次数为：" + count);

        for (int i1 : result) {
            System.out.println(i1);
        }

    }
}
