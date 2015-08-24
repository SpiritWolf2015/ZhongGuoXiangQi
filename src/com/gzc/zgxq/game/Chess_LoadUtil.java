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

	/** �ж������Ƿ��������� */ 
		public static boolean IN_BOARD(int sq) {
		  return Constant.ccInBoard[sq] != 0;
		}

		/** �ж������Ƿ��ھŹ��� */ 
		public static boolean IN_FORT(int sq) {
		  return Constant.ccInFort[sq] != 0;
		}


		/**
		 * ��ø��ӵ�������row
		 * @param sq
		 * @return
		 */
		public static int RANK_Y(int sq) {
		  return sq >> 4;
		}
		
		/**
		 * ��ø��ӵĺ�����col
		 * @param sq
		 * @return
		 */
		public static int FILE_X(int sq) {
		  return sq & 15;
		}

		// ����������ͺ������ø���
		public static int COORD_XY(int x, int y) {
		  return x + (y << 4);
		}

		
		/**
		 * ��ת����
		 * @param sq
		 * @return
		 */
		public static int SQUARE_FLIP(int sq) {
		  return 254 - sq;
		}

		// ������ˮƽ����
		public static int FILE_FLIP(int x) {
		  return 14 - x;
		}

		// �����괹ֱ����
		public static int RANK_FLIP(int y) {
		  return 15 - y;
		}

		// ����ˮƽ����
		public static int MIRROR_SQUARE(int sq) {
		  return COORD_XY(FILE_FLIP(FILE_X(sq)), RANK_Y(sq));
		}

		/** ����ˮƽ���� */ 
		public static int SQUARE_FORWARD(int sq, int sd) {
		  return sq - 16 + (sd << 5);
		}
		
		/**
		 * �߷��Ƿ����˧(��)�Ĳ���
		 * @param sqSrc
		 * @param sqDst
		 * @return
		 */
		public static boolean KING_SPAN(int sqSrc, int sqDst) {
		  return ccLegalSpan[sqDst - sqSrc + 256] == 1;
		}

		// �߷��Ƿ������(ʿ)�Ĳ���
		public static boolean ADVISOR_SPAN(int sqSrc, int sqDst) {
		  return ccLegalSpan[sqDst - sqSrc + 256] == 2;
		}
		
		/** �߷��Ƿ������(��)�Ĳ��� */ 
		public static boolean BISHOP_SPAN(int sqSrc, int sqDst) {
		  return ccLegalSpan[sqDst - sqSrc + 256] == 3;
		}
		
		/** ��(��)�۵�λ�� */ 
		public static int BISHOP_PIN(int sqSrc, int sqDst) {
		  return (sqSrc + sqDst) >> 1;
		}
		
		/** ���ȵ�λ�� */ 
		public static int KNIGHT_PIN(int sqSrc, int sqDst) {
		  return sqSrc + ccKnightPin[sqDst - sqSrc + 256];
		}

		/** �Ƿ�δ���� */ 
		public static boolean HOME_HALF(int sq, int sd) {
		  return (sq & 0x80) != (sd << 7);
		}

		/**  �Ƿ��ѹ��� */
		public static boolean AWAY_HALF(int sq, int sd) {
		  return (sq & 0x80) == (sd << 7);
		}

		/**  �Ƿ��ںӵ�ͬһ�� */
		public static boolean SAME_HALF(int sqSrc, int sqDst) {
		  return ((sqSrc ^ sqDst) & 0x80) == 0;
		}
		
		/**  �Ƿ���ͬһ�� */
		public static boolean SAME_RANK(int sqSrc, int sqDst) {
		  return ((sqSrc ^ sqDst) & 0xf0) == 0;
		}
		
		/**  �Ƿ���ͬһ�� */
		public static boolean SAME_FILE(int sqSrc, int sqDst) {
		  return ((sqSrc ^ sqDst) & 0x0f) == 0;
		}

		/** ��ú�ڱ��(������8��������16) */
		public static int SIDE_TAG(int sd) {
		  return 8 + (sd << 3);
		}

		/** ��öԷ���ڱ�� */ 
		public static int OPP_SIDE_TAG(int sd) {
		  return 16 - (sd << 3);
		}
		
		/**
		 * ����߷������
		 * @param mv
		 * @return
		 */
		public static int SRC(int mv) {
		  return mv & 255;
		}
		
		/**
		 * ����߷����յ�
		 * @param move ��һ������߷�
		 * @return �ò��߷����Ӻ�����λ����256�����е��±�
		 */
		public static int DST(int move) {
		  return move >> 8;
		}

		/** ���������յ����߷� */ 
		public static int MOVE(int sqSrc, int sqDst) {
		  return sqSrc + sqDst * 256;
		}

		// �߷�ˮƽ����
		public static int MIRROR_MOVE(int mv) {
		  return MOVE(MIRROR_SQUARE(SRC(mv)), MIRROR_SQUARE(DST(mv)));
		}

}
