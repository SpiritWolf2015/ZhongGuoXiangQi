package com.gzc.zgxq.game;

import java.util.Comparator;

/**
 * 在进行alpha-beta搜索过程时，对其局面下的所有走法进行排序，排序依据为历史表，历史表数组的下标代表走法，
 * 其值为在该步骤下的深度关值，所以，每次搜索时就可以先搜索最好的走法。减少对后面的搜索次数。
 */
public class MyComparator implements Comparator< Integer> {
	
	@Override
	public int compare(Integer arg0, Integer arg1) {
		Integer i1 = (Integer) arg0;
		Integer i2 = (Integer) arg1;
		
		return GameLogic.nHistoryTable[i2.intValue()] - GameLogic.nHistoryTable[i1.intValue()];
	}
}
