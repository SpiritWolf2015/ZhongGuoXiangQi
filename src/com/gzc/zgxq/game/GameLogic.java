package com.gzc.zgxq.game;

import static com.gzc.zgxq.game.Chess_LoadUtil.*;
import static com.gzc.zgxq.game.Constant.*;

import java.util.Arrays;

import android.util.Log;

import com.gzc.zgxq.view.ViewConstant;

/**
 * 走法引擎主类， 该类通过最大最小搜索，alpha-beta搜索，迭代加深搜索，alpha截断，beta截断等算法来共同搜索出最佳下棋步骤。
 * 
 * @author gzc
 * 
 */
public class GameLogic {

	/**
	 * 轮到谁走，0 = 红方，1 = 黑方
	 */
	public static int sdPlayer = 0;
	/**
	 * 棋盘上的棋子，搜索数组
	 */
	public static int ucpcSquares[] = new int[256];
	/**
	 * 红、黑双方的子力价值
	 */
	public static int vlWhite, vlBlack;
	/**
	 * 距离根节点的步数
	 */
	public static int nDistance;
	/**
	 * 电脑走的棋
	 */
	public static int mvResult;
	/**
	 * 历史表
	 */
	public static int nHistoryTable[] = new int[65536];

	/** 初始化历史表 */
	public static void InitHistorytable() {
		for (int i = 0; i < nHistoryTable.length; i++) {
			nHistoryTable[i] = 0;
		}
	}

	/**
	 * 初始化棋盘
	 */
	public static void Startup() {
		int sq, pc;
		sdPlayer = vlWhite = vlBlack = nDistance = 0;
		// 初始化为零
		for (int i = 0; i < 256; i++) {
			ucpcSquares[i] = 0;
		}
		for (sq = 0; sq < 256; sq++) {
			// 根据初始棋子位置数组，在棋盘上放棋子，for循环结束，棋盘上所有棋子也就按初始位置摆好了
			pc = Constant.cucpcStartup[sq];
			if (pc != 0) {
				// 如初始游戏时，sq=52(黑方左边的车), cucpcStartup[sq]为20,pc =
				// cucpcStartup[sq]，此时pc = 20
				// 黑方加分，cucvlPiecePos[pc -
				// 16][SQUARE_FLIP(sq)]为cucvlPiecePos[4][202]=206，为黑方子力价值加206分。

				// 在棋盘上放一枚棋子
				AddPiece(sq, pc);
			}
		}
	}

	/**
	 * 在棋盘上放一枚棋子
	 * 
	 * @param sq
	 *            位置下标
	 * @param pc
	 *            哪颗棋子
	 */
	public static void AddPiece(int sq, int pc) {
		ucpcSquares[sq] = pc;
		// 红方加分，黑方(注意"cucvlPiecePos"取值要颠倒)减分
		// 小于16是红方棋子，大于16为黑方棋子，0表示没棋子
		if (pc < 16) {
			vlWhite += Constant.cucvlPiecePos[pc - 8][sq];
		} else {
			vlBlack += Constant.cucvlPiecePos[pc - 16][Chess_LoadUtil
					.SQUARE_FLIP(sq)];
		}
	}

	/**
	 * 从棋盘上拿走一枚棋子，当拿走一枚棋子时，要将拿走的该棋子对应该位子的子力价值从该方在该局下的局面价值上减去
	 * 
	 * @param sq
	 *            位置下标
	 * @param pc
	 *            哪颗棋子
	 */
	public static void DelPiece(int sq, int pc) {
		ucpcSquares[sq] = 0;
		// 红方减分，黑方(注意"cucvlPiecePos"取值要颠倒)加分
		if (pc < 16) {
			vlWhite -= Constant.cucvlPiecePos[pc - 8][sq];
		} else {
			vlBlack -= Constant.cucvlPiecePos[pc - 16][Chess_LoadUtil
					.SQUARE_FLIP(sq)];
		}
		Log.i("DelPiece", "pc = " + pc + ", sq = " + sq);
	}

	/**
	 * 撤消搬一步棋的棋子，该方法要传入走棋的步骤，以及该走棋步骤下的目标位置上的棋子。
	 * 先将该棋子搬到初始位置，如果该步骤下的目标位置上的棋子不为空，即： 是吃子走法时， 把被吃掉的棋子重新搬到该位置，以此来实现撤销搬一步棋的功能。
	 * 
	 * @param mv
	 *            走棋的步骤
	 * @param pcCaptured
	 *            该走棋步骤下的目标位置上的棋子
	 */
	public static void UndoMovePiece(int mv, int pcCaptured) {
		int sqSrc, sqDst, pc;
		sqSrc = Chess_LoadUtil.SRC(mv); // 得到起始位置的数组下标
		sqDst = Chess_LoadUtil.DST(mv); // 得到目标位置的数组下标
		pc = ucpcSquares[sqDst]; // 得到目的格子的棋子
		DelPiece(sqDst, pc); // 删除目标格子的棋子
		AddPiece(sqSrc, pc); // 在起始格子上放棋子
		if (pcCaptured != 0) { // 如果目标格子上起初有棋子
			AddPiece(sqDst, pcCaptured); // 将该棋子放在目标格子上
		}
	}

	/**
	 * 判断走法是否合理，生成所有走法后，判断每个走法是否合理，只把合理的走法留下来。
	 * 
	 * @param mv
	 * @return
	 */
	public static boolean LegalMove(int mv) {
		int sqSrc, sqDst, sqPin;
		int pcSelfSide, pcSrc, pcDst, nDelta;
		// 判断走法是否合法，需要经过以下的判断过程：

		// 1. 判断起始格是否有自己的棋子
		sqSrc = Chess_LoadUtil.SRC(mv);
		pcSrc = ucpcSquares[sqSrc];
		pcSelfSide = Chess_LoadUtil.SIDE_TAG(sdPlayer); // 获得红黑标记(红子是8，黑子是16)
		if ((pcSrc & pcSelfSide) == 0) {
			return false;
		}

		// 2. 判断目标格是否有自己的棋子
		sqDst = Chess_LoadUtil.DST(mv);
		pcDst = ucpcSquares[sqDst];
		if ((pcDst & pcSelfSide) != 0) {
			return false;
		}

		// 3. 根据棋子的类型检查走法是否合理
		switch (pcSrc - pcSelfSide) {
		case PIECE_KING:
			return IN_FORT(sqDst) && KING_SPAN(sqSrc, sqDst);
		case PIECE_ADVISOR:
			return IN_FORT(sqDst) && ADVISOR_SPAN(sqSrc, sqDst);
		case PIECE_BISHOP:
			return SAME_HALF(sqSrc, sqDst) && BISHOP_SPAN(sqSrc, sqDst)
					&& ucpcSquares[BISHOP_PIN(sqSrc, sqDst)] == 0;
		case PIECE_KNIGHT:
			sqPin = KNIGHT_PIN(sqSrc, sqDst);
			return sqPin != sqSrc && ucpcSquares[sqPin] == 0;
		case PIECE_ROOK:
		case PIECE_CANNON:
			if (SAME_RANK(sqSrc, sqDst)) {
				nDelta = (sqDst < sqSrc ? -1 : 1);
			} else if (SAME_FILE(sqSrc, sqDst)) {
				nDelta = (sqDst < sqSrc ? -16 : 16);
			} else {
				return false;
			}
			sqPin = sqSrc + nDelta;
			while (sqPin != sqDst && ucpcSquares[sqPin] == 0) {
				sqPin += nDelta;
			}
			if (sqPin == sqDst) {
				return pcDst == 0 || pcSrc - pcSelfSide == PIECE_ROOK;
			} else if (pcDst != 0 && pcSrc - pcSelfSide == PIECE_CANNON) {
				sqPin += nDelta;
				while (sqPin != sqDst && ucpcSquares[sqPin] == 0) {
					sqPin += nDelta;
				}
				return sqPin == sqDst;
			} else {
				return false;
			}
		case PIECE_PAWN:
			if (Chess_LoadUtil.AWAY_HALF(sqDst, sdPlayer)
					&& (sqDst == sqSrc - 1 || sqDst == sqSrc + 1)) {
				return true;
			}
			return sqDst == Chess_LoadUtil.SQUARE_FORWARD(sqSrc, sdPlayer);
		default:
			return false;
		}
	}

	/**
	 * 迭代加深搜索过程，该方法从0到最大搜索深度进行搜索。
	 */
	public static void SearchMain() {
		int i, vl;
		i = vl = 0;

		// 初始化
		InitHistorytable();
		nDistance = 0; // 初始步数
		long start = System.nanoTime();

		// 迭代加深过程
		for (i = 1; i <= Constant.LIMIT_DEPTH; i++) {
			// 搜索
			vl = SearchFull(-Constant.MATE_VALUE, Constant.MATE_VALUE, i);
			// 搜索到杀棋，就终止搜索
			if (vl > Constant.WIN_VALUE || vl < -Constant.WIN_VALUE) {
				break;
			}
			// 超过时间，就终止搜索
			if ((System.nanoTime() - start) / 1.e9 > ViewConstant.thinkDeeplyTime) {
				break;
			}
		}
	}

	final static String LOG_SearchFull = "SearchFull";

	/**
	 * 超出边界(Fail-Soft)的Alpha-Beta搜索过程，最主要的方法，
	 * 通过递归调用，遍历所有走法，然后得出每个走法下的局面价值，根据其价值的大小搜索出最佳下棋步骤。
	 * 
	 * @param vlAlpha
	 * @param vlBeta
	 * @param nDepth
	 * @return
	 */
	public static int SearchFull(int vlAlpha, int vlBeta, int nDepth) {
		int i = 0, nGenMoves = 0, pcCaptured = 0;
		int vl, vlBest, mvBest;
		// 每一层生成的所有走法
		Integer moves[] = new Integer[Constant.MAX_GEN_MOVES];

		// 一个Alpha-Beta完全搜索分为以下6个阶段:

		// 1. 到达水平线，则返回局面评价值
		if (0 == nDepth) {
			return Evaluate();
		}

		// 2. 初始化最佳值和最佳走法
		vlBest = -Constant.MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)
		mvBest = 0; // 这样可以知道，是否搜索到了Beta走法或PV走法，以便保存到历史表

		// 3. 生成全部走法，并根据历史表排序
		nGenMoves = GenerateMoves(moves);
		Arrays.sort(moves, 0, nGenMoves, new MyComparator()); // 不是全都排序，只对产生的走法排序，数组中其他值不变
		for (int i2 = 0; i2 < moves.length; i2++) {
			Log.i("生成走法排序后", "第" + (i2 + 1) + "个走法mvs[" + i2 + "] = " + moves[i2]);
		}

		// 4. 逐一走这些走法，并进行递归
		for (i = 0; i < nGenMoves; i++) {
			pcCaptured = ucpcSquares[Chess_LoadUtil.DST(moves[i])];
			Log.i(LOG_SearchFull, "第" + (i + 1) + "个走法mvs[" + i + "] = "
					+ moves[i] + ", pcCaptured=" + pcCaptured);

			if (MakeMove(moves[i], pcCaptured)) {
				// 递归
				vl = -SearchFull(-vlBeta, -vlAlpha, nDepth - 1);
				UndoMakeMove(moves[i], pcCaptured);

				// 5. 进行Alpha-Beta大小判断和截断
				if (vl > vlBest) { // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
					vlBest = vl; // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
					if (vl >= vlBeta) { // 找到一个Beta走法
						mvBest = moves[i]; // Beta走法要保存到历史表
						break; // Beta截断
					}
					if (vl > vlAlpha) { // 找到一个PV走法
						mvBest = moves[i]; // PV走法要保存到历史表
						vlAlpha = vl; // 缩小Alpha-Beta边界
					}
				}
			}
		}

		// 6. 所有走法都搜索完了，把最佳走法(不能是Alpha走法)保存到历史表，返回最佳值
		if (vlBest == -Constant.MATE_VALUE) {
			// 如果是杀棋，就根据杀棋步数给出评价
			return nDistance - Constant.MATE_VALUE;
		}
		if (mvBest != 0) {
			// 如果不是Alpha走法，就将最佳走法保存到历史表
			nHistoryTable[mvBest] += nDepth * nDepth;
			if (nDistance == 0) {
				// 搜索根节点时，总是有一个最佳走法(因为全窗口搜索不会超出边界)，将这个走法保存下来
				mvResult = mvBest;
			}
		}
		Log.i("SearchFull( ) over", "vlBest = " + vlBest);
		return vlBest;
	}

	final static String LOG_Evaluate = "局面评价";

	/**
	 * 局面评价函数
	 * 
	 * @return
	 */
	public static int Evaluate() {
		int value = (sdPlayer == 0 ? vlWhite - vlBlack : vlBlack - vlWhite)
				+ Constant.ADVANCED_VALUE;
		Log.i(LOG_Evaluate, value + "");
		return value;
	}

	/**
	 * 生成所有走法，该方法根据该局面来生成所有该方的走棋步骤
	 * 
	 * @param mvs
	 * @return
	 */
	public static int GenerateMoves(Integer[] mvs) {
		int i, j, nGenMoves, nDelta, sqSrc, sqDst;
		int pcSelfSide, pcOppSide, pcSrc, pcDst;

		// 生成所有走法，需要经过以下2个步骤：

		nGenMoves = 0;
		pcSelfSide = Chess_LoadUtil.SIDE_TAG(sdPlayer);
		pcOppSide = Chess_LoadUtil.OPP_SIDE_TAG(sdPlayer);

		for (sqSrc = 0; sqSrc < 256; sqSrc++) {
			// 1. 找到一个本方棋子，再做以下判断：
			pcSrc = ucpcSquares[sqSrc];
			if ((pcSrc & pcSelfSide) == 0) {
				continue;
			}

			// 2. 根据棋子确定走法
			switch (pcSrc - pcSelfSide) {
			// 将帅
			case Constant.PIECE_KING:
				for (i = 0; i < 4; i++) {
					sqDst = sqSrc + Constant.ccKingDelta[i];
					if (!Chess_LoadUtil.IN_FORT(sqDst)) {
						continue;
					}
					pcDst = ucpcSquares[sqDst];
					if ((pcDst & pcSelfSide) == 0) {
						mvs[nGenMoves] = Chess_LoadUtil.MOVE(sqSrc, sqDst);// 根据起点和终点获得走法
						Log.i("GenerateMoves", "mvs[" + nGenMoves + "] = "
								+ mvs[nGenMoves]);
						nGenMoves++;
					}
				}
				break;
			// 士
			case Constant.PIECE_ADVISOR:
				for (i = 0; i < 4; i++) {
					sqDst = sqSrc + Constant.ccAdvisorDelta[i];
					if (!IN_FORT(sqDst)) {
						continue;
					}
					pcDst = ucpcSquares[sqDst];
					if ((pcDst & pcSelfSide) == 0) {
						mvs[nGenMoves] = MOVE(sqSrc, sqDst);
						nGenMoves++;
					}
				}
				break;
			case Constant.PIECE_BISHOP:
				for (i = 0; i < 4; i++) {
					sqDst = sqSrc + Constant.ccAdvisorDelta[i];
					if (!(Chess_LoadUtil.IN_BOARD(sqDst)
							&& HOME_HALF(sqDst, sdPlayer) && ucpcSquares[sqDst] == 0)) {
						continue;
					}
					sqDst += ccAdvisorDelta[i];
					pcDst = ucpcSquares[sqDst];
					if ((pcDst & pcSelfSide) == 0) {
						mvs[nGenMoves] = MOVE(sqSrc, sqDst);
						nGenMoves++;
					}
				}
				break;
			case Constant.PIECE_KNIGHT:
				for (i = 0; i < 4; i++) {
					sqDst = sqSrc + ccKingDelta[i];
					if (ucpcSquares[sqDst] != 0) {
						continue;
					}
					for (j = 0; j < 2; j++) {
						sqDst = sqSrc + ccKnightDelta[i][j];
						if (!IN_BOARD(sqDst)) {
							continue;
						}
						pcDst = ucpcSquares[sqDst];
						if ((pcDst & pcSelfSide) == 0) {
							mvs[nGenMoves] = MOVE(sqSrc, sqDst);
							nGenMoves++;
						}
					}
				}
				break;
			case Constant.PIECE_ROOK:
				for (i = 0; i < 4; i++) {
					nDelta = ccKingDelta[i];
					sqDst = sqSrc + nDelta;
					while (IN_BOARD(sqDst)) {
						pcDst = ucpcSquares[sqDst];
						if (pcDst == 0) {
							mvs[nGenMoves] = MOVE(sqSrc, sqDst);
							nGenMoves++;
						} else {
							if ((pcDst & pcOppSide) != 0) {
								mvs[nGenMoves] = MOVE(sqSrc, sqDst);
								nGenMoves++;
							}
							break;
						}
						sqDst += nDelta;
					}
				}
				break;
			case Constant.PIECE_CANNON:
				for (i = 0; i < 4; i++) {
					nDelta = ccKingDelta[i];
					sqDst = sqSrc + nDelta;
					while (IN_BOARD(sqDst)) {
						pcDst = ucpcSquares[sqDst];
						if (pcDst == 0) {
							mvs[nGenMoves] = MOVE(sqSrc, sqDst);
							nGenMoves++;
						} else {
							break;
						}
						sqDst += nDelta;
					}
					sqDst += nDelta;
					while (IN_BOARD(sqDst)) {
						pcDst = ucpcSquares[sqDst];
						if (pcDst != 0) {
							if ((pcDst & pcOppSide) != 0) {
								mvs[nGenMoves] = MOVE(sqSrc, sqDst);
								nGenMoves++;
							}
							break;
						}
						sqDst += nDelta;
					}
				}
				break;
			case Constant.PIECE_PAWN:
				sqDst = SQUARE_FORWARD(sqSrc, sdPlayer);// 格子水平镜像
				if (IN_BOARD(sqDst)) {// 判断棋子是否在棋盘中
					pcDst = ucpcSquares[sqDst];
					if ((pcDst & pcSelfSide) == 0) {
						mvs[nGenMoves] = MOVE(sqSrc, sqDst);
						nGenMoves++;
					}
				}
				if (AWAY_HALF(sqSrc, sdPlayer)) {
					for (nDelta = -1; nDelta <= 1; nDelta += 2) {
						sqDst = sqSrc + nDelta;
						if (IN_BOARD(sqDst)) {
							pcDst = ucpcSquares[sqDst];
							if ((pcDst & pcSelfSide) == 0) {
								mvs[nGenMoves] = MOVE(sqSrc, sqDst);
								nGenMoves++;
							}
						}
					}
				}
				break;
			}
		}
		return nGenMoves;
	}

	/**
	 * 撤消走一步棋
	 * 
	 * @param mv
	 * @param pcCaptured
	 */
	public static void UndoMakeMove(int mv, int pcCaptured) {
		nDistance--;
		ChangeSide();// 交换走子
		UndoMovePiece(mv, pcCaptured);// 撤销走子
	}

	/**
	 * 走一步棋，该方法先走一步棋，如果走棋后被将军了，则撤销该走棋步骤，并返回false，表示将军，此步不能走，撤销走法
	 * 
	 * @param mv
	 *            走棋步骤
	 * @param pcCaptured
	 *            原来目标格子上的棋子
	 * @return
	 */
	public static boolean MakeMove(int mv, int pcCaptured) {
		pcCaptured = MovePiece(mv);
		if (Checked()) {
			UndoMovePiece(mv, pcCaptured);
			return false;
		}
		ChangeSide();
		nDistance++;
		return true;
	}

	/**
	 * 搬一步棋的棋子
	 * 
	 * @param mv
	 *            走棋步骤
	 * @return 返回原来目标格子上的棋子
	 */
	public static int MovePiece(int mv) {
		int sqSrc, sqDst, pc, pcCaptured;

		sqSrc = SRC(mv); // 得到起始位置的数组下标
		sqDst = DST(mv); // 得到目标位置的数组下标

		pcCaptured = ucpcSquares[sqDst]; // 得到目的格子的棋子

		if (pcCaptured != 0) {// 目的地不为空
			DelPiece(sqDst, pcCaptured);// 删掉目标格子棋子
		}
		pc = ucpcSquares[sqSrc];// 得到初始格子上的棋子
		DelPiece(sqSrc, pc);// 删掉初始格子上的棋子
		AddPiece(sqDst, pc);// 在目标格子上放上棋子
		return pcCaptured;// 返回原来目标格子上的棋子
	}

	/**
	 * 判断是否被将军
	 * 
	 * @return
	 */
	public static boolean Checked() {
		int i, j, sqSrc, sqDst;
		int pcSelfSide, pcOppSide, pcDst, nDelta;

		pcSelfSide = Chess_LoadUtil.SIDE_TAG(sdPlayer);// 获得红黑标记(红子是8，黑子是16)
		pcOppSide = Chess_LoadUtil.OPP_SIDE_TAG(sdPlayer);// 获得红黑标记，对方的

		// 找到棋盘上的帅(将)，再做以下判断：

		for (sqSrc = 0; sqSrc < 256; sqSrc++) {
			if (ucpcSquares[sqSrc] != pcSelfSide + PIECE_KING) {
				continue;
			}

			// 1. 判断是否被对方的兵(卒)将军
			if (ucpcSquares[SQUARE_FORWARD(sqSrc, sdPlayer)] == pcOppSide
					+ PIECE_PAWN) {
				return true;
			}
			for (nDelta = -1; nDelta <= 1; nDelta += 2) {
				if (ucpcSquares[sqSrc + nDelta] == pcOppSide + PIECE_PAWN) {
					return true;
				}
			}

			// 2. 判断是否被对方的马将军(以仕(士)的步长当作马腿)
			for (i = 0; i < 4; i++) {
				if (ucpcSquares[sqSrc + ccAdvisorDelta[i]] != 0) {
					continue;
				}
				for (j = 0; j < 2; j++) {
					int pcDstt = ucpcSquares[sqSrc + ccKnightCheckDelta[i][j]];
					if (pcDstt == pcOppSide + PIECE_KNIGHT) {
						return true;
					}
				}
			}

			// 3. 判断是否被对方的车或炮将军(包括将帅对脸)
			for (i = 0; i < 4; i++) {
				nDelta = ccKingDelta[i];
				sqDst = sqSrc + nDelta;
				while (IN_BOARD(sqDst)) {
					pcDst = ucpcSquares[sqDst];
					if (pcDst != 0) {
						if (pcDst == pcOppSide + PIECE_ROOK
								|| pcDst == pcOppSide + PIECE_KING) {
							return true;
						}
						break;
					}
					sqDst += nDelta;
				}
				sqDst += nDelta;
				while (IN_BOARD(sqDst)) {
					pcDst = ucpcSquares[sqDst];
					if (pcDst != 0) {
						if (pcDst == pcOppSide + PIECE_CANNON) {
							return true;
						}
						break;
					}
					sqDst += nDelta;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * 交换走子方
	 */
	public static void ChangeSide() {
		sdPlayer = 1 - sdPlayer;
	}

	/**
	 * 判断是否被杀，该方法在该局面下生成所有的走法，每一个走法走完后，判断是否被将军，
	 * 如果所有的走法下都被将军，说明该方已经被将死，即被杀。只要有一个走法下没有被将军， 则说明还没有被将死，即没有被杀死。
	 * 
	 * @return
	 */
	public static boolean IsMate() {
		int i, nGenMoveNum, pcCaptured;
		Integer[] mvs = new Integer[Constant.MAX_GEN_MOVES];

		nGenMoveNum = GenerateMoves(mvs);
		for (i = 0; i < nGenMoveNum; i++) {
			pcCaptured = MovePiece(mvs[i]);
			if (!Checked()) {
				UndoMovePiece(mvs[i], pcCaptured);
				return false;
			} else {
				UndoMovePiece(mvs[i], pcCaptured);
			}
		}
		return true;
	}

}
