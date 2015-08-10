package com.gzc.zgxq.game;

/**
 * ��¼�߷�����
 * ������ʱ���߷����ڸ��߷���Ŀ��λ�õ����Ӽ�¼����������ջ��
 * �ڻ���ʱȡ�������������߷����ɡ�
 * @author gzc
 */
public class StackPlayChess {

	/** �õ�Ŀ�ĸ��ӵ����� */
	private int pcCaptured;
	/** �����ߵ�һ���� */
	private int mvResult;
	
	public StackPlayChess(int mvResult,int pcCaptured){
		this.setPcCaptured(pcCaptured);
		this.setMvResult(mvResult);
	}

	public int getMvResult() {
		return mvResult;
	}

	public void setMvResult(int mvResult) {
		this.mvResult = mvResult;
	}

	public int getPcCaptured() {
		return pcCaptured;
	}

	public void setPcCaptured(int pcCaptured) {
		this.pcCaptured = pcCaptured;
	}

	@Override
	public String toString() {
		return "StackPlayChess [�õ�Ŀ�ĸ��ӵ�����pcCaptured=" + pcCaptured + ",  �����ߵ�һ����mvResult="
				+ mvResult + "]";
	}	

}
