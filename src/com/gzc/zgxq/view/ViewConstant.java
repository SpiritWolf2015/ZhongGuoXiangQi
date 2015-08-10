package com.gzc.zgxq.view;

import android.graphics.Bitmap;
import android.graphics.Matrix;


/**
 * View常量，工具方法
 * @author gzc
 */
public class ViewConstant {

	public static int thinkDeeplyTime = 1;
	
	
	/**
	 * 是否为弹出了难度选择
	 */
	public static boolean isnoTCNDxuanz;
	/**
	 * 悔棋步数
	 */
	public static int huiqiBS = 2;
	/**
	 * 是否播放声音
	 */
	public static boolean isnoPlaySound = true; 
	/**
	 * 赢界面标志
	 */
	public static boolean yingJMflag;
	/**
	 * 输界面标志
	 */
	public static boolean shuJMflag; 
	/**
	 * 缩放比例
	 */
	public static float xZoom = 1F, yZoom = 1F;	
	/**
	 * 屏幕宽，高
	 */
	public static float width, height;
	/**
	 * 棋盘的起始坐标
	 */
	public static float sXtart = 0,sYtart = 0;
	/**
	 * 是否为开始或者暂停
	 */
	public static boolean isnoStart = false;
	/**
	 * 难度系数
	 */
	public static int nanduXS = 1;
	
	public static int zTime = 900000;
	/**
	 * 总时间
	 */
	public static int endTime = zTime;
	
	public static float xSpan = 48.0f * xZoom;
	public static float ySpan = 48.0f * yZoom;
	
	public static float scoreWidth = 7 * xZoom * 4f;// 时间数字间隔
	public static float sXtartCk;
	public static float sYtartCk;// 第二窗口的起始坐标

	public static float windowWidth = 200 * xZoom;// 窗口的大小
	public static float windowHeight = 400 * xZoom;

	public static float windowXstartLeft = sXtart + (5 * xSpan - windowWidth)
			/ 2 * xZoom;// 小窗口的起始坐标
	public static float windowXstartRight = sYtart + 5 * xSpan
			+ (5 * xSpan - windowWidth) / 2 * xZoom;
	public static float windowYstart = sYtart + ySpan * 4 * xZoom;

	/**
	 * 棋子半径
	 */
	public static float chessR = 30 * xZoom;
	/**
	 * 棋子缩放比例
	 */
	public static float fblRatio = 0.6f * xZoom;
	
	
	/**
	 * 缩放图片的方法
	 * 
	 * @param bm
	 *            要缩放的图片
	 * @param fblRatio
	 *            图片等比例缩小为原来的fblRatio倍
	 * @return 适配缩放后的图片
	 */
	public static Bitmap scaleToFit(Bitmap bm, float fblRatio) {
		int width = bm.getWidth(); // 图片宽度
		int height = bm.getHeight();// 图片高度
		Matrix matrix = new Matrix();
		matrix.postScale(fblRatio, fblRatio);// 图片等比例缩小为原来的fblRatio倍
		Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);// 声明位图
		return bmResult;
	}
	
	public static void initChessViewFinal() {
		xSpan = 48.0f * xZoom;
		ySpan = 48.0f * yZoom;

		scoreWidth = 7 * xZoom * 4f;// 时间数字间隔

		windowWidth = 200 * xZoom;// 窗口的大小
		windowHeight = 250 * xZoom;

		windowXstartLeft = sXtart + (5 * xSpan - windowWidth) / 2 * xZoom;// 小窗口的起始坐标
		windowXstartRight = sYtart + 5 * xSpan + (5 * xSpan - windowWidth) / 2
				* xZoom;
		windowYstart = sYtart + ySpan * 1 * xZoom;

		chessR = 30 * xZoom;// 棋子半径
		fblRatio = 0.6f * xZoom;// 棋子缩放比例
	}
	
	
}
