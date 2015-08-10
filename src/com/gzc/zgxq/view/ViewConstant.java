package com.gzc.zgxq.view;

import android.graphics.Bitmap;
import android.graphics.Matrix;


/**
 * View���������߷���
 * @author gzc
 */
public class ViewConstant {

	public static int thinkDeeplyTime = 1;
	
	
	/**
	 * �Ƿ�Ϊ�������Ѷ�ѡ��
	 */
	public static boolean isnoTCNDxuanz;
	/**
	 * ���岽��
	 */
	public static int huiqiBS = 2;
	/**
	 * �Ƿ񲥷�����
	 */
	public static boolean isnoPlaySound = true; 
	/**
	 * Ӯ�����־
	 */
	public static boolean yingJMflag;
	/**
	 * ������־
	 */
	public static boolean shuJMflag; 
	/**
	 * ���ű���
	 */
	public static float xZoom = 1F, yZoom = 1F;	
	/**
	 * ��Ļ����
	 */
	public static float width, height;
	/**
	 * ���̵���ʼ����
	 */
	public static float sXtart = 0,sYtart = 0;
	/**
	 * �Ƿ�Ϊ��ʼ������ͣ
	 */
	public static boolean isnoStart = false;
	/**
	 * �Ѷ�ϵ��
	 */
	public static int nanduXS = 1;
	
	public static int zTime = 900000;
	/**
	 * ��ʱ��
	 */
	public static int endTime = zTime;
	
	public static float xSpan = 48.0f * xZoom;
	public static float ySpan = 48.0f * yZoom;
	
	public static float scoreWidth = 7 * xZoom * 4f;// ʱ�����ּ��
	public static float sXtartCk;
	public static float sYtartCk;// �ڶ����ڵ���ʼ����

	public static float windowWidth = 200 * xZoom;// ���ڵĴ�С
	public static float windowHeight = 400 * xZoom;

	public static float windowXstartLeft = sXtart + (5 * xSpan - windowWidth)
			/ 2 * xZoom;// С���ڵ���ʼ����
	public static float windowXstartRight = sYtart + 5 * xSpan
			+ (5 * xSpan - windowWidth) / 2 * xZoom;
	public static float windowYstart = sYtart + ySpan * 4 * xZoom;

	/**
	 * ���Ӱ뾶
	 */
	public static float chessR = 30 * xZoom;
	/**
	 * �������ű���
	 */
	public static float fblRatio = 0.6f * xZoom;
	
	
	/**
	 * ����ͼƬ�ķ���
	 * 
	 * @param bm
	 *            Ҫ���ŵ�ͼƬ
	 * @param fblRatio
	 *            ͼƬ�ȱ�����СΪԭ����fblRatio��
	 * @return �������ź��ͼƬ
	 */
	public static Bitmap scaleToFit(Bitmap bm, float fblRatio) {
		int width = bm.getWidth(); // ͼƬ���
		int height = bm.getHeight();// ͼƬ�߶�
		Matrix matrix = new Matrix();
		matrix.postScale(fblRatio, fblRatio);// ͼƬ�ȱ�����СΪԭ����fblRatio��
		Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);// ����λͼ
		return bmResult;
	}
	
	public static void initChessViewFinal() {
		xSpan = 48.0f * xZoom;
		ySpan = 48.0f * yZoom;

		scoreWidth = 7 * xZoom * 4f;// ʱ�����ּ��

		windowWidth = 200 * xZoom;// ���ڵĴ�С
		windowHeight = 250 * xZoom;

		windowXstartLeft = sXtart + (5 * xSpan - windowWidth) / 2 * xZoom;// С���ڵ���ʼ����
		windowXstartRight = sYtart + 5 * xSpan + (5 * xSpan - windowWidth) / 2
				* xZoom;
		windowYstart = sYtart + ySpan * 1 * xZoom;

		chessR = 30 * xZoom;// ���Ӱ뾶
		fblRatio = 0.6f * xZoom;// �������ű���
	}
	
	
}
