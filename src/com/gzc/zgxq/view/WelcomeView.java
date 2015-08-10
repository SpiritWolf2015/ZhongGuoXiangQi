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
 * 欢迎界面View
 */
public class WelcomeView extends SurfaceView implements SurfaceHolder.Callback {

	
	/**
	 * 主activity的引用
	 */
	GameManagerActivity activity;
	/**
	 * 画笔
	 */
	Paint paint; 	
	/**
	 * 当前的不透明值
	 */
	int currentAlpha = 0;
	/**
	 * 动画的时延ms
	 */
	byte sleepSpan = 50; 
	/**
	 *  logo图片数组
	 */
	Bitmap[] logos = new Bitmap[2];
	/**
	 * 当前logo图片引用
	 */
	Bitmap currentLogo; 
	/**
	 * 图片位置
	 */
	int currentX,currentY; 
	
	
	
	
	
	/**
	 * 构造函数
	 * @context 主Activity
	 */	
	public WelcomeView(Context context) {
		super(context);
		
		this.activity = (GameManagerActivity) context;
		this.getHolder().addCallback(this); // 设置生命周期回调接口的实现者
		paint = new Paint(); // 创建画笔
		paint.setAntiAlias(true); // 打开抗锯齿
		// 加载图片
		float xZoom = ViewConstant.xZoom;
		if (xZoom < 1) {
			xZoom *= 1.5f;
		}
		logos[0] = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.baina), xZoom);
		logos[1] = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.bnkjs), xZoom);
	}
	
	// 绘制时回调
	@Override
	public void draw(Canvas canvas) {
		// 绘制黑填充矩形清背景
		paint.setColor(Color.BLUE);// 设置画笔颜色
		paint.setAlpha(255);// 设置不透明度为255
		canvas.drawRect(0, 0, ViewConstant.width, ViewConstant.height, paint);
		// 进行平面贴图
		if (currentLogo == null)
			return;
		paint.setAlpha(currentAlpha);
		canvas.drawBitmap(currentLogo, currentX, currentY, paint);
	}

	// 创建时回调	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread() {
			
			@Override
			public void run() {
				for (Bitmap bm : logos) {
					currentLogo = bm;// 当前图片的引用
					currentX = (int) (ViewConstant.width / 2 - bm.getWidth() / 2);// 图片位置
					currentY = (int) (ViewConstant.height / 2 - bm.getHeight() / 2);
					for (int i = 255; i > -10; i -= 10) {// 动态更改图片的透明度值并不断重绘
						currentAlpha = i;
						if (currentAlpha < 0)// 如果当前不透明度小于零
						{
							currentAlpha = 0;// 将不透明度置为零
						}
						SurfaceHolder myholder = WelcomeView.this.getHolder();// 获取回调接口
						Canvas canvas = WelcomeView.this.getHolder().lockCanvas();// 获取画布
						try {
							synchronized (myholder)// 同步
							{
								WelcomeView.this.draw(canvas);// 进行绘制绘制
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (canvas != null)// 如果当前画布不为空
							{
								myholder.unlockCanvasAndPost(canvas);// 解锁画布
							}
						}
						try {
							if (i == 255)// 若是新图片，多等待一会
							{
								Thread.sleep(1000);
							}
							Thread.sleep(sleepSpan);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				activity.hd.sendEmptyMessage(0);// 发送消息，进入游戏View，开始加载棋子模型
				System.out.println("GZC->sendEmptyMessage线程id："+Thread.currentThread().getId());
			}
		}.start();
	}
	// 改变时回调	
	@Override public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}
	// 销毁时回调	
	@Override public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

	
	
}
