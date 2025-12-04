package com.coolcollege.intelligent.common.util;

import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/09
 */
public class CoolListUtils {

    /**
     * newList 比oldList多了那些
     * @param oldList
     * @param newList
     * @param <E>
     * @return
     */
    public static <E> List<E> getAddaListThanbList(List<E> oldList, List<E> newList){
        List<E> addList = new ArrayList<E>();
        for (int i = 0; i < newList.size(); i++){
            if(!myListContains(oldList, newList.get(i))){
                addList.add(newList.get(i));
            }
        }
        return addList;
    }

    /**
     * newList比oldList少了哪些
     * @param oldList
     * @param newList
     * @param <E>
     * @return
     */
    public static <E> List<E> getReduceaListThanbList(List<E> oldList, List<E> newList){
        List<E> reduceaList = new ArrayList<E>();
        for (int i = 0; i < oldList.size(); i++){
            if(!myListContains(newList, oldList.get(i))){
                reduceaList.add(oldList.get(i));
            }
        }
        return reduceaList;
    }

    private static <E> boolean myListContains(List<E> sourceList, E element) {

        if (sourceList == null || element == null){
            return false;
        }
        if (sourceList.isEmpty()){
            return false;
        }
        for (E tip : sourceList){
            if(element.equals(tip)){
                return true;
            }
        }
        return false;
    }
    /**
     * 两个集合是否完全相同  oldList  newList 只要有一个为null则返回false
     * @param oldList
     * @param newList
     * @return
     */
    public static <E> boolean checkDiffrent(List<E> oldList, List<E> newList) {
        if (oldList == null || newList == null){
            return false;
        }
        if(oldList.size() != newList.size()) {
            return false;
        }
        for(E str : oldList) {
            if(!newList.contains(str)) {
                return false;
            }
        }
        return true;
    }
}
