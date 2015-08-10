package com.gzc.zgxq;

import java.util.HashMap;

import com.gzc.testsurfaceview.R;
import com.gzc.zgxq.view.GameView;
import com.gzc.zgxq.view.ViewConstant;
import com.gzc.zgxq.view.WelcomeView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;


/** 
 * ��ʾ��ǰ���ڽ����ö��
 * @author gzc
 */
enum WhichView{
	WELCOME_VIEW,
	GAME_VIEW
};

/**
 * ��Ϸ��Activity��Ҳ����Ϸ�Ŀ�������
 * ��ʼ����Ӧ�Ľ��棬�������������淢�ͻ�������Ϣ�л��û���Ҫ�Ľ��档
 * @author gzc
 */
@SuppressLint({ "HandlerLeak", "UseSparseArrays" })
public class GameManagerActivity extends Activity {

	/**
	 * ��ӭ����View
	 */
	WelcomeView welcomeView;	
	/**
	 * ��Ϸ����View
	 */
	GameView gameView;
	/**
	 * �ĸ�View
	 */
	WhichView whichView;
	/**
	 * ������
	 */
	SoundPool soundPool;
	/**
	 * ��������ID���Զ�������ID��Map
	 */
	HashMap<Integer, Integer> soundPoolMap;
	/**
	 * ��Ϣ������
	 */
	public Handler hd = new Handler(){		
		@Override public void handleMessage(Message message){
			switch(message.what){			
				case 0:
					System.out.println("GZC->handleMessage�߳�id��"+Thread.currentThread().getId());
					// ������ϷView
					goToGameView();
				break;
			}
		}
	};
	
	/** Called when the activity is first created. */	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				// NO_TITLE
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				// ����ȫ����ʾ
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				// ��������ģʽ
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				// ��Ϸ������ֻ�����ý������,��������ͨ������
				setVolumeControlStream(AudioManager.STREAM_MUSIC);
				initPm();// ������Ļ�ֱ���
				initSound();	//��ʼ������
//				goToWelcomeView();	// ���뻶ӭ����
				this.goToGameView();	// ������Ϸ�������
	}
	
	/**
	 * ���뻶ӭView
	 */
	protected void goToWelcomeView() {
		if (welcomeView == null) {
			welcomeView = new WelcomeView(this);
		}
		setContentView(welcomeView);
		whichView = WhichView.WELCOME_VIEW;
	}

	/**
	 * ������ϷView
	 */
	protected void goToGameView() {
		gameView = new GameView(GameManagerActivity.this);
		setContentView(gameView);
		whichView = WhichView.GAME_VIEW;
		System.out.println("gzc::>>goToGameView");
	}
	
	/**
	 * ��ʼ��Ļ�ֱ���
	 */
	void initPm() {
		// ��ȡ��Ļ�ֱ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int tempHeight = (int) (ViewConstant.height = dm.heightPixels);
		int tempWidth = (int) (ViewConstant.width = dm.widthPixels);
		//
		if (tempHeight > tempWidth) {
			ViewConstant.height = tempHeight;
			ViewConstant.width = tempWidth;
		} else {
			ViewConstant.height  = tempWidth;
			ViewConstant.width  = tempHeight;
		}
		float zoomx = ViewConstant.width / 480;
		float zoomy = ViewConstant.height / 800;
		if (zoomx > zoomy) {
			ViewConstant.xZoom = ViewConstant.yZoom = zoomy;

		} else {
			ViewConstant.xZoom = ViewConstant.yZoom = zoomx;
		}
		 ViewConstant.sXtart = (ViewConstant.width - 480 * ViewConstant.xZoom) / 2;
		 ViewConstant.sYtart = ( ViewConstant.height - 800 *  ViewConstant.yZoom) / 2;
		ViewConstant.initChessViewFinal(); 
	}
	
	@SuppressWarnings("deprecation")
	public void initSound() {
		// ������
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();
		// �Զ�������
		soundPoolMap.put(1, soundPool.load(this, R.raw.noxiaqi, 1));
		soundPoolMap.put(2, soundPool.load(this, R.raw.dong, 1)); // �������
		soundPoolMap.put(4, soundPool.load(this, R.raw.win, 1)); // Ӯ��
		soundPoolMap.put(5, soundPool.load(this, R.raw.loss, 1)); // ����
	}

	// ��������
	public void playSound(int sound, int loop) {
		if (!ViewConstant.isnoPlaySound) {
			return;
		}
		AudioManager mgr = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;
		soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
	}
	
}