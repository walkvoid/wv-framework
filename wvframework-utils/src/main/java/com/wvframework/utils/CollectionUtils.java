package com.wvframework.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jiangjunqing
 * @date 2023/11/16
 * @desc
 */
public class CollectionUtils {


    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();

    }

    public static boolean isNotEmpty(Collection<?> collection){
        return !isEmpty(collection);
    }

    public static int size(Collection<?> collection){
        return collection == null ? 0 : collection.size();
    }

    public static <E> ArrayList<E> newArrayList(){
        return new ArrayList<E>();
    }

    public static  <E> ArrayList<E> newArrayList(E... elements) {
        if (elements == null || elements.length ==0 ) {
            throw new IllegalArgumentException("elements is null.");
        }
        ArrayList<E> arrayList = new ArrayList<E>(computeArrayListCapacity(elements.length));
        Collections.addAll(arrayList, elements);
        return arrayList;
    }

    public static <E> ArrayList<E> newArrayList(ArrayList<E>... arrayLists) {
        if (arrayLists == null || arrayLists.length == 0) {
            throw new IllegalArgumentException("arrayLists is null.");
        }
        int count = 0;
        for (ArrayList<E> arrayList : arrayLists) {
            if (arrayList == null || arrayList.size() == 0) {
                continue;
            }
            count = count + arrayList.size();
        }
        ArrayList<E> result = new ArrayList<E>(computeArrayListCapacity(count));
        for (ArrayList<E> arrayList : arrayLists) {
            if (arrayList == null || arrayList.size() == 0) {
                continue;
            }
            result.addAll(arrayList);
        }
        return result;
    }

    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<E>();
    }

    public static <E> LinkedList<E> newLinkedList(E... elements) {
        if (elements == null || elements.length ==0 ) {
            throw new IllegalArgumentException("elements is null.");
        }
        LinkedList<E> list = new LinkedList<>();
        Collections.addAll(list, elements);
        return list;
    }

    public static  <E> LinkedList<E> newLinkedList(LinkedList<E>... arrayLists) {
        if (arrayLists == null || arrayLists.length == 0 ) {
            throw new IllegalArgumentException("arrayLists is null.");
        }
        LinkedList<E> list = new LinkedList<>();
        for (LinkedList<E> linkedList : arrayLists) {
            list.addAll(linkedList);
        }
        return list;
    }

    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();

    }

    public static  <E> HashSet<E> newHashSet(E... elements) {
        if (elements == null || elements.length == 0 ) {
            throw new IllegalArgumentException("elements is null.");
        }
        HashSet<E> hashSet = new HashSet<>();
        Collections.addAll(hashSet, elements);
        return hashSet;
    }

    public static  <E> HashSet<E> newHashSet(HashSet<E>... hashSets) {
        if (hashSets == null || hashSets.length == 0 ) {
            throw new IllegalArgumentException("hashSets is null.");
        }
        HashSet<E> hashSet = new HashSet<>();
        for (HashSet<E> set : hashSets) {
            hashSet.addAll(set);
        }
        return hashSet;
    }


    /**
     * compute arrayList capacity
     * @param size
     * @return
     */
    private static int computeArrayListCapacity(int size) {
        if (size <= 0) {
           throw new IllegalArgumentException("size must be positive");
        } else if (size < 10) {
            return 10;
        } else {
            int newCapacity = 5 + size + size / 10;
            //overflow
            return newCapacity < 0 ? Integer.MAX_VALUE : newCapacity;
        }
    }






}
