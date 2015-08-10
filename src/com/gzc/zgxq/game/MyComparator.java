package com.gzc.zgxq.game;

import java.util.Comparator;

/**
 * �ڽ���alpha-beta��������ʱ����������µ������߷�����������������Ϊ��ʷ����ʷ��������±�����߷���
 * ��ֵΪ�ڸò����µ���ȹ�ֵ�����ԣ�ÿ������ʱ�Ϳ�����������õ��߷������ٶԺ��������������
 */
public class MyComparator implements Comparator< Integer> {
	
	@Override
	public int compare(Integer arg0, Integer arg1) {
		Integer i1 = (Integer) arg0;
		Integer i2 = (Integer) arg1;
		
		return GameLogic.nHistoryTable[i2.intValue()] - GameLogic.nHistoryTable[i1.intValue()];
	}
}
