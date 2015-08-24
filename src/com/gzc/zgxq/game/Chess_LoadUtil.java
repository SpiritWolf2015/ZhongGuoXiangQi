/**
 * 
 */
package com.gzc.zgxq.game;

import static com.gzc.zgxq.game.Constant.*;

/**
 * @author gzc
 *
 */
public class Chess_LoadUtil {

	/** 判断棋子是否在棋盘中 */ 
		public static boolean IN_BOARD(int sq) {
		  return Constant.ccInBoard[sq] != 0;
		}

		/** 判断棋子是否在九宫中 */ 
		public static boolean IN_FORT(int sq) {
		  return Constant.ccInFort[sq] != 0;
		}


		/**
		 * 获得格子的纵坐标row
		 * @param sq
		 * @return
		 */
		public static int RANK_Y(int sq) {
		  return sq >> 4;
		}
		
		/**
		 * 获得格子的横坐标col
		 * @param sq
		 * @return
		 */
		public static int FILE_X(int sq) {
		  return sq & 15;
		}

		// 根据纵坐标和横坐标获得格子
		public static int COORD_XY(int x, int y) {
		  return x + (y << 4);
		}

		
		/**
		 * 翻转格子
		 * @param sq
		 * @return
		 */
		public static int SQUARE_FLIP(int sq) {
		  return 254 - sq;
		}

		// 纵坐标水平镜像
		public static int FILE_FLIP(int x) {
		  return 14 - x;
		}

		// 横坐标垂直镜像
		public static int RANK_FLIP(int y) {
		  return 15 - y;
		}

		// 格子水平镜像
		public static int MIRROR_SQUARE(int sq) {
		  return COORD_XY(FILE_FLIP(FILE_X(sq)), RANK_Y(sq));
		}

		/** 格子水平镜像 */ 
		public static int SQUARE_FORWARD(int sq, int sd) {
		  return sq - 16 + (sd << 5);
		}
		
		/**
		 * 走法是否符合帅(将)的步长
		 * @param sqSrc
		 * @param sqDst
		 * @return
		 */
		public static boolean KING_SPAN(int sqSrc, int sqDst) {
		  return ccLegalSpan[sqDst - sqSrc + 256] == 1;
		}

		// 走法是否符合仕(士)的步长
		public static boolean ADVISOR_SPAN(int sqSrc, int sqDst) {
		  return ccLegalSpan[sqDst - sqSrc + 256] == 2;
		}
		
		/** 走法是否符合相(象)的步长 */ 
		public static boolean BISHOP_SPAN(int sqSrc, int sqDst) {
		  return ccLegalSpan[sqDst - sqSrc + 256] == 3;
		}
		
		/** 相(象)眼的位置 */ 
		public static int BISHOP_PIN(int sqSrc, int sqDst) {
		  return (sqSrc + sqDst) >> 1;
		}
		
		/** 马腿的位置 */ 
		public static int KNIGHT_PIN(int sqSrc, int sqDst) {
		  return sqSrc + ccKnightPin[sqDst - sqSrc + 256];
		}

		/** 是否未过河 */ 
		public static boolean HOME_HALF(int sq, int sd) {
		  return (sq & 0x80) != (sd << 7);
		}

		/**  是否已过河 */
		public static boolean AWAY_HALF(int sq, int sd) {
		  return (sq & 0x80) == (sd << 7);
		}

		/**  是否在河的同一边 */
		public static boolean SAME_HALF(int sqSrc, int sqDst) {
		  return ((sqSrc ^ sqDst) & 0x80) == 0;
		}
		
		/**  是否在同一行 */
		public static boolean SAME_RANK(int sqSrc, int sqDst) {
		  return ((sqSrc ^ sqDst) & 0xf0) == 0;
		}
		
		/**  是否在同一列 */
		public static boolean SAME_FILE(int sqSrc, int sqDst) {
		  return ((sqSrc ^ sqDst) & 0x0f) == 0;
		}

		/** 获得红黑标记(红子是8，黑子是16) */
		public static int SIDE_TAG(int sd) {
		  return 8 + (sd << 3);
		}

		/** 获得对方红黑标记 */ 
		public static int OPP_SIDE_TAG(int sd) {
		  return 16 - (sd << 3);
		}
		
		/**
		 * 获得走法的起点
		 * @param mv
		 * @return
		 */
		public static int SRC(int mv) {
		  return mv & 255;
		}
		
		/**
		 * 获得走法的终点
		 * @param move 走一步棋的走法
		 * @return 该步走法落子后，棋子位置在256数组中的下标
		 */
		public static int DST(int move) {
		  return move >> 8;
		}

		/** 根据起点和终点获得走法 */ 
		public static int MOVE(int sqSrc, int sqDst) {
		  return sqSrc + sqDst * 256;
		}

		// 走法水平镜像
		public static int MIRROR_MOVE(int mv) {
		  return MOVE(MIRROR_SQUARE(SRC(mv)), MIRROR_SQUARE(DST(mv)));
		}

}
