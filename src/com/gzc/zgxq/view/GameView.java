package com.gzc.zgxq.view;

import static com.gzc.zgxq.view.ViewConstant.*;

import java.util.Stack;

import com.gzc.testsurfaceview.R;
import com.gzc.zgxq.GameManagerActivity;
import com.gzc.zgxq.game.Chess_LoadUtil;
import com.gzc.zgxq.game.Constant;
import com.gzc.zgxq.game.GameLogic;
import com.gzc.zgxq.game.StackPlayChess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * 游戏界面View
 * 
 * @author gzc
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	/**
	 * 主Activity
	 */
	GameManagerActivity father;
	/**
	 * 画笔
	 */
	Paint paint;
	/**
	 * 难度数
	 */
	int length;
	/**
	 * 难度滚动条颜色数组
	 */
	int[][] color = new int[20][3];
	/**
	 * 悔棋步数
	 */
	int huiqibushu = 0;
	/**
	 * 画布
	 */
	Canvas canvas;
	/**
	 * 移动坐标
	 */
	float xMove, yMove;
	/**
	 * 触摸是否有效
	 */
	boolean cMfleg = true;
	/**
	 * 是否为第一次下棋
	 */
	boolean isFlage;
	/**
	 * 是否为选中，选中中需要移动的话就绘制移动
	 */
	boolean xzflag = false;
	/**
	 * 线程是否运行
	 */
	boolean threadFlag = true;
	/**
	 * 选中的初始格子
	 */
	int xzgz = 0;
	/**
	 * 是否为移动标志
	 */
	boolean flag;
	/**
	 * 点击处为悔棋标志
	 */
	boolean huiqiFlag = false;
	/**
	 * 点击处为重玩标志
	 */
	boolean chonwanFlag = false;
	/**
	 * 难度是否可选
	 */
	boolean isnoNanDu = true;
	/**
	 * 点击处为难度
	 */
	boolean dianjiNanDu;
	/**
	 * 点击处为新局
	 */
	boolean dianjiXinJu;
	/**
	 * 点击处为开始
	 */
	boolean dianjiKaiShi;
	/**
	 * 点击为声音区域
	 */
	boolean dianjishengyin;
	/**
	 * 点击处为拖动
	 */
	boolean dianjiJDT;
	/**
	 * 点击处为确定按钮
	 */
	boolean dianjiQueDing;
	/**
	 * 下棋方标志位，false为黑方下棋
	 */
	boolean isRedPlayChess;

	boolean nanduBXZ;
	int bzcol;
	int bzrow;

	Stack<StackPlayChess> stack = new Stack<StackPlayChess>();
	SurfaceHolder holder;
	int ucpcSquares[] = new int[256];

	float guanggao2X = 0;

	// --------------------------------图片变量--------------------------------
	Bitmap[][] chessBitmap;// 象棋棋子图片
	Bitmap chessZouQiflag;// 标志走棋
	Bitmap paotai;// 炮台
	Bitmap paotai2;// 炮台边沿的时候
	Bitmap paotai1;
	Bitmap chuhe;// 楚河图片

	Bitmap chessQipan2;// 棋盘边框
	Bitmap huiqi;// 悔棋图片
	Bitmap chonWan;// 重玩文字
	Bitmap[] iscore = new Bitmap[10];// 数字图
	Bitmap dunhao;// 顿号
	Bitmap beijint;// 背景图
	Bitmap minueBeijint;// 菜单背景图
	Bitmap ifPlayChess;// 标识下棋方的背景
	Bitmap isPlaySound;// 开启声音
	Bitmap noPlaySound;// 关闭声音
	Bitmap start;// 开始
	Bitmap suspend;// 暂停
	Bitmap nandutiaoZ;// 难度
	Bitmap nonandutiaoZ;// 难度不可用
	Bitmap guanggao1[] = new Bitmap[2];// 广告条1
	Bitmap guanggao2;// 广告条2

	Bitmap winJiemian;// 赢界面
	Bitmap loseJiemian;// 输界面
	Bitmap fugaiTu;// 覆盖图
	Bitmap queDinButton;// 确定按钮
	Bitmap beijint3;// 拖动条背景
	Bitmap beijint4;// 剪裁框背景

	// --------------------------------图片变量--------------------------------

	public GameView(Context context) {
		super(context);

		this.father = (GameManagerActivity) context;
		this.getHolder().addCallback(this);// 设置生命周期回调接口的实现者
		paint = new Paint();// 创建画笔
		paint.setAntiAlias(true);// 打开抗锯齿
		ViewConstant.isnoStart = false;

		// 难度数
		length = ViewConstant.nanduXS * 4;
		// 初始化难度滚动条颜色数组
		initColer();

		// 初始化棋盘所有棋子
		GameLogic.Startup();
		// 初始化数组
		initArrays();
		// 初始化图片
		initBitmap();
		ViewConstant.endTime = ViewConstant.zTime;// 总时间
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.holder = holder;
		canvas = null;
		try {
			// 锁定整个画布，在内存要求比较高的情况下，建议参数不要为null
			canvas = holder.lockCanvas(null);
			synchronized (holder) {

				draw(canvas);// 绘制
			}
		} finally {
			if (canvas != null) {
				// 并释放锁
				holder.unlockCanvasAndPost(canvas);
			}
		}
		newThread();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.threadFlag = false;
	}

	void newThread() {
		new Thread() {
			@Override
			public void run() {
				while (threadFlag) {
					if (ViewConstant.isnoStart) {
						if (ViewConstant.endTime - 500 < 0) {
							if (!cMfleg)// 如果电脑正在下棋，时间多了，则为电脑输了
							{
								ViewConstant.yingJMflag = true;
								GameLogic.Startup();// 初始化棋盘
								initArrays();// 初始化数组
								ViewConstant.endTime = ViewConstant.zTime;
								ViewConstant.isnoStart = false;
								dianjiJDT = false;
							} else {// 则为自己输了
								ViewConstant.shuJMflag = true;
								GameLogic.Startup();// 初始化棋盘
								initArrays();// 初始化数组
								ViewConstant.endTime = ViewConstant.zTime;
								ViewConstant.isnoStart = false;
								dianjiJDT = false;
							}
						} else {
							// 游戏正常进行，一直计时
							ViewConstant.endTime -= 500;
						}
					}
					// 广告条
					guanggao2X -= 10;
					if (guanggao2X < -400 * ViewConstant.xZoom) {
						guanggao2X = 400 * ViewConstant.xZoom;
					}

					onDrawcanvas();// 重绘方法
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}.start();
	}

	public void onDrawcanvas() {
		try {
			// 锁定整个画布，在内存要求比较高的情况下，建议参数不要为null
			canvas = holder.lockCanvas(null);
			synchronized (holder) {
				draw(canvas);// 绘制
			}
		} finally {
			if (canvas != null) {
				// 并释放锁
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		canvas.drawColor(Color.argb(255, 0, 0, 0));
		// 背景图
		canvas.drawBitmap(beijint, ViewConstant.sXtart, ViewConstant.sYtart,
				null);

		if (ViewConstant.isnoStart)// 如果开始了
		{
			onDrawWindowindow(canvas, ViewConstant.sXtart, ViewConstant.sYtart);
			if (flag) {
				// 上下左右位置坐标
				float left = xMove > ViewConstant.sXtart + 5
						* ViewConstant.xSpan ? ViewConstant.windowXstartLeft
						: ViewConstant.windowXstartRight;
				float top = ViewConstant.windowYstart;
				float right = left + ViewConstant.windowWidth;
				float bottom = top + ViewConstant.windowHeight;
				canvas.save();
				canvas.clipRect(new RectF(left, top, right, bottom));
				onDrawWindowindow(canvas, ViewConstant.sXtartCk,
						ViewConstant.sYtartCk);// 小窗口
				canvas.restore();
				canvas.drawBitmap(beijint4, left - 6, top - 6, null);
			}

		} else {
			canvas.drawBitmap(
					guanggao1[(int) ((Math.abs(guanggao2X / 40) % 2))],
					ViewConstant.sXtart, ViewConstant.sYtart, null);
		}

		onDrawWindowMenu(canvas, ViewConstant.sXtart, ViewConstant.sYtart);
		if (yingJMflag)// 如果是赢了
		{
			canvas.drawBitmap(fugaiTu, sXtart, sYtart, null);// 画覆盖图
			canvas.drawBitmap(winJiemian, sXtart + 2.5f * xSpan, sYtart + 5f
					* ySpan, null);// 赢背景盖图
			if (dianjiQueDing) {
				canvas.drawBitmap(scaleToFit(queDinButton, 1.2f), sXtart + 3.7f
						* xSpan, sYtart + 8.3f * ySpan, null);// 确定按钮
			} else
				canvas.drawBitmap(queDinButton, sXtart + 3.9f * xSpan, sYtart
						+ 8.5f * ySpan, null);// 确定按钮
		} else if (shuJMflag)// 输了
		{
			canvas.drawBitmap(fugaiTu, sXtart, sYtart, null);// 画覆盖图
			canvas.drawBitmap(loseJiemian, sXtart + 2.5f * xSpan, sYtart + 5f
					* ySpan, null);// 赢背景盖图
			if (dianjiQueDing) {
				canvas.drawBitmap(scaleToFit(queDinButton, 1.2f), sXtart + 3.7f
						* xSpan, sYtart + 8.3f * ySpan, null);// 确定按钮
			} else
				canvas.drawBitmap(queDinButton, sXtart + 3.9f * xSpan, sYtart
						+ 8.5f * ySpan, null);// 确定按钮
		}
	}

	// 玩家点击各个按钮，棋子事件的处理
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// 如果正在进行电脑下棋
		if (!cMfleg) {
			return false;
		}

		int col = (int) ((e.getX() - sXtart) / xSpan);
		int row = (int) ((e.getY() - sYtart) / ySpan);

		if (((e.getX() - col * xSpan - sXtart)
				* (e.getX() - col * xSpan - sXtart) + (e.getY() - row * ySpan - sYtart)
				* (e.getY() - row * ySpan - sYtart)) < xSpan / 2 * xSpan / 2) {
			bzcol = col - 1;
			bzrow = row - 1;// 看其在哪一个格子上
		} else if (((e.getX() - col * xSpan - sXtart)
				* (e.getX() - col * xSpan - sXtart) + (e.getY() - (row + 1)
				* ySpan - sYtart)
				* (e.getY() - (row + 1) * ySpan - sYtart)) < xSpan / 2 * xSpan
				/ 2) {
			bzcol = col - 1;
			bzrow = row;
		} else if (((e.getX() - (1 + col) * xSpan - sXtart)
				* (e.getX() - (1 + col) * xSpan - sXtart) + (e.getY()
				- (row + 1) * ySpan - sYtart)
				* (e.getY() - (row + 1) * ySpan - sYtart)) < xSpan / 2 * xSpan
				/ 2) {
			bzcol = col;
			bzrow = row;
		} else if (((e.getX() - (1 + col) * xSpan - sXtart)
				* (e.getX() - (1 + col) * xSpan - sXtart) + (e.getY() - row
				* ySpan - sYtart)
				* (e.getY() - row * ySpan - sYtart)) < xSpan / 2 * xSpan / 2) {
			bzcol = col;
			bzrow = row - 1;
		}
		if (e.getAction() == MotionEvent.ACTION_DOWN)// 如果按下
		{
			if (yingJMflag || shuJMflag)// 如果当前为赢界面
			{
				if (e.getX() > sXtart + 3.9f * xSpan
						&& e.getY() > sYtart + 8.5f * ySpan
						&& // 谁先下
						e.getY() < sYtart + 8.5f * ySpan + 1.5f * ySpan
						&& e.getX() < sXtart + 3.9f * xSpan + 2 * xSpan) {
					dianjiQueDing = true;
				}

				return true;
			}
			if (e.getX() > sXtart + 0.5f * xSpan
					&& e.getY() > sYtart + 13.3f * ySpan
					&& // 新局标志
					e.getY() < sYtart + 13.3f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 0.5f * xSpan + 2 * xSpan) {
				chonwanFlag = true;
				return true;
			}
			if (e.getX() > sXtart + 7f * xSpan
					&& e.getY() > sYtart + 11.5f * ySpan
					&& // 悔棋标志
					e.getY() < sYtart + 11.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 7f * xSpan + 2 * xSpan) {
				huiqiFlag = true;
				return true;
			}
			if (e.getX() > sXtart + 3.3f * xSpan
					&& e.getY() > sYtart + 13.5f * ySpan
					&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 3.3f * xSpan + 2 * xSpan)// 开始区域
			{
				dianjiKaiShi = true;
				return true;
			}
			if (e.getX() > sXtart + 5.8f * xSpan
					&& e.getY() > sYtart + 13.5f * ySpan
					&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 5.8f * xSpan + 2 * xSpan)// 难度区域
			{
				dianjiNanDu = true;
				return true;
			}
			if (e.getX() > sXtart + 8.3f * xSpan
					&& e.getY() > sYtart + 13.5f * ySpan
					&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 8.3f * xSpan + 2 * xSpan)// 声音区域
			{
				dianjishengyin = true;
				return true;
			}

			if (e.getX() > sXtart && e.getY() > sYtart + 15.3f * ySpan
					&& e.getY() < sYtart + 15.3f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 10f * xSpan + 2 * xSpan)// 进度条区域
			{
				xMove = e.getX();
				dianjiJDT = true;
				return true;
			}
			if ((bzrow + 3) * 16 + bzcol + 3 < 0
					|| (bzrow + 3) * 16 + bzcol + 3 > 255 || !isnoStart)// 如果没有开始
			{
				return false;
			}
			if (ucpcSquares[(bzrow + 3) * 16 + bzcol + 3] != 0
					&& ucpcSquares[(bzrow + 3) * 16 + bzcol + 3] / 16 == 0) {// 如果是自己的棋子

				xzflag = true;
				xzgz = (bzrow + 3) * 16 + bzcol + 3;// 选中的格子是这么多

				flag = true;// 选中了
				onDrawcanvas();// 重绘方法
				return true;
			}
		} else if (e.getAction() == MotionEvent.ACTION_MOVE) {
			float x = xMove = e.getX();
			float y = yMove = e.getY();
			if (xzflag)// 如果已经选中，则此为要着棋
			{

				if (x > 48 * 10 * xZoom + sXtart - windowWidth / 2) {
					x = 48 * 10 * xZoom + sXtart - windowWidth / 2;
				} else if (x < sXtart + windowWidth / 2 + 0 * 48 * xZoom) {
					x = sXtart + windowWidth / 2 - 0 * 48 * xZoom;
				}
				if (y > 48 * 11 * xZoom + sYtart - windowHeight / 2) {
					y = 48 * 11 * xZoom + sYtart - windowHeight / 2;
				} else if (y < sYtart + windowHeight / 2 + 0 * 48 * xZoom) {
					y = sYtart + windowHeight / 2 - 0 * 48 * xZoom;
				}
				if (xMove > sXtart + 5 * xSpan) {
					sXtartCk = sXtart
							- (x - windowWidth / 2 - windowXstartLeft);
					sYtartCk = sYtart - (y - windowHeight / 2 - windowYstart);

				} else {
					sXtartCk = sXtart
							- (x - windowWidth / 2 - windowXstartRight);
					sYtartCk = sYtart - (y - windowHeight / 2 - windowYstart);
				}
				onDrawcanvas();// 重绘方法
				return true;
			} else {
				xzflag = false;// 恢复，不为选中状态
				flag = false;// 选中了
				onDrawcanvas();// 重绘方法
				return true;
			}
		} else if (e.getAction() == MotionEvent.ACTION_UP) {

			if (yingJMflag || shuJMflag)// 如果当前为赢界面
			{
				if (dianjiQueDing == true) {
					if (e.getX() > sXtart + 3.9f * xSpan
							&& e.getY() > sYtart + 8.5f * ySpan
							&& // 谁先下
							e.getY() < sYtart + 8.5f * ySpan + 1.5f * ySpan
							&& e.getX() < sXtart + 3.9f * xSpan + 2 * xSpan) {
						shuJMflag = false;
						yingJMflag = false;
						GameLogic.Startup();// 初始化棋盘
						initArrays();// 初始化数组
						GameLogic.sdPlayer = 0;// 下棋方为自己
						endTime = zTime;// 总时间
						isnoStart = false;// 为暂停状态
					}
					dianjiQueDing = false;
				}
				onDrawcanvas();// 重绘方法

				return true;
			}

			if (e.getX() > sXtart + 7f * xSpan
					&& e.getY() > sYtart + 11.5f * ySpan
					&& // 悔棋标志
					e.getY() < sYtart + 11.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 7f * xSpan + 2 * xSpan) {// 如果是悔棋
				if (huiqiFlag == true) {
					if (!stack.empty() && stack.size() > 1) {
						if (huiqibushu > ViewConstant.huiqiBS) {
							Toast.makeText// 同时发Toast.提示玩家
							(father, "悔棋步数已经超过规定!", Toast.LENGTH_LONG).show();
							return true;
						}
						huiqibushu++;
						StackPlayChess chess = stack.pop();
						GameLogic.UndoMovePiece(chess.getMvResult(),
								chess.getPcCaptured());

						chess = stack.pop();
						GameLogic.UndoMovePiece(chess.getMvResult(),
								chess.getPcCaptured());
						if (!stack.empty()) {
							GameLogic.mvResult = stack.peek().getMvResult();
						}
						initArrays();// 数组操作
					}
					huiqiFlag = false;
					onDrawcanvas();// 重绘方法
					return false;
				}

			} else if (huiqiFlag) {
				huiqiFlag = false;
				onDrawcanvas();// 重绘方法
				return true;
			}

			if (e.getX() > sXtart + 0.5f * xSpan
					&& e.getY() > sYtart + 13.3f * ySpan
					&& // 新局标志
					e.getY() < sYtart + 13.3f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 0.5f * xSpan + 2 * xSpan) {
				if (chonwanFlag == true) {

					shuJMflag = false;
					yingJMflag = false;
					isnoStart = false;
					endTime = zTime;
					stack.clear();
					GameLogic.Startup();// 初始化棋盘
					initArrays();// 初始化数组
				}

				chonwanFlag = false;
				onDrawcanvas();// 重绘方法
				return true;
			} else if (chonwanFlag) {
				chonwanFlag = false;
				onDrawcanvas();// 重绘方法
				return true;
			}

			if (dianjiKaiShi)// 开始
			{
				if (e.getX() > sXtart + 3.3f * xSpan
						&& e.getY() > sYtart + 13.5f * ySpan
						&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
						&& e.getX() < sXtart + 3.3f * xSpan + 2 * xSpan)// 开始区域
				{
					// 该处为接点击开始时根据不同情况做不同操作
					isnoStart = !isnoStart;
					nanduBXZ = false;
					if (!isnoStart) {
						dianjiJDT = false;// 点击拖动
						ViewConstant.isnoTCNDxuanz = false;
					}
					dianjiKaiShi = false;
					onDrawcanvas();// 重绘方法

				}
				dianjiKaiShi = false;
				onDrawcanvas();// 重绘方法
				return true;
			}

			if (dianjiNanDu)// 难度区域
			{
				if (e.getX() > sXtart + 5.8f * xSpan
						&& e.getY() > sYtart + 13.5f * ySpan
						&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
						&& e.getX() < sXtart + 5.8f * xSpan + 2 * xSpan)// 难度区域
				{
					if (!isnoStart)// 如果为暂停状态下，才可用
					{
						nanduBXZ = !nanduBXZ;
						isnoTCNDxuanz = !isnoTCNDxuanz;
					} else {
						isnoTCNDxuanz = false;
						dianjiJDT = false;
					}

					isnoNanDu = !isnoNanDu;
				}

				dianjiNanDu = false;
				onDrawcanvas();// 重绘方法
				return true;
			}

			if (dianjishengyin) {
				if (e.getX() > sXtart + 8.3f * xSpan
						&& e.getY() > sYtart + 13.5f * ySpan
						&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
						&& e.getX() < sXtart + 8.3f * xSpan + 2 * xSpan)// 声音区域
				{
					isnoPlaySound = !isnoPlaySound;
				}

				dianjishengyin = false;
				onDrawcanvas();// 重绘方法
				return true;
			}

			// 是否为选择的棋子
			if (xzflag) {
				if ((bzrow + 3) * 16 + bzcol + 3 < 0
						|| (bzrow + 3) * 16 + bzcol + 3 > 255) {
					xzflag = false;
					flag = false;
					onDrawcanvas();// 重绘方法
					return true;
				}
				if (bzrow < 0 || bzrow > 9 || bzcol < 0 || bzcol > 8) {
					xzflag = false;
					flag = false;
					onDrawcanvas();// 重绘方法
					return true;
				}
				int sqDst = Chess_LoadUtil.DST(xzgz
						+ ((bzrow + 3) * 16 + bzcol + 3) * 256);
				int pcCaptured = ucpcSquares[sqDst];// 得到目的格子的是哪颗棋子
				int mv = xzgz + ((bzrow + 3) * 16 + bzcol + 3) * 256;
				if (stack.size() > 12
						&& // 判断是否走车轱辘棋
						mv == stack.get(stack.size() - 4).getMvResult()
						&& stack.get(stack.size() - 1).getMvResult() == stack
								.get(stack.size() - 5).getMvResult()
						&& stack.get(stack.size() - 5).getMvResult() == stack
								.get(stack.size() - 9).getMvResult()
						&& stack.get(stack.size() - 2).getMvResult() == stack
								.get(stack.size() - 6).getMvResult()
						&& stack.get(stack.size() - 6).getMvResult() == stack
								.get(stack.size() - 10).getMvResult()
						&& stack.get(stack.size() - 3).getMvResult() == stack
								.get(stack.size() - 7).getMvResult()
						&& stack.get(stack.size() - 3).getMvResult() == stack
								.get(stack.size() - 11).getMvResult()
						&& stack.get(stack.size() - 4).getMvResult() == stack
								.get(stack.size() - 8).getMvResult()
						&& stack.get(stack.size() - 8).getMvResult() == stack
								.get(stack.size() - 12).getMvResult()) {
					xzflag = false;
					flag = false;
					onDrawcanvas();// 重绘方法
					Toast.makeText(// 同时发Toast.提示玩家
							father, "不能重复局面走子，请重新走子。", Toast.LENGTH_SHORT)
							.show();
					return true;
				}
				if (GameLogic.LegalMove(mv)) {// 如果下棋符合规则
					if (GameLogic.MakeMove(mv, 0)) {// 如果没有被将军
						initArrays();// 初始化数组
						father.playSound(2, 1);// b播放声音玩家走棋
						huiqibushu = 0;// 悔棋标志置零
						onDrawcanvas();// 重绘方法
						stack.push(new StackPlayChess(xzgz
								+ ((bzrow + 3) * 16 + bzcol + 3) * 256,
								pcCaptured));// 下棋步骤入栈

						if (GameLogic.IsMate()) {// 如果玩家赢了
							GameLogic.Startup();// 初始化棋盘
							initArrays();// 初始化数组
							yingJMflag = true;
							father.playSound(4, 1);// 播放声音,赢了
							onDrawcanvas();// 重绘方法
						} else {
							// 电脑走棋
							new Thread() {// 启动一个线程进行电脑下棋
								@Override
								public void run() {
									ViewConstant.endTime = ViewConstant.zTime;// 时间初始化

									isRedPlayChess = true;// 正在下棋
									cMfleg = false;// 正在下棋标志

									onDrawcanvas();// 重绘方法

									// 电脑走棋
									GameLogic.SearchMain();

									int sqSrc = Chess_LoadUtil
											.SRC(GameLogic.mvResult); // 得到起始位置的数组下标
									int sqDst = Chess_LoadUtil
											.DST(GameLogic.mvResult);
									int pcCaptured = ucpcSquares[sqDst];// 得到目的格子的棋子
									Log.i("电脑走棋", "走法起点=" + sqSrc + ", 走法终点="
											+ sqDst + ", 得到目的格子的棋子="
											+ pcCaptured);

									GameLogic.MakeMove(GameLogic.mvResult, 0);

									stack.push(new StackPlayChess(
											GameLogic.mvResult, pcCaptured));// 下棋步骤入栈

									initArrays();// 数组操作

									// 如果电脑赢了
									if (GameLogic.IsMate()) {
										GameLogic.Startup();// 初始化棋盘
										initArrays();// 初始化数组
										shuJMflag = true;
										father.playSound(5, 1);// 播放声音,输了
									} else {
										father.playSound(2, 1);// 播放声音,电脑下棋了
									}

									cMfleg = true;// 下完棋子，玩家可以操控了。
									isRedPlayChess = false;
									endTime = zTime;
									onDrawcanvas();// 重绘方法
								}
							}.start();

						}
					}
					xzflag = false;
					flag = false;
					onDrawcanvas();// 重绘方法

				} else {
					xzflag = false;
					flag = false;
					onDrawcanvas();// 重绘方法
				}

			}

			return true;
		}

		return super.onTouchEvent(e);
	}

	/**
	 * 画整个窗口
	 * 
	 * @param canvas
	 * @param sXtart
	 * @param sYtart
	 */
	void onDrawWindowindow(Canvas canvas, float sXtart, float sYtart) {
		canvas.drawBitmap(chessQipan2, sXtart, sYtart, null);

		// 绘制红色填充矩形
		paint.setColor(Color.RED);// 设置画笔颜色
		paint.setStrokeWidth(3);// 设置线的粗细

		for (int i = 0; i < 10; i++)// 画横线
		{
			canvas.drawLine(ViewConstant.xSpan + sXtart, ViewConstant.ySpan
					+ ViewConstant.ySpan * i + sYtart, sXtart
					+ ViewConstant.xSpan * 9, sYtart + ViewConstant.ySpan
					+ ViewConstant.ySpan * i, paint);
		}

		for (int i = 0; i < 9; i++)// 画竖线
		{
			canvas.drawLine(sXtart + ViewConstant.xSpan + i
					* ViewConstant.xSpan, sYtart + ViewConstant.ySpan, sXtart
					+ ViewConstant.xSpan + ViewConstant.xSpan * i, sYtart
					+ ViewConstant.ySpan * 10, paint);
		}

		canvas.drawLine(sXtart + ViewConstant.xSpan * 4, sYtart
				+ ViewConstant.ySpan, sXtart + ViewConstant.xSpan * 6, sYtart
				+ ViewConstant.ySpan * 3, paint);// 绘制九宫斜线
		canvas.drawLine(sXtart + ViewConstant.xSpan * 6, sYtart
				+ ViewConstant.ySpan, sXtart + ViewConstant.xSpan * 4, sYtart
				+ ViewConstant.ySpan * 3, paint);

		canvas.drawLine(sXtart + ViewConstant.xSpan * 4, sYtart
				+ ViewConstant.ySpan * 8, sXtart + ViewConstant.xSpan * 6,
				sYtart + ViewConstant.ySpan * 10, paint);
		canvas.drawLine(sXtart + ViewConstant.xSpan * 6, sYtart
				+ ViewConstant.ySpan * 8, sXtart + ViewConstant.xSpan * 4,
				sYtart + ViewConstant.ySpan * 10, paint);

		// 绘制边框
		paint.setStrokeWidth(5);// 设置线的粗细
		canvas.drawLine(sXtart + 0.8f * ViewConstant.xSpan, sYtart + 0.8f
				* ViewConstant.ySpan, sXtart + 9.2f * ViewConstant.xSpan,
				sYtart + 0.8f * ViewConstant.ySpan, paint);
		canvas.drawLine(sXtart + 0.8f * ViewConstant.xSpan, sYtart + 0.8f
				* ViewConstant.ySpan, sXtart + 0.8f * ViewConstant.xSpan,
				sYtart + 10.2f * ViewConstant.ySpan, paint);
		canvas.drawLine(sXtart + 9.2f * ViewConstant.xSpan, sYtart + 0.8f
				* ViewConstant.ySpan, sXtart + 9.2f * ViewConstant.xSpan,
				sYtart + 10.2f * ViewConstant.ySpan, paint);
		canvas.drawLine(sXtart + 0.8f * ViewConstant.xSpan, sYtart + 10.2f
				* ViewConstant.ySpan, sXtart + 9.2f * ViewConstant.xSpan,
				sYtart + 10.2f * ViewConstant.ySpan, paint);

		canvas.drawBitmap(chuhe, sXtart + ViewConstant.xSpan + 1.8f, sYtart + 5
				* ViewConstant.ySpan + 1.0f, null);// 绘制楚河
		canvas.drawBitmap(paotai, sXtart + 2 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 3 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制炮台
		canvas.drawBitmap(paotai, sXtart + 2 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 8 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制炮台
		canvas.drawBitmap(paotai, sXtart + 8 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 3 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制炮台
		canvas.drawBitmap(paotai, sXtart + 8 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 8 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制炮台

		canvas.drawBitmap(paotai2, sXtart + 1 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵台
		canvas.drawBitmap(paotai, sXtart + 3 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵炮台
		canvas.drawBitmap(paotai, sXtart + 5 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵炮台
		canvas.drawBitmap(paotai, sXtart + 7 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵炮台
		canvas.drawBitmap(paotai1, sXtart + 9 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵炮台

		canvas.drawBitmap(paotai2, sXtart + 1 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵台
		canvas.drawBitmap(paotai, sXtart + 3 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵炮台
		canvas.drawBitmap(paotai, sXtart + 5 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵炮台
		canvas.drawBitmap(paotai, sXtart + 7 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵炮台
		canvas.drawBitmap(paotai1, sXtart + 9 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// 绘制兵炮台

		// 画棋子
		for (int i = 0; i < 10; i++)// 绘制棋子
		{
			for (int j = 0; j < 9; j++) {
				if (ucpcSquares[(i + 3) * 16 + j + 3] != 0) {
					canvas.drawBitmap(chessBitmap[ucpcSquares[(i + 3) * 16 + j
							+ 3] / 16][ucpcSquares[(i + 3) * 16 + j + 3] % 8],
							sXtart + j * ViewConstant.xSpan
									- ViewConstant.chessR + ViewConstant.xSpan,
							sYtart + i * ViewConstant.ySpan
									- ViewConstant.chessR + ViewConstant.ySpan,
							null);
				}
			}
		}
		if (flag)// 绘制拖拉效果
		{
			// 绘制选中要走棋子的标志
			canvas.drawBitmap(
					chessZouQiflag,
					sXtart
							+ (Chess_LoadUtil.FILE_X(Chess_LoadUtil.SRC(xzgz)) - 2)
							* ViewConstant.xSpan - ViewConstant.chessR,
					sYtart
							+ (Chess_LoadUtil.RANK_Y(Chess_LoadUtil.SRC(xzgz)) - 2)
							* ViewConstant.ySpan - ViewConstant.chessR, null);
			canvas.drawBitmap(chessZouQiflag, sXtart + (bzcol + 1)
					* ViewConstant.xSpan - ViewConstant.chessR, sYtart
					+ (bzrow + 1) * ViewConstant.ySpan - ViewConstant.chessR,
					null);// 绘制移动时移动到的某格

		}
		if (cMfleg && stack.size() > 0)// 绘制电脑下棋的标志
		{
			canvas.drawBitmap(
					chessZouQiflag,
					sXtart
							+ (Chess_LoadUtil.FILE_X(Chess_LoadUtil
									.SRC(GameLogic.mvResult)) - 2)
							* ViewConstant.xSpan - ViewConstant.chessR,
					sYtart
							+ (Chess_LoadUtil.RANK_Y(Chess_LoadUtil
									.SRC(GameLogic.mvResult)) - 2)
							* ViewConstant.ySpan - ViewConstant.chessR, null);

			canvas.drawBitmap(
					chessZouQiflag,
					sXtart
							+ (Chess_LoadUtil.FILE_X(Chess_LoadUtil
									.DST(GameLogic.mvResult)) - 2)
							* ViewConstant.xSpan - ViewConstant.chessR,
					sYtart
							+ (Chess_LoadUtil.RANK_Y(Chess_LoadUtil
									.DST(GameLogic.mvResult)) - 2)
							* ViewConstant.ySpan - ViewConstant.chessR, null);
		}
		if (flag) {
			paint.setAlpha(200);// 绘制放大的棋子
			canvas.drawBitmap(ViewConstant.scaleToFit(
					chessBitmap[ucpcSquares[xzgz] / 16][ucpcSquares[xzgz] % 8],
					2), xMove - 2 * ViewConstant.chessR, yMove - 2
					* ViewConstant.chessR, paint);
		}
		// 绘制悔棋等菜单
	}

	/**
	 * 画下半方的各个按钮，滚动文本等菜单窗口
	 * 
	 * @param canvas
	 * @param sXtart
	 * @param sYtart
	 */
	void onDrawWindowMenu(Canvas canvas, float sXtart, float sYtart) {

		canvas.drawBitmap(scaleToFit(minueBeijint, 1f), sXtart, sYtart + 11.0f
				* ySpan, null);// 菜单背景图

		canvas.drawBitmap(ifPlayChess, sXtart + 1f * xSpan, sYtart + 11.4f
				* ySpan, null);

		if (isRedPlayChess)// 如果是红方下棋
		{
			canvas.drawBitmap(scaleToFit(chessBitmap[1][0], 0.9f), sXtart
					+ 1.1f * xSpan, sYtart + 11.45f * ySpan, null);
		} else {
			canvas.drawBitmap(scaleToFit(chessBitmap[0][0], 0.9f), sXtart
					+ 1.1f * xSpan, sYtart + 11.45f * ySpan, null);
		}

		// 绘制时间
		drawScoreStr(canvas, endTime / 1000 / 60 < 10 ? "0" + endTime / 1000
				/ 60 : endTime / 1000 / 60 + "", sXtart + 3f * xSpan, sYtart
				+ 11.4f * ySpan);
		canvas.drawBitmap(dunhao, sXtart + scoreWidth * 2 + 3f * xSpan, sYtart
				+ 11.4f * ySpan, null);// 顿号
		drawScoreStr(canvas, endTime / 1000 % 60 < 10 ? "0" + endTime / 1000
				% 60 : endTime / 1000 % 60 + "", scoreWidth * 3 + sXtart + 3f
				* xSpan, sYtart + 11.4f * ySpan);

		if (huiqiFlag)// 是否按下了悔棋按钮
		{
			canvas.drawBitmap(scaleToFit(huiqi, 1.2f), sXtart + 6.9f * xSpan,
					sYtart + 11.25f * ySpan, null);// 悔棋
		} else {
			canvas.drawBitmap(huiqi, sXtart + 7f * xSpan, sYtart + 11.35f
					* ySpan, null);// 悔棋
		}

		canvas.drawBitmap(scaleToFit(minueBeijint, 1f), sXtart, sYtart + 12.8f
				* ySpan, null);// 菜单背景图
		if (chonwanFlag)// 新局
		{
			canvas.drawBitmap(scaleToFit(chonWan, 1.2f), sXtart + 0.3f * xSpan,
					sYtart + 12.9f * ySpan, null);// 重玩
		} else {
			canvas.drawBitmap(chonWan, sXtart + 0.8f * xSpan, sYtart + 13.2f
					* ySpan, null);// 重玩
		}
		if (isnoStart)// 开始暂停，如果已经开始
		{
			if (dianjiKaiShi)// 如果点击此处了
			{
				canvas.drawBitmap(scaleToFit(suspend, 1.2f), sXtart + 3.0f
						* xSpan, sYtart + 13.0f * ySpan, null);
			} else
				canvas.drawBitmap(suspend, sXtart + 3.3f * xSpan, sYtart
						+ 13.2f * ySpan, null);
		} else {
			if (dianjiKaiShi)// 如果点击此处了
			{
				canvas.drawBitmap(scaleToFit(start, 1.2f), sXtart + 3.0f
						* xSpan, sYtart + 13.0f * ySpan, null);
			} else
				canvas.drawBitmap(start, sXtart + 3.3f * xSpan, sYtart + 13.2f
						* ySpan, null);
		}

		if (!isnoStart)// 难度是否可选，如果可选，则表示当前状态下可以点击，为暂停状态下是为可选
		{
			if (dianjiNanDu) {
				canvas.drawBitmap(scaleToFit(nandutiaoZ, 1.2f), sXtart + 5.5f
						* xSpan, sYtart + 13.0f * ySpan, null);
			} else
				canvas.drawBitmap(nandutiaoZ, sXtart + 5.8f * xSpan, sYtart
						+ 13.2f * ySpan, null);
		} else {

			canvas.drawBitmap(nonandutiaoZ, sXtart + 5.8f * xSpan, sYtart
					+ 13.2f * ySpan, null);
		}

		if (isnoPlaySound)// 是否开启声音,如果是已经开启了声音
		{
			if (dianjishengyin) {
				canvas.drawBitmap(scaleToFit(isPlaySound, 1.2f), sXtart + 8.0f
						* xSpan, sYtart + 13.1f * ySpan, null);
			} else
				canvas.drawBitmap(isPlaySound, sXtart + 8.3f * xSpan, sYtart
						+ 13.2f * ySpan, null);
		} else {
			if (dianjishengyin) {
				canvas.drawBitmap(scaleToFit(noPlaySound, 1.2f), sXtart + 8.0f
						* xSpan, sYtart + 13.1f * ySpan, null);
			} else
				canvas.drawBitmap(noPlaySound, sXtart + 8.3f * xSpan, sYtart
						+ 13.2f * ySpan, null);
		}

		if (!isnoStart && nanduBXZ)// 如果为按下了难度
		{

			canvas.drawBitmap(beijint3, sXtart, sYtart + 14.6f * ySpan, null);
			Rect r = new Rect(50, 50, 200, 200);

			if (dianjiJDT) {
				length = (int) ((xMove - sXtart) / (xZoom * 20));
				nanduXS = (int) ((xMove - sXtart) / (90 * xZoom));
				if (nanduXS < 1) {
					nanduXS = 1;
				}
				if (nanduXS > 5) {
					nanduXS = 5;
				}
				Constant.LIMIT_DEPTH = nanduXS * 6;
			}

			if (length < 1) {
				length = 1;
			} else if (length > 20) {
				length = 20;
			}
			for (int i = 0; i < length; i++) {
				paint.setARGB(color[i][0], color[i][1], color[i][2], 0);// 设置画笔颜色
				r = new Rect((int) (sXtart + 60 * xZoom + i * xZoom * 18),
						(int) (sYtart + 15.3f * ySpan), (int) (sXtart + 60
								* xZoom + (i) * xZoom * 18 + 13 * xZoom),
						(int) (sYtart + 15.3f * ySpan + 32 * xZoom));
				canvas.drawRect(r, paint);
			}

		} else {
			canvas.drawBitmap(minueBeijint, sXtart, sYtart + 14.6f * ySpan,
					null);
			// 绘制广告
			float left = sXtart + 40 * xZoom;
			float top = sYtart + 15f * ySpan;
			float right = sXtart + 9 * xSpan;
			float bottom = sYtart + 15.2f * ySpan + 40 * xZoom;
			canvas.save();
			canvas.clipRect(new RectF(left, top, right, bottom));
			canvas.drawBitmap(guanggao2, guanggao2X, sYtart + 14.8f * ySpan,
					null);
			canvas.restore();
		}
	}

	// 初始化难度滚动条颜色数组
	void initColer() {
		int r = (200 - 61) / 20;
		int g = (159 - 24) / 20;
		int b = (107 - 6) / 20;
		for (int i = 0; i < 20; i++) {

			color[i][0] = 61 + i * r;
			color[i][1] = 24 + i * g;
			color[i][2] = 5 + i * b;
		}
	}

	/**
	 * 初始化数组
	 */
	void initArrays() {
		for (int i = 0; i < 256; i++) {
			ucpcSquares[i] = GameLogic.ucpcSquares[i];
		}
	}

	/**
	 * 初始化图片
	 */
	void initBitmap() {
		float xZoom = ViewConstant.xZoom;

		beijint4 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.beijingkuangtu), xZoom);// 剪裁框
		beijint3 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.beijing3), xZoom);// 难度选择拖动它背景
		fugaiTu = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.shuyingfugai), xZoom);// 覆盖图
		winJiemian = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.yingjiemian), xZoom);// 赢界面
		loseJiemian = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.shuijiemian), xZoom);// 输界面
		queDinButton = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.queding), xZoom);// 再来一局
		guanggao1[0] = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.guanggao1), xZoom);
		guanggao1[1] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.floor),
				xZoom);
		guanggao2 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.guanggao2), xZoom);// 广告12
		nonandutiaoZ = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.nonandu), xZoom);// 难度
		nandutiaoZ = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.nanduxuanz), xZoom);// 难度
		suspend = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.zhanting), xZoom);// 暂停
		start = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.start),
				xZoom);// 开始
		isPlaySound = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.kaiqishengy), xZoom);// 开启声音图片
		noPlaySound = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.guanbishengy), xZoom);// 关闭声音图片
		ifPlayChess = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.caidanxiaqifang), xZoom);// 菜单下棋方背景图
		minueBeijint = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.beijing), xZoom);// 菜单背景图
		beijint = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.beijintu), xZoom);// 背景图
		chonWan = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.chonwan), xZoom);// 重玩
		huiqi = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.huiqi),
				xZoom);// 悔棋

		chessQipan2 = ViewConstant
				.scaleToFit(BitmapFactory.decodeResource(getResources(),
						R.drawable.floor2), xZoom);// 棋盘
		chuhe = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.chuhe),
				xZoom);// 楚河
		chessZouQiflag = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.selected), xZoom);// 标志位
		iscore[0] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d0),
				xZoom);// 数字图
		iscore[1] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d1),
				xZoom);
		iscore[2] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d2),
				xZoom);
		iscore[3] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d3),
				xZoom);
		iscore[4] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d4),
				xZoom);
		iscore[5] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d5),
				xZoom);
		iscore[6] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d6),
				xZoom);
		iscore[7] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d7),
				xZoom);
		iscore[8] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d8),
				xZoom);
		iscore[9] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d9),
				xZoom);

		dunhao = ViewConstant
				.scaleToFit(BitmapFactory.decodeResource(getResources(),
						R.drawable.dunhao), xZoom);// 顿号
		xZoom = xZoom * 0.6f;
		paotai = ViewConstant
				.scaleToFit(BitmapFactory.decodeResource(getResources(),
						R.drawable.paotai), xZoom);// 炮台
		paotai1 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.paotai1), xZoom);// 炮台2
		paotai2 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.paotai2), xZoom);// 炮台2
		xZoom = ViewConstant.xZoom * 0.9f;

		// 棋子
		chessBitmap = new Bitmap[][] {
				{
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.bk), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.ba), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.bb), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.bn), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.br), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.bc), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.bp), xZoom),

				},
				{
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.rk), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.ra), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.rb), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.rn), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.rr), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.rc), xZoom),
						ViewConstant.scaleToFit(BitmapFactory.decodeResource(
								getResources(), R.drawable.rp), xZoom), } };
	}

	/**
	 * 绘制字符串方法
	 * 
	 * @param canvas
	 * @param s
	 * @param width
	 * @param height
	 */
	public void drawScoreStr(Canvas canvas, String s, float width, float height) {
		// 绘制得分
		String scoreStr = s;
		if (s.length() < 2) {
			s = "0" + s;
		}
		for (int i = 0; i < scoreStr.length(); i++) {// 循环绘制得分
			int tempScore = scoreStr.charAt(i) - '0';
			canvas.drawBitmap(iscore[tempScore], width + i * scoreWidth,
					height, null);
		}
	}

}
