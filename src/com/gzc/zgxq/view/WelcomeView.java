/**
 * 
 */
package com.gzc.zgxq.view;



import com.gzc.testsurfaceview.R;
import com.gzc.zgxq.GameManagerActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;




/**
 * @author gzc
 * ��ӭ����View
 */
public class WelcomeView extends SurfaceView implements SurfaceHolder.Callback {

	
	/**
	 * ��activity������
	 */
	GameManagerActivity activity;
	/**
	 * ����
	 */
	Paint paint; 	
	/**
	 * ��ǰ�Ĳ�͸��ֵ
	 */
	int currentAlpha = 0;
	/**
	 * ������ʱ��ms
	 */
	byte sleepSpan = 50; 
	/**
	 *  logoͼƬ����
	 */
	Bitmap[] logos = new Bitmap[2];
	/**
	 * ��ǰlogoͼƬ����
	 */
	Bitmap currentLogo; 
	/**
	 * ͼƬλ��
	 */
	int currentX,currentY; 
	
	
	
	
	
	/**
	 * ���캯��
	 * @context ��Activity
	 */	
	public WelcomeView(Context context) {
		super(context);
		
		this.activity = (GameManagerActivity) context;
		this.getHolder().addCallback(this); // �����������ڻص��ӿڵ�ʵ����
		paint = new Paint(); // ��������
		paint.setAntiAlias(true); // �򿪿����
		// ����ͼƬ
		float xZoom = ViewConstant.xZoom;
		if (xZoom < 1) {
			xZoom *= 1.5f;
		}
		logos[0] = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.baina), xZoom);
		logos[1] = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.bnkjs), xZoom);
	}
	
	// ����ʱ�ص�
	@Override
	public void draw(Canvas canvas) {
		// ���ƺ��������屳��
		paint.setColor(Color.BLUE);// ���û�����ɫ
		paint.setAlpha(255);// ���ò�͸����Ϊ255
		canvas.drawRect(0, 0, ViewConstant.width, ViewConstant.height, paint);
		// ����ƽ����ͼ
		if (currentLogo == null)
			return;
		paint.setAlpha(currentAlpha);
		canvas.drawBitmap(currentLogo, currentX, currentY, paint);
	}

	// ����ʱ�ص�	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread() {
			
			@Override
			public void run() {
				for (Bitmap bm : logos) {
					currentLogo = bm;// ��ǰͼƬ������
					currentX = (int) (ViewConstant.width / 2 - bm.getWidth() / 2);// ͼƬλ��
					currentY = (int) (ViewConstant.height / 2 - bm.getHeight() / 2);
					for (int i = 255; i > -10; i -= 10) {// ��̬����ͼƬ��͸����ֵ�������ػ�
						currentAlpha = i;
						if (currentAlpha < 0)// �����ǰ��͸����С����
						{
							currentAlpha = 0;// ����͸������Ϊ��
						}
						SurfaceHolder myholder = WelcomeView.this.getHolder();// ��ȡ�ص��ӿ�
						Canvas canvas = WelcomeView.this.getHolder().lockCanvas();// ��ȡ����
						try {
							synchronized (myholder)// ͬ��
							{
								WelcomeView.this.draw(canvas);// ���л��ƻ���
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (canvas != null)// �����ǰ������Ϊ��
							{
								myholder.unlockCanvasAndPost(canvas);// ��������
							}
						}
						try {
							if (i == 255)// ������ͼƬ����ȴ�һ��
							{
								Thread.sleep(1000);
							}
							Thread.sleep(sleepSpan);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				activity.hd.sendEmptyMessage(0);// ������Ϣ��������ϷView����ʼ��������ģ��
				System.out.println("GZC->sendEmptyMessage�߳�id��"+Thread.currentThread().getId());
			}
		}.start();
	}
	// �ı�ʱ�ص�	
	@Override public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}
	// ����ʱ�ص�	
	@Override public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

	
	
}
