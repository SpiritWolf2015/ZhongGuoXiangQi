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
 * ��Ϸ����View
 * 
 * @author gzc
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	/**
	 * ��Activity
	 */
	GameManagerActivity father;
	/**
	 * ����
	 */
	Paint paint;
	/**
	 * �Ѷ���
	 */
	int length;
	/**
	 * �Ѷȹ�������ɫ����
	 */
	int[][] color = new int[20][3];
	/**
	 * ���岽��
	 */
	int huiqibushu = 0;
	/**
	 * ����
	 */
	Canvas canvas;
	/**
	 * �ƶ�����
	 */
	float xMove, yMove;
	/**
	 * �����Ƿ���Ч
	 */
	boolean cMfleg = true;
	/**
	 * �Ƿ�Ϊ��һ������
	 */
	boolean isFlage;
	/**
	 * �Ƿ�Ϊѡ�У�ѡ������Ҫ�ƶ��Ļ��ͻ����ƶ�
	 */
	boolean xzflag = false;
	/**
	 * �߳��Ƿ�����
	 */
	boolean threadFlag = true;
	/**
	 * ѡ�еĳ�ʼ����
	 */
	int xzgz = 0;
	/**
	 * �Ƿ�Ϊ�ƶ���־
	 */
	boolean flag;
	/**
	 * �����Ϊ�����־
	 */
	boolean huiqiFlag = false;
	/**
	 * �����Ϊ�����־
	 */
	boolean chonwanFlag = false;
	/**
	 * �Ѷ��Ƿ��ѡ
	 */
	boolean isnoNanDu = true;
	/**
	 * �����Ϊ�Ѷ�
	 */
	boolean dianjiNanDu;
	/**
	 * �����Ϊ�¾�
	 */
	boolean dianjiXinJu;
	/**
	 * �����Ϊ��ʼ
	 */
	boolean dianjiKaiShi;
	/**
	 * ���Ϊ��������
	 */
	boolean dianjishengyin;
	/**
	 * �����Ϊ�϶�
	 */
	boolean dianjiJDT;
	/**
	 * �����Ϊȷ����ť
	 */
	boolean dianjiQueDing;
	/**
	 * ���巽��־λ��falseΪ�ڷ�����
	 */
	boolean isRedPlayChess;

	boolean nanduBXZ;
	int bzcol;
	int bzrow;

	Stack<StackPlayChess> stack = new Stack<StackPlayChess>();
	SurfaceHolder holder;
	int ucpcSquares[] = new int[256];

	float guanggao2X = 0;

	// --------------------------------ͼƬ����--------------------------------
	Bitmap[][] chessBitmap;// ��������ͼƬ
	Bitmap chessZouQiflag;// ��־����
	Bitmap paotai;// ��̨
	Bitmap paotai2;// ��̨���ص�ʱ��
	Bitmap paotai1;
	Bitmap chuhe;// ����ͼƬ

	Bitmap chessQipan2;// ���̱߿�
	Bitmap huiqi;// ����ͼƬ
	Bitmap chonWan;// ��������
	Bitmap[] iscore = new Bitmap[10];// ����ͼ
	Bitmap dunhao;// �ٺ�
	Bitmap beijint;// ����ͼ
	Bitmap minueBeijint;// �˵�����ͼ
	Bitmap ifPlayChess;// ��ʶ���巽�ı���
	Bitmap isPlaySound;// ��������
	Bitmap noPlaySound;// �ر�����
	Bitmap start;// ��ʼ
	Bitmap suspend;// ��ͣ
	Bitmap nandutiaoZ;// �Ѷ�
	Bitmap nonandutiaoZ;// �ѶȲ�����
	Bitmap guanggao1[] = new Bitmap[2];// �����1
	Bitmap guanggao2;// �����2

	Bitmap winJiemian;// Ӯ����
	Bitmap loseJiemian;// �����
	Bitmap fugaiTu;// ����ͼ
	Bitmap queDinButton;// ȷ����ť
	Bitmap beijint3;// �϶�������
	Bitmap beijint4;// ���ÿ򱳾�

	// --------------------------------ͼƬ����--------------------------------

	public GameView(Context context) {
		super(context);

		this.father = (GameManagerActivity) context;
		this.getHolder().addCallback(this);// �����������ڻص��ӿڵ�ʵ����
		paint = new Paint();// ��������
		paint.setAntiAlias(true);// �򿪿����
		ViewConstant.isnoStart = false;

		// �Ѷ���
		length = ViewConstant.nanduXS * 4;
		// ��ʼ���Ѷȹ�������ɫ����
		initColer();

		// ��ʼ��������������
		GameLogic.Startup();
		// ��ʼ������
		initArrays();
		// ��ʼ��ͼƬ
		initBitmap();
		ViewConstant.endTime = ViewConstant.zTime;// ��ʱ��
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.holder = holder;
		canvas = null;
		try {
			// �����������������ڴ�Ҫ��Ƚϸߵ�����£����������ҪΪnull
			canvas = holder.lockCanvas(null);
			synchronized (holder) {

				draw(canvas);// ����
			}
		} finally {
			if (canvas != null) {
				// ���ͷ���
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
							if (!cMfleg)// ��������������壬ʱ����ˣ���Ϊ��������
							{
								ViewConstant.yingJMflag = true;
								GameLogic.Startup();// ��ʼ������
								initArrays();// ��ʼ������
								ViewConstant.endTime = ViewConstant.zTime;
								ViewConstant.isnoStart = false;
								dianjiJDT = false;
							} else {// ��Ϊ�Լ�����
								ViewConstant.shuJMflag = true;
								GameLogic.Startup();// ��ʼ������
								initArrays();// ��ʼ������
								ViewConstant.endTime = ViewConstant.zTime;
								ViewConstant.isnoStart = false;
								dianjiJDT = false;
							}
						} else {
							// ��Ϸ�������У�һֱ��ʱ
							ViewConstant.endTime -= 500;
						}
					}
					// �����
					guanggao2X -= 10;
					if (guanggao2X < -400 * ViewConstant.xZoom) {
						guanggao2X = 400 * ViewConstant.xZoom;
					}

					onDrawcanvas();// �ػ淽��
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
			// �����������������ڴ�Ҫ��Ƚϸߵ�����£����������ҪΪnull
			canvas = holder.lockCanvas(null);
			synchronized (holder) {
				draw(canvas);// ����
			}
		} finally {
			if (canvas != null) {
				// ���ͷ���
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		canvas.drawColor(Color.argb(255, 0, 0, 0));
		// ����ͼ
		canvas.drawBitmap(beijint, ViewConstant.sXtart, ViewConstant.sYtart,
				null);

		if (ViewConstant.isnoStart)// �����ʼ��
		{
			onDrawWindowindow(canvas, ViewConstant.sXtart, ViewConstant.sYtart);
			if (flag) {
				// ��������λ������
				float left = xMove > ViewConstant.sXtart + 5
						* ViewConstant.xSpan ? ViewConstant.windowXstartLeft
						: ViewConstant.windowXstartRight;
				float top = ViewConstant.windowYstart;
				float right = left + ViewConstant.windowWidth;
				float bottom = top + ViewConstant.windowHeight;
				canvas.save();
				canvas.clipRect(new RectF(left, top, right, bottom));
				onDrawWindowindow(canvas, ViewConstant.sXtartCk,
						ViewConstant.sYtartCk);// С����
				canvas.restore();
				canvas.drawBitmap(beijint4, left - 6, top - 6, null);
			}

		} else {
			canvas.drawBitmap(
					guanggao1[(int) ((Math.abs(guanggao2X / 40) % 2))],
					ViewConstant.sXtart, ViewConstant.sYtart, null);
		}

		onDrawWindowMenu(canvas, ViewConstant.sXtart, ViewConstant.sYtart);
		if (yingJMflag)// �����Ӯ��
		{
			canvas.drawBitmap(fugaiTu, sXtart, sYtart, null);// ������ͼ
			canvas.drawBitmap(winJiemian, sXtart + 2.5f * xSpan, sYtart + 5f
					* ySpan, null);// Ӯ������ͼ
			if (dianjiQueDing) {
				canvas.drawBitmap(scaleToFit(queDinButton, 1.2f), sXtart + 3.7f
						* xSpan, sYtart + 8.3f * ySpan, null);// ȷ����ť
			} else
				canvas.drawBitmap(queDinButton, sXtart + 3.9f * xSpan, sYtart
						+ 8.5f * ySpan, null);// ȷ����ť
		} else if (shuJMflag)// ����
		{
			canvas.drawBitmap(fugaiTu, sXtart, sYtart, null);// ������ͼ
			canvas.drawBitmap(loseJiemian, sXtart + 2.5f * xSpan, sYtart + 5f
					* ySpan, null);// Ӯ������ͼ
			if (dianjiQueDing) {
				canvas.drawBitmap(scaleToFit(queDinButton, 1.2f), sXtart + 3.7f
						* xSpan, sYtart + 8.3f * ySpan, null);// ȷ����ť
			} else
				canvas.drawBitmap(queDinButton, sXtart + 3.9f * xSpan, sYtart
						+ 8.5f * ySpan, null);// ȷ����ť
		}
	}

	// ��ҵ��������ť�������¼��Ĵ���
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// ������ڽ��е�������
		if (!cMfleg) {
			return false;
		}

		int col = (int) ((e.getX() - sXtart) / xSpan);
		int row = (int) ((e.getY() - sYtart) / ySpan);

		if (((e.getX() - col * xSpan - sXtart)
				* (e.getX() - col * xSpan - sXtart) + (e.getY() - row * ySpan - sYtart)
				* (e.getY() - row * ySpan - sYtart)) < xSpan / 2 * xSpan / 2) {
			bzcol = col - 1;
			bzrow = row - 1;// ��������һ��������
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
		if (e.getAction() == MotionEvent.ACTION_DOWN)// �������
		{
			if (yingJMflag || shuJMflag)// �����ǰΪӮ����
			{
				if (e.getX() > sXtart + 3.9f * xSpan
						&& e.getY() > sYtart + 8.5f * ySpan
						&& // ˭����
						e.getY() < sYtart + 8.5f * ySpan + 1.5f * ySpan
						&& e.getX() < sXtart + 3.9f * xSpan + 2 * xSpan) {
					dianjiQueDing = true;
				}

				return true;
			}
			if (e.getX() > sXtart + 0.5f * xSpan
					&& e.getY() > sYtart + 13.3f * ySpan
					&& // �¾ֱ�־
					e.getY() < sYtart + 13.3f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 0.5f * xSpan + 2 * xSpan) {
				chonwanFlag = true;
				return true;
			}
			if (e.getX() > sXtart + 7f * xSpan
					&& e.getY() > sYtart + 11.5f * ySpan
					&& // �����־
					e.getY() < sYtart + 11.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 7f * xSpan + 2 * xSpan) {
				huiqiFlag = true;
				return true;
			}
			if (e.getX() > sXtart + 3.3f * xSpan
					&& e.getY() > sYtart + 13.5f * ySpan
					&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 3.3f * xSpan + 2 * xSpan)// ��ʼ����
			{
				dianjiKaiShi = true;
				return true;
			}
			if (e.getX() > sXtart + 5.8f * xSpan
					&& e.getY() > sYtart + 13.5f * ySpan
					&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 5.8f * xSpan + 2 * xSpan)// �Ѷ�����
			{
				dianjiNanDu = true;
				return true;
			}
			if (e.getX() > sXtart + 8.3f * xSpan
					&& e.getY() > sYtart + 13.5f * ySpan
					&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 8.3f * xSpan + 2 * xSpan)// ��������
			{
				dianjishengyin = true;
				return true;
			}

			if (e.getX() > sXtart && e.getY() > sYtart + 15.3f * ySpan
					&& e.getY() < sYtart + 15.3f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 10f * xSpan + 2 * xSpan)// ����������
			{
				xMove = e.getX();
				dianjiJDT = true;
				return true;
			}
			if ((bzrow + 3) * 16 + bzcol + 3 < 0
					|| (bzrow + 3) * 16 + bzcol + 3 > 255 || !isnoStart)// ���û�п�ʼ
			{
				return false;
			}
			if (ucpcSquares[(bzrow + 3) * 16 + bzcol + 3] != 0
					&& ucpcSquares[(bzrow + 3) * 16 + bzcol + 3] / 16 == 0) {// ������Լ�������

				xzflag = true;
				xzgz = (bzrow + 3) * 16 + bzcol + 3;// ѡ�еĸ�������ô��

				flag = true;// ѡ����
				onDrawcanvas();// �ػ淽��
				return true;
			}
		} else if (e.getAction() == MotionEvent.ACTION_MOVE) {
			float x = xMove = e.getX();
			float y = yMove = e.getY();
			if (xzflag)// ����Ѿ�ѡ�У����ΪҪ����
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
				onDrawcanvas();// �ػ淽��
				return true;
			} else {
				xzflag = false;// �ָ�����Ϊѡ��״̬
				flag = false;// ѡ����
				onDrawcanvas();// �ػ淽��
				return true;
			}
		} else if (e.getAction() == MotionEvent.ACTION_UP) {

			if (yingJMflag || shuJMflag)// �����ǰΪӮ����
			{
				if (dianjiQueDing == true) {
					if (e.getX() > sXtart + 3.9f * xSpan
							&& e.getY() > sYtart + 8.5f * ySpan
							&& // ˭����
							e.getY() < sYtart + 8.5f * ySpan + 1.5f * ySpan
							&& e.getX() < sXtart + 3.9f * xSpan + 2 * xSpan) {
						shuJMflag = false;
						yingJMflag = false;
						GameLogic.Startup();// ��ʼ������
						initArrays();// ��ʼ������
						GameLogic.sdPlayer = 0;// ���巽Ϊ�Լ�
						endTime = zTime;// ��ʱ��
						isnoStart = false;// Ϊ��ͣ״̬
					}
					dianjiQueDing = false;
				}
				onDrawcanvas();// �ػ淽��

				return true;
			}

			if (e.getX() > sXtart + 7f * xSpan
					&& e.getY() > sYtart + 11.5f * ySpan
					&& // �����־
					e.getY() < sYtart + 11.5f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 7f * xSpan + 2 * xSpan) {// ����ǻ���
				if (huiqiFlag == true) {
					if (!stack.empty() && stack.size() > 1) {
						if (huiqibushu > ViewConstant.huiqiBS) {
							Toast.makeText// ͬʱ��Toast.��ʾ���
							(father, "���岽���Ѿ������涨!", Toast.LENGTH_LONG).show();
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
						initArrays();// �������
					}
					huiqiFlag = false;
					onDrawcanvas();// �ػ淽��
					return false;
				}

			} else if (huiqiFlag) {
				huiqiFlag = false;
				onDrawcanvas();// �ػ淽��
				return true;
			}

			if (e.getX() > sXtart + 0.5f * xSpan
					&& e.getY() > sYtart + 13.3f * ySpan
					&& // �¾ֱ�־
					e.getY() < sYtart + 13.3f * ySpan + 1 * ySpan
					&& e.getX() < sXtart + 0.5f * xSpan + 2 * xSpan) {
				if (chonwanFlag == true) {

					shuJMflag = false;
					yingJMflag = false;
					isnoStart = false;
					endTime = zTime;
					stack.clear();
					GameLogic.Startup();// ��ʼ������
					initArrays();// ��ʼ������
				}

				chonwanFlag = false;
				onDrawcanvas();// �ػ淽��
				return true;
			} else if (chonwanFlag) {
				chonwanFlag = false;
				onDrawcanvas();// �ػ淽��
				return true;
			}

			if (dianjiKaiShi)// ��ʼ
			{
				if (e.getX() > sXtart + 3.3f * xSpan
						&& e.getY() > sYtart + 13.5f * ySpan
						&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
						&& e.getX() < sXtart + 3.3f * xSpan + 2 * xSpan)// ��ʼ����
				{
					// �ô�Ϊ�ӵ����ʼʱ���ݲ�ͬ�������ͬ����
					isnoStart = !isnoStart;
					nanduBXZ = false;
					if (!isnoStart) {
						dianjiJDT = false;// ����϶�
						ViewConstant.isnoTCNDxuanz = false;
					}
					dianjiKaiShi = false;
					onDrawcanvas();// �ػ淽��

				}
				dianjiKaiShi = false;
				onDrawcanvas();// �ػ淽��
				return true;
			}

			if (dianjiNanDu)// �Ѷ�����
			{
				if (e.getX() > sXtart + 5.8f * xSpan
						&& e.getY() > sYtart + 13.5f * ySpan
						&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
						&& e.getX() < sXtart + 5.8f * xSpan + 2 * xSpan)// �Ѷ�����
				{
					if (!isnoStart)// ���Ϊ��ͣ״̬�£��ſ���
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
				onDrawcanvas();// �ػ淽��
				return true;
			}

			if (dianjishengyin) {
				if (e.getX() > sXtart + 8.3f * xSpan
						&& e.getY() > sYtart + 13.5f * ySpan
						&& e.getY() < sYtart + 13.5f * ySpan + 1 * ySpan
						&& e.getX() < sXtart + 8.3f * xSpan + 2 * xSpan)// ��������
				{
					isnoPlaySound = !isnoPlaySound;
				}

				dianjishengyin = false;
				onDrawcanvas();// �ػ淽��
				return true;
			}

			// �Ƿ�Ϊѡ�������
			if (xzflag) {
				if ((bzrow + 3) * 16 + bzcol + 3 < 0
						|| (bzrow + 3) * 16 + bzcol + 3 > 255) {
					xzflag = false;
					flag = false;
					onDrawcanvas();// �ػ淽��
					return true;
				}
				if (bzrow < 0 || bzrow > 9 || bzcol < 0 || bzcol > 8) {
					xzflag = false;
					flag = false;
					onDrawcanvas();// �ػ淽��
					return true;
				}
				int sqDst = Chess_LoadUtil.DST(xzgz
						+ ((bzrow + 3) * 16 + bzcol + 3) * 256);
				int pcCaptured = ucpcSquares[sqDst];// �õ�Ŀ�ĸ��ӵ����Ŀ�����
				int mv = xzgz + ((bzrow + 3) * 16 + bzcol + 3) * 256;
				if (stack.size() > 12
						&& // �ж��Ƿ��߳������
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
					onDrawcanvas();// �ػ淽��
					Toast.makeText(// ͬʱ��Toast.��ʾ���
							father, "�����ظ��������ӣ����������ӡ�", Toast.LENGTH_SHORT)
							.show();
					return true;
				}
				if (GameLogic.LegalMove(mv)) {// ���������Ϲ���
					if (GameLogic.MakeMove(mv, 0)) {// ���û�б�����
						initArrays();// ��ʼ������
						father.playSound(2, 1);// b���������������
						huiqibushu = 0;// �����־����
						onDrawcanvas();// �ػ淽��
						stack.push(new StackPlayChess(xzgz
								+ ((bzrow + 3) * 16 + bzcol + 3) * 256,
								pcCaptured));// ���岽����ջ

						if (GameLogic.IsMate()) {// ������Ӯ��
							GameLogic.Startup();// ��ʼ������
							initArrays();// ��ʼ������
							yingJMflag = true;
							father.playSound(4, 1);// ��������,Ӯ��
							onDrawcanvas();// �ػ淽��
						} else {
							// ��������
							new Thread() {// ����һ���߳̽��е�������
								@Override
								public void run() {
									ViewConstant.endTime = ViewConstant.zTime;// ʱ���ʼ��

									isRedPlayChess = true;// ��������
									cMfleg = false;// ���������־

									onDrawcanvas();// �ػ淽��

									// ��������
									GameLogic.SearchMain();

									int sqSrc = Chess_LoadUtil
											.SRC(GameLogic.mvResult); // �õ���ʼλ�õ������±�
									int sqDst = Chess_LoadUtil
											.DST(GameLogic.mvResult);
									int pcCaptured = ucpcSquares[sqDst];// �õ�Ŀ�ĸ��ӵ�����
									Log.i("��������", "�߷����=" + sqSrc + ", �߷��յ�="
											+ sqDst + ", �õ�Ŀ�ĸ��ӵ�����="
											+ pcCaptured);

									GameLogic.MakeMove(GameLogic.mvResult, 0);

									stack.push(new StackPlayChess(
											GameLogic.mvResult, pcCaptured));// ���岽����ջ

									initArrays();// �������

									// �������Ӯ��
									if (GameLogic.IsMate()) {
										GameLogic.Startup();// ��ʼ������
										initArrays();// ��ʼ������
										shuJMflag = true;
										father.playSound(5, 1);// ��������,����
									} else {
										father.playSound(2, 1);// ��������,����������
									}

									cMfleg = true;// �������ӣ���ҿ��Բٿ��ˡ�
									isRedPlayChess = false;
									endTime = zTime;
									onDrawcanvas();// �ػ淽��
								}
							}.start();

						}
					}
					xzflag = false;
					flag = false;
					onDrawcanvas();// �ػ淽��

				} else {
					xzflag = false;
					flag = false;
					onDrawcanvas();// �ػ淽��
				}

			}

			return true;
		}

		return super.onTouchEvent(e);
	}

	/**
	 * ����������
	 * 
	 * @param canvas
	 * @param sXtart
	 * @param sYtart
	 */
	void onDrawWindowindow(Canvas canvas, float sXtart, float sYtart) {
		canvas.drawBitmap(chessQipan2, sXtart, sYtart, null);

		// ���ƺ�ɫ������
		paint.setColor(Color.RED);// ���û�����ɫ
		paint.setStrokeWidth(3);// �����ߵĴ�ϸ

		for (int i = 0; i < 10; i++)// ������
		{
			canvas.drawLine(ViewConstant.xSpan + sXtart, ViewConstant.ySpan
					+ ViewConstant.ySpan * i + sYtart, sXtart
					+ ViewConstant.xSpan * 9, sYtart + ViewConstant.ySpan
					+ ViewConstant.ySpan * i, paint);
		}

		for (int i = 0; i < 9; i++)// ������
		{
			canvas.drawLine(sXtart + ViewConstant.xSpan + i
					* ViewConstant.xSpan, sYtart + ViewConstant.ySpan, sXtart
					+ ViewConstant.xSpan + ViewConstant.xSpan * i, sYtart
					+ ViewConstant.ySpan * 10, paint);
		}

		canvas.drawLine(sXtart + ViewConstant.xSpan * 4, sYtart
				+ ViewConstant.ySpan, sXtart + ViewConstant.xSpan * 6, sYtart
				+ ViewConstant.ySpan * 3, paint);// ���ƾŹ�б��
		canvas.drawLine(sXtart + ViewConstant.xSpan * 6, sYtart
				+ ViewConstant.ySpan, sXtart + ViewConstant.xSpan * 4, sYtart
				+ ViewConstant.ySpan * 3, paint);

		canvas.drawLine(sXtart + ViewConstant.xSpan * 4, sYtart
				+ ViewConstant.ySpan * 8, sXtart + ViewConstant.xSpan * 6,
				sYtart + ViewConstant.ySpan * 10, paint);
		canvas.drawLine(sXtart + ViewConstant.xSpan * 6, sYtart
				+ ViewConstant.ySpan * 8, sXtart + ViewConstant.xSpan * 4,
				sYtart + ViewConstant.ySpan * 10, paint);

		// ���Ʊ߿�
		paint.setStrokeWidth(5);// �����ߵĴ�ϸ
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
				* ViewConstant.ySpan + 1.0f, null);// ���Ƴ���
		canvas.drawBitmap(paotai, sXtart + 2 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 3 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ������̨
		canvas.drawBitmap(paotai, sXtart + 2 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 8 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ������̨
		canvas.drawBitmap(paotai, sXtart + 8 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 3 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ������̨
		canvas.drawBitmap(paotai, sXtart + 8 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 8 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ������̨

		canvas.drawBitmap(paotai2, sXtart + 1 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ�̨
		canvas.drawBitmap(paotai, sXtart + 3 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ���̨
		canvas.drawBitmap(paotai, sXtart + 5 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ���̨
		canvas.drawBitmap(paotai, sXtart + 7 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ���̨
		canvas.drawBitmap(paotai1, sXtart + 9 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 4 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ���̨

		canvas.drawBitmap(paotai2, sXtart + 1 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ�̨
		canvas.drawBitmap(paotai, sXtart + 3 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ���̨
		canvas.drawBitmap(paotai, sXtart + 5 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ���̨
		canvas.drawBitmap(paotai, sXtart + 7 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ���̨
		canvas.drawBitmap(paotai1, sXtart + 9 * ViewConstant.xSpan
				- ViewConstant.chessR * 0.86f, sYtart + 7 * ViewConstant.ySpan
				- ViewConstant.chessR * 0.86f, null);// ���Ʊ���̨

		// ������
		for (int i = 0; i < 10; i++)// ��������
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
		if (flag)// ��������Ч��
		{
			// ����ѡ��Ҫ�����ӵı�־
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
					null);// �����ƶ�ʱ�ƶ�����ĳ��

		}
		if (cMfleg && stack.size() > 0)// ���Ƶ�������ı�־
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
			paint.setAlpha(200);// ���ƷŴ������
			canvas.drawBitmap(ViewConstant.scaleToFit(
					chessBitmap[ucpcSquares[xzgz] / 16][ucpcSquares[xzgz] % 8],
					2), xMove - 2 * ViewConstant.chessR, yMove - 2
					* ViewConstant.chessR, paint);
		}
		// ���ƻ���Ȳ˵�
	}

	/**
	 * ���°뷽�ĸ�����ť�������ı��Ȳ˵�����
	 * 
	 * @param canvas
	 * @param sXtart
	 * @param sYtart
	 */
	void onDrawWindowMenu(Canvas canvas, float sXtart, float sYtart) {

		canvas.drawBitmap(scaleToFit(minueBeijint, 1f), sXtart, sYtart + 11.0f
				* ySpan, null);// �˵�����ͼ

		canvas.drawBitmap(ifPlayChess, sXtart + 1f * xSpan, sYtart + 11.4f
				* ySpan, null);

		if (isRedPlayChess)// ����Ǻ췽����
		{
			canvas.drawBitmap(scaleToFit(chessBitmap[1][0], 0.9f), sXtart
					+ 1.1f * xSpan, sYtart + 11.45f * ySpan, null);
		} else {
			canvas.drawBitmap(scaleToFit(chessBitmap[0][0], 0.9f), sXtart
					+ 1.1f * xSpan, sYtart + 11.45f * ySpan, null);
		}

		// ����ʱ��
		drawScoreStr(canvas, endTime / 1000 / 60 < 10 ? "0" + endTime / 1000
				/ 60 : endTime / 1000 / 60 + "", sXtart + 3f * xSpan, sYtart
				+ 11.4f * ySpan);
		canvas.drawBitmap(dunhao, sXtart + scoreWidth * 2 + 3f * xSpan, sYtart
				+ 11.4f * ySpan, null);// �ٺ�
		drawScoreStr(canvas, endTime / 1000 % 60 < 10 ? "0" + endTime / 1000
				% 60 : endTime / 1000 % 60 + "", scoreWidth * 3 + sXtart + 3f
				* xSpan, sYtart + 11.4f * ySpan);

		if (huiqiFlag)// �Ƿ����˻��尴ť
		{
			canvas.drawBitmap(scaleToFit(huiqi, 1.2f), sXtart + 6.9f * xSpan,
					sYtart + 11.25f * ySpan, null);// ����
		} else {
			canvas.drawBitmap(huiqi, sXtart + 7f * xSpan, sYtart + 11.35f
					* ySpan, null);// ����
		}

		canvas.drawBitmap(scaleToFit(minueBeijint, 1f), sXtart, sYtart + 12.8f
				* ySpan, null);// �˵�����ͼ
		if (chonwanFlag)// �¾�
		{
			canvas.drawBitmap(scaleToFit(chonWan, 1.2f), sXtart + 0.3f * xSpan,
					sYtart + 12.9f * ySpan, null);// ����
		} else {
			canvas.drawBitmap(chonWan, sXtart + 0.8f * xSpan, sYtart + 13.2f
					* ySpan, null);// ����
		}
		if (isnoStart)// ��ʼ��ͣ������Ѿ���ʼ
		{
			if (dianjiKaiShi)// �������˴���
			{
				canvas.drawBitmap(scaleToFit(suspend, 1.2f), sXtart + 3.0f
						* xSpan, sYtart + 13.0f * ySpan, null);
			} else
				canvas.drawBitmap(suspend, sXtart + 3.3f * xSpan, sYtart
						+ 13.2f * ySpan, null);
		} else {
			if (dianjiKaiShi)// �������˴���
			{
				canvas.drawBitmap(scaleToFit(start, 1.2f), sXtart + 3.0f
						* xSpan, sYtart + 13.0f * ySpan, null);
			} else
				canvas.drawBitmap(start, sXtart + 3.3f * xSpan, sYtart + 13.2f
						* ySpan, null);
		}

		if (!isnoStart)// �Ѷ��Ƿ��ѡ�������ѡ�����ʾ��ǰ״̬�¿��Ե����Ϊ��ͣ״̬����Ϊ��ѡ
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

		if (isnoPlaySound)// �Ƿ�������,������Ѿ�����������
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

		if (!isnoStart && nanduBXZ)// ���Ϊ�������Ѷ�
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
				paint.setARGB(color[i][0], color[i][1], color[i][2], 0);// ���û�����ɫ
				r = new Rect((int) (sXtart + 60 * xZoom + i * xZoom * 18),
						(int) (sYtart + 15.3f * ySpan), (int) (sXtart + 60
								* xZoom + (i) * xZoom * 18 + 13 * xZoom),
						(int) (sYtart + 15.3f * ySpan + 32 * xZoom));
				canvas.drawRect(r, paint);
			}

		} else {
			canvas.drawBitmap(minueBeijint, sXtart, sYtart + 14.6f * ySpan,
					null);
			// ���ƹ��
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

	// ��ʼ���Ѷȹ�������ɫ����
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
	 * ��ʼ������
	 */
	void initArrays() {
		for (int i = 0; i < 256; i++) {
			ucpcSquares[i] = GameLogic.ucpcSquares[i];
		}
	}

	/**
	 * ��ʼ��ͼƬ
	 */
	void initBitmap() {
		float xZoom = ViewConstant.xZoom;

		beijint4 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.beijingkuangtu), xZoom);// ���ÿ�
		beijint3 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.beijing3), xZoom);// �Ѷ�ѡ���϶�������
		fugaiTu = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.shuyingfugai), xZoom);// ����ͼ
		winJiemian = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.yingjiemian), xZoom);// Ӯ����
		loseJiemian = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.shuijiemian), xZoom);// �����
		queDinButton = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.queding), xZoom);// ����һ��
		guanggao1[0] = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.guanggao1), xZoom);
		guanggao1[1] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.floor),
				xZoom);
		guanggao2 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.guanggao2), xZoom);// ���12
		nonandutiaoZ = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.nonandu), xZoom);// �Ѷ�
		nandutiaoZ = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.nanduxuanz), xZoom);// �Ѷ�
		suspend = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.zhanting), xZoom);// ��ͣ
		start = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.start),
				xZoom);// ��ʼ
		isPlaySound = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.kaiqishengy), xZoom);// ��������ͼƬ
		noPlaySound = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.guanbishengy), xZoom);// �ر�����ͼƬ
		ifPlayChess = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.caidanxiaqifang), xZoom);// �˵����巽����ͼ
		minueBeijint = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.beijing), xZoom);// �˵�����ͼ
		beijint = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.beijintu), xZoom);// ����ͼ
		chonWan = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.chonwan), xZoom);// ����
		huiqi = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.huiqi),
				xZoom);// ����

		chessQipan2 = ViewConstant
				.scaleToFit(BitmapFactory.decodeResource(getResources(),
						R.drawable.floor2), xZoom);// ����
		chuhe = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.chuhe),
				xZoom);// ����
		chessZouQiflag = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.selected), xZoom);// ��־λ
		iscore[0] = ViewConstant.scaleToFit(
				BitmapFactory.decodeResource(getResources(), R.drawable.d0),
				xZoom);// ����ͼ
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
						R.drawable.dunhao), xZoom);// �ٺ�
		xZoom = xZoom * 0.6f;
		paotai = ViewConstant
				.scaleToFit(BitmapFactory.decodeResource(getResources(),
						R.drawable.paotai), xZoom);// ��̨
		paotai1 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.paotai1), xZoom);// ��̨2
		paotai2 = ViewConstant.scaleToFit(BitmapFactory.decodeResource(
				getResources(), R.drawable.paotai2), xZoom);// ��̨2
		xZoom = ViewConstant.xZoom * 0.9f;

		// ����
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
	 * �����ַ�������
	 * 
	 * @param canvas
	 * @param s
	 * @param width
	 * @param height
	 */
	public void drawScoreStr(Canvas canvas, String s, float width, float height) {
		// ���Ƶ÷�
		String scoreStr = s;
		if (s.length() < 2) {
			s = "0" + s;
		}
		for (int i = 0; i < scoreStr.length(); i++) {// ѭ�����Ƶ÷�
			int tempScore = scoreStr.charAt(i) - '0';
			canvas.drawBitmap(iscore[tempScore], width + i * scoreWidth,
					height, null);
		}
	}

}
