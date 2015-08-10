package com.gzc.zgxq.game;

/**
 * 记录走法的类
 * 将对弈时，走法和在该走法下目标位置的棋子记录起来，放入栈，
 * 在悔棋时取出来，撤销该走法即可。
 * @author gzc
 */
public class StackPlayChess {

	/** 得到目的格子的棋子 */
	private int pcCaptured;
	/** 电脑走的一步棋 */
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
		return "StackPlayChess [得到目的格子的棋子pcCaptured=" + pcCaptured + ",  电脑走的一步棋mvResult="
				+ mvResult + "]";
	}	

}
