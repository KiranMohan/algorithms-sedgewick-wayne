package chapter2.section4;

import edu.princeton.cs.algs4.StdOut;
import util.ArrayGenerator;
import util.ArrayUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rene on 31/03/17.
 */
//Use the VM Option of -Xmx20g to create a 10^9 size array
//This implementation uses array sizes of 10^3, 10^5 and 10^6 for the experiments
public class Exercise42_PreorderHeaps {

    private enum HeapType {
        LEVEL_ORDER, PRE_ORDER;
    }

    private long numberOfCompares;
    private int preorderTraversalIndicesIndex;

    public static void main(String[] args) {
        int[] arraySizes = {1000, 100000, 1000000};

        Map<Integer, Comparable[]> allInputArrays = new HashMap<>();
        for(int i=0; i < 3; i++) {
            Comparable[] array = ArrayGenerator.generateDistinctValuesShuffledArray(arraySizes[i]);
            array[0] = null; //0 index is not used on heaps
            allInputArrays.put(i, array);
        }

        Exercise42_PreorderHeaps preorderHeaps = new Exercise42_PreorderHeaps();
        preorderHeaps.doExperiment(allInputArrays);
    }

    private void doExperiment(Map<Integer, Comparable[]> allInputArrays) {

        StdOut.printf("%13s %25s %20s\n", "Array Size | ","Number of Compares Level Order | ", "Number of Compares Pre Order");

        for(int i=0; i < 3; i++) {
            Comparable[] originalArray = allInputArrays.get(i);
            Comparable[] array = new Comparable[originalArray.length];
            System.arraycopy(originalArray, 0, array, 0, originalArray.length);

            numberOfCompares = 0;
            //Standard implementation - heap-ordered tree in level order
            heapSort(originalArray, null, HeapType.LEVEL_ORDER);
            long numberOfComparesStandardImpl = numberOfCompares;

            numberOfCompares = 0;
            preorderTraversalIndicesIndex = 0;
            //Using a heap-ordered tree in preorder
            int[] preorderTraversalIndices = new int[array.length - 1];
            generatePreorderTraversalIndices(preorderTraversalIndices, 1, array.length);
            heapSort(array, preorderTraversalIndices, HeapType.PRE_ORDER);

            printResults(originalArray.length, numberOfComparesStandardImpl, numberOfCompares);
        }
    }

    private void heapSort(Comparable[] array, int[] preorderTraversalIndices, HeapType heapType) {
        if(heapType == HeapType.LEVEL_ORDER) {
            constructHeapLevelOrder(array);
            sortdownLevelOrder(array);
        } else if(heapType == HeapType.PRE_ORDER) {
            constructHeapPreOrder(array, preorderTraversalIndices);
            sortdownPreorder(array, preorderTraversalIndices);
        }
    }

    //Standard implementation - heap-ordered tree in level order
    private void constructHeapLevelOrder(Comparable[] array) {
        for(int i = array.length / 2; i >= 1; i--) {
            sink(array, i, array.length - 1);
        }
    }

    //Heap-ordered tree in preorder
    private void constructHeapPreOrder(Comparable[] array, int[] preorderTraversalIndices) {
        for(int i = preorderTraversalIndices.length - 2; i >= 0; i--) {
            sinkHeapPreOrder(array, preorderTraversalIndices[i], i + 1, preorderTraversalIndices, preorderTraversalIndices.length - 1);
        }
    }

    private void sortdownLevelOrder(Comparable[] array) {
        int endIndex = array.length - 1;

        while (endIndex > 1) {
            ArrayUtil.exchange(array, 1, endIndex);
            endIndex--;
            sink(array, 1, endIndex);
        }
    }

    private void sortdownPreorder(Comparable[] array, int[] preorderTraversalIndices) {
        int endIndex = preorderTraversalIndices.length - 1;

        //If we use an in-place sort in this case, the array will end up pre-ordered instead of in-ordered
        Comparable[] orderedArray = new Comparable[array.length];
        int orderedArrayIndex = orderedArray.length - 1;

        while (endIndex >= 0) {
            ArrayUtil.exchange(array, 1, preorderTraversalIndices[endIndex]);
            orderedArray[orderedArrayIndex--] = array[preorderTraversalIndices[endIndex]];
            endIndex--;
            sinkHeapPreOrder(array, 1, 1, preorderTraversalIndices, endIndex);
        }

        //Array is now sorted in preorder, but we need an array sorted inorder
        System.arraycopy(orderedArray, 0, array, 0, orderedArray.length);
    }

    private void sink(Comparable[] array, int index, int endIndex) {
        while (index * 2 <= endIndex) {
            int biggestChildIndex = index * 2;

            if(index * 2 + 1 <= endIndex) {
                numberOfCompares++;
                if(ArrayUtil.more(array[index * 2 + 1], array[index * 2])) {
                    biggestChildIndex = index * 2 + 1;
                }
            }

            numberOfCompares++;
            if(ArrayUtil.less(array[index], array[biggestChildIndex])) {
                ArrayUtil.exchange(array, index, biggestChildIndex);
            } else {
                break;
            }

            index = biggestChildIndex;
        }
    }

    private void sinkHeapPreOrder(Comparable[] array, int currentIndex, int startTraversalIndex,
                                         int[] preorderTraversalIndices, int endIndex) {
        for(int i = startTraversalIndex; i <= endIndex; i++) {
            numberOfCompares++;
            if(ArrayUtil.less(array[currentIndex], array[preorderTraversalIndices[i]])) {
                ArrayUtil.exchange(array, currentIndex, preorderTraversalIndices[i]);
                currentIndex = preorderTraversalIndices[i];
            } else {
                return;
            }
        }
    }

    private void generatePreorderTraversalIndices(int[] preorderTraversalIndices, int currentIndex, int arrayLength) {
        if(currentIndex >= arrayLength) {
            return;
        }

        preorderTraversalIndices[preorderTraversalIndicesIndex++] = currentIndex;

        generatePreorderTraversalIndices(preorderTraversalIndices, currentIndex * 2, arrayLength);
        generatePreorderTraversalIndices(preorderTraversalIndices, currentIndex * 2 + 1, arrayLength);
    }

    private void printResults(int arraySize, long numberOfComparesLevelOrder, long numberOfComparesPreOrder) {
        StdOut.printf("%10d %33d %31d\n", arraySize, numberOfComparesLevelOrder, numberOfComparesPreOrder);
    }
}