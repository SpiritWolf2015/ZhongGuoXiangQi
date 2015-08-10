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
 * 标示当前所在界面的枚举
 * @author gzc
 */
enum WhichView{
	WELCOME_VIEW,
	GAME_VIEW
};

/**
 * 游戏主Activity，也是游戏的控制器。
 * 初始化相应的界面，并根据其他界面发送回来的消息切换用户需要的界面。
 * @author gzc
 */
@SuppressLint({ "HandlerLeak", "UseSparseArrays" })
public class GameManagerActivity extends Activity {

	/**
	 * 欢迎界面View
	 */
	WelcomeView welcomeView;	
	/**
	 * 游戏界面View
	 */
	GameView gameView;
	/**
	 * 哪个View
	 */
	WhichView whichView;
	/**
	 * 声音池
	 */
	SoundPool soundPool;
	/**
	 * 声音池中ID与自定义声音ID的Map
	 */
	HashMap<Integer, Integer> soundPoolMap;
	/**
	 * 消息处理器
	 */
	public Handler hd = new Handler(){		
		@Override public void handleMessage(Message message){
			switch(message.what){			
				case 0:
					System.out.println("GZC->handleMessage线程id："+Thread.currentThread().getId());
					// 进入游戏View
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
				// 设置全屏显示
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				// 设置竖屏模式
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				// 游戏过程中只允许多媒体音量,而不允许通化音量
				setVolumeControlStream(AudioManager.STREAM_MUSIC);
				initPm();// 调整屏幕分辨率
				initSound();	//初始化声音
//				goToWelcomeView();	// 进入欢迎界面
				this.goToGameView();	// 进入游戏界面界面
	}
	
	/**
	 * 进入欢迎View
	 */
	protected void goToWelcomeView() {
		if (welcomeView == null) {
			welcomeView = new WelcomeView(this);
		}
		setContentView(welcomeView);
		whichView = WhichView.WELCOME_VIEW;
	}

	/**
	 * 进入游戏View
	 */
	protected void goToGameView() {
		gameView = new GameView(GameManagerActivity.this);
		setContentView(gameView);
		whichView = WhichView.GAME_VIEW;
		System.out.println("gzc::>>goToGameView");
	}
	
	/**
	 * 初始屏幕分辨率
	 */
	void initPm() {
		// 获取屏幕分辨率
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
		// 声音池
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();
		// 吃东西音乐
		soundPoolMap.put(1, soundPool.load(this, R.raw.noxiaqi, 1));
		soundPoolMap.put(2, soundPool.load(this, R.raw.dong, 1)); // 玩家走棋
		soundPoolMap.put(4, soundPool.load(this, R.raw.win, 1)); // 赢了
		soundPoolMap.put(5, soundPool.load(this, R.raw.loss, 1)); // 输了
	}

	// 播放声音
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