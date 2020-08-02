package dataStructure.sparseArray;

/**
 * @ClassName sparseArray
 * @Description TODO 稀疏数组，当数组中有一堆重复数据，不同数据很少时使用可以减少数据存储量
 * 二维数组
 * 0 0 1
 * 0 2 0
 * 3 0 0
 * 0 4 6
 * 转换成稀疏数组后
 * 4 3 5 //稀疏数组第一行数据，4行， 3列，总共4个数据
 * 0 2 1 //第二行及以后代表 第几行，第几列，数据值
 * 1 1 2
 * 2 0 3
 * 3 1 4
 * 3 2 6
 * @Author zhangyp
 * @Date 2020/5/17 22:33
 * @Version 1.0
 */
public class sparseArray {
    public static void main(String[] args) {
        int count = 0;
        System.out.println("========原始数组========");
        int[][] arr = new int[11][11];
        arr[2][5] = 1;
        arr[6][2] = 3;
        arr[9][8] = 5;
        for (int[] a : arr) {
            for (int b : a) {
                System.out.print(b + "\t");
                if (b != 0) {
                    count++;
                }
            }
            System.out.println();
        }
        System.out.println("========稀疏数组========");
        int[][] sparseArr = new int[count + 1][3];
        sparseArr[0][0] = 11;
        sparseArr[0][1] = 11;
        sparseArr[0][2] = count;
        count = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (arr[i][j] != 0) {
                    count++;
                    sparseArr[count][0] = i;
                    sparseArr[count][1] = j;
                    sparseArr[count][2] = arr[i][j];
                }
            }
        }
        for (int[] a : sparseArr) {
            for (int b : a) {
                System.out.print(b + "\t");
            }
            System.out.println();
        }
        System.out.println("========稀疏数组恢复原始数组========");
        int[][] recoverArr = new int[sparseArr[0][0]][sparseArr[0][1]];
        for (int i = 1; i < sparseArr.length; i++) {
            recoverArr[sparseArr[i][0]][sparseArr[i][1]] = sparseArr[i][2];
        }
        for (int[] a : recoverArr) {
            for (int b : a) {
                System.out.print(b + "\t");
                if (b != 0) {
                    count++;
                }
            }
            System.out.println();
        }
    }
}
