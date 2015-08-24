package com.gzc.zgxq.game;

import static com.gzc.zgxq.game.Chess_LoadUtil.*;
import static com.gzc.zgxq.game.Constant.*;

import java.util.Arrays;

import android.util.Log;

import com.gzc.zgxq.view.ViewConstant;

/**
 * �߷��������࣬ ����ͨ�������С������alpha-beta��������������������alpha�ضϣ�beta�ضϵ��㷨����ͬ������������岽�衣
 * 
 * @author gzc
 * 
 */
public class GameLogic {

	/**
	 * �ֵ�˭�ߣ�0 = �췽��1 = �ڷ�
	 */
	public static int sdPlayer = 0;
	/**
	 * �����ϵ����ӣ���������
	 */
	public static int ucpcSquares[] = new int[256];
	/**
	 * �졢��˫����������ֵ
	 */
	public static int vlWhite, vlBlack;
	/**
	 * ������ڵ�Ĳ���
	 */
	public static int nDistance;
	/**
	 * �����ߵ���
	 */
	public static int mvResult;
	/**
	 * ��ʷ��
	 */
	public static int nHistoryTable[] = new int[65536];

	/** ��ʼ����ʷ�� */
	public static void InitHistorytable() {
		for (int i = 0; i < nHistoryTable.length; i++) {
			nHistoryTable[i] = 0;
		}
	}

	/**
	 * ��ʼ������
	 */
	public static void Startup() {
		int sq, pc;
		sdPlayer = vlWhite = vlBlack = nDistance = 0;
		// ��ʼ��Ϊ��
		for (int i = 0; i < 256; i++) {
			ucpcSquares[i] = 0;
		}
		for (sq = 0; sq < 256; sq++) {
			// ���ݳ�ʼ����λ�����飬�������Ϸ����ӣ�forѭ����������������������Ҳ�Ͱ���ʼλ�ðں���
			pc = Constant.cucpcStartup[sq];
			if (pc != 0) {
				// ���ʼ��Ϸʱ��sq=52(�ڷ���ߵĳ�), cucpcStartup[sq]Ϊ20,pc =
				// cucpcStartup[sq]����ʱpc = 20
				// �ڷ��ӷ֣�cucvlPiecePos[pc -
				// 16][SQUARE_FLIP(sq)]ΪcucvlPiecePos[4][202]=206��Ϊ�ڷ�������ֵ��206�֡�

				// �������Ϸ�һö����
				AddPiece(sq, pc);
			}
		}
	}

	/**
	 * �������Ϸ�һö����
	 * 
	 * @param sq
	 *            λ���±�
	 * @param pc
	 *            �Ŀ�����
	 */
	public static void AddPiece(int sq, int pc) {
		ucpcSquares[sq] = pc;
		// �췽�ӷ֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)����
		// С��16�Ǻ췽���ӣ�����16Ϊ�ڷ����ӣ�0��ʾû����
		if (pc < 16) {
			vlWhite += Constant.cucvlPiecePos[pc - 8][sq];
		} else {
			vlBlack += Constant.cucvlPiecePos[pc - 16][Chess_LoadUtil
					.SQUARE_FLIP(sq)];
		}
	}

	/**
	 * ������������һö���ӣ�������һö����ʱ��Ҫ�����ߵĸ����Ӷ�Ӧ��λ�ӵ�������ֵ�Ӹ÷��ڸþ��µľ����ֵ�ϼ�ȥ
	 * 
	 * @param sq
	 *            λ���±�
	 * @param pc
	 *            �Ŀ�����
	 */
	public static void DelPiece(int sq, int pc) {
		ucpcSquares[sq] = 0;
		// �췽���֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)�ӷ�
		if (pc < 16) {
			vlWhite -= Constant.cucvlPiecePos[pc - 8][sq];
		} else {
			vlBlack -= Constant.cucvlPiecePos[pc - 16][Chess_LoadUtil
					.SQUARE_FLIP(sq)];
		}
		Log.i("DelPiece", "pc = " + pc + ", sq = " + sq);
	}

	/**
	 * ������һ��������ӣ��÷���Ҫ��������Ĳ��裬�Լ������岽���µ�Ŀ��λ���ϵ����ӡ�
	 * �Ƚ������Ӱᵽ��ʼλ�ã�����ò����µ�Ŀ��λ���ϵ����Ӳ�Ϊ�գ����� �ǳ����߷�ʱ�� �ѱ��Ե����������°ᵽ��λ�ã��Դ���ʵ�ֳ�����һ����Ĺ��ܡ�
	 * 
	 * @param mv
	 *            ����Ĳ���
	 * @param pcCaptured
	 *            �����岽���µ�Ŀ��λ���ϵ�����
	 */
	public static void UndoMovePiece(int mv, int pcCaptured) {
		int sqSrc, sqDst, pc;
		sqSrc = Chess_LoadUtil.SRC(mv); // �õ���ʼλ�õ������±�
		sqDst = Chess_LoadUtil.DST(mv); // �õ�Ŀ��λ�õ������±�
		pc = ucpcSquares[sqDst]; // �õ�Ŀ�ĸ��ӵ�����
		DelPiece(sqDst, pc); // ɾ��Ŀ����ӵ�����
		AddPiece(sqSrc, pc); // ����ʼ�����Ϸ�����
		if (pcCaptured != 0) { // ���Ŀ����������������
			AddPiece(sqDst, pcCaptured); // �������ӷ���Ŀ�������
		}
	}

	/**
	 * �ж��߷��Ƿ�������������߷����ж�ÿ���߷��Ƿ����ֻ�Ѻ�����߷���������
	 * 
	 * @param mv
	 * @return
	 */
	public static boolean LegalMove(int mv) {
		int sqSrc, sqDst, sqPin;
		int pcSelfSide, pcSrc, pcDst, nDelta;
		// �ж��߷��Ƿ�Ϸ�����Ҫ�������µ��жϹ��̣�

		// 1. �ж���ʼ���Ƿ����Լ�������
		sqSrc = Chess_LoadUtil.SRC(mv);
		pcSrc = ucpcSquares[sqSrc];
		pcSelfSide = Chess_LoadUtil.SIDE_TAG(sdPlayer); // ��ú�ڱ��(������8��������16)
		if ((pcSrc & pcSelfSide) == 0) {
			return false;
		}

		// 2. �ж�Ŀ����Ƿ����Լ�������
		sqDst = Chess_LoadUtil.DST(mv);
		pcDst = ucpcSquares[sqDst];
		if ((pcDst & pcSelfSide) != 0) {
			return false;
		}

		// 3. �������ӵ����ͼ���߷��Ƿ����
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
	 * ���������������̣��÷�����0�����������Ƚ���������
	 */
	public static void SearchMain() {
		int i, vl;
		i = vl = 0;

		// ��ʼ��
		InitHistorytable();
		nDistance = 0; // ��ʼ����
		long start = System.nanoTime();

		// �����������
		for (i = 1; i <= Constant.LIMIT_DEPTH; i++) {
			// ����
			vl = SearchFull(-Constant.MATE_VALUE, Constant.MATE_VALUE, i);
			// ������ɱ�壬����ֹ����
			if (vl > Constant.WIN_VALUE || vl < -Constant.WIN_VALUE) {
				break;
			}
			// ����ʱ�䣬����ֹ����
			if ((System.nanoTime() - start) / 1.e9 > ViewConstant.thinkDeeplyTime) {
				break;
			}
		}
	}

	final static String LOG_SearchFull = "SearchFull";

	/**
	 * �����߽�(Fail-Soft)��Alpha-Beta�������̣�����Ҫ�ķ�����
	 * ͨ���ݹ���ã����������߷���Ȼ��ó�ÿ���߷��µľ����ֵ���������ֵ�Ĵ�С������������岽�衣
	 * 
	 * @param vlAlpha
	 * @param vlBeta
	 * @param nDepth
	 * @return
	 */
	public static int SearchFull(int vlAlpha, int vlBeta, int nDepth) {
		int i = 0, nGenMoves = 0, pcCaptured = 0;
		int vl, vlBest, mvBest;
		// ÿһ�����ɵ������߷�
		Integer moves[] = new Integer[Constant.MAX_GEN_MOVES];

		// һ��Alpha-Beta��ȫ������Ϊ����6���׶�:

		// 1. ����ˮƽ�ߣ��򷵻ؾ�������ֵ
		if (0 == nDepth) {
			return Evaluate();
		}

		// 2. ��ʼ�����ֵ������߷�
		vlBest = -Constant.MATE_VALUE; // ��������֪�����Ƿ�һ���߷���û�߹�(ɱ��)
		mvBest = 0; // ��������֪�����Ƿ���������Beta�߷���PV�߷����Ա㱣�浽��ʷ��

		// 3. ����ȫ���߷�����������ʷ������
		nGenMoves = GenerateMoves(moves);
		Arrays.sort(moves, 0, nGenMoves, new MyComparator()); // ����ȫ������ֻ�Բ������߷���������������ֵ����
		for (int i2 = 0; i2 < moves.length; i2++) {
			Log.i("�����߷������", "��" + (i2 + 1) + "���߷�mvs[" + i2 + "] = " + moves[i2]);
		}

		// 4. ��һ����Щ�߷��������еݹ�
		for (i = 0; i < nGenMoves; i++) {
			pcCaptured = ucpcSquares[Chess_LoadUtil.DST(moves[i])];
			Log.i(LOG_SearchFull, "��" + (i + 1) + "���߷�mvs[" + i + "] = "
					+ moves[i] + ", pcCaptured=" + pcCaptured);

			if (MakeMove(moves[i], pcCaptured)) {
				// �ݹ�
				vl = -SearchFull(-vlBeta, -vlAlpha, nDepth - 1);
				UndoMakeMove(moves[i], pcCaptured);

				// 5. ����Alpha-Beta��С�жϺͽض�
				if (vl > vlBest) { // �ҵ����ֵ(������ȷ����Alpha��PV����Beta�߷�)
					vlBest = vl; // "vlBest"����ĿǰҪ���ص����ֵ�����ܳ���Alpha-Beta�߽�
					if (vl >= vlBeta) { // �ҵ�һ��Beta�߷�
						mvBest = moves[i]; // Beta�߷�Ҫ���浽��ʷ��
						break; // Beta�ض�
					}
					if (vl > vlAlpha) { // �ҵ�һ��PV�߷�
						mvBest = moves[i]; // PV�߷�Ҫ���浽��ʷ��
						vlAlpha = vl; // ��СAlpha-Beta�߽�
					}
				}
			}
		}

		// 6. �����߷����������ˣ�������߷�(������Alpha�߷�)���浽��ʷ���������ֵ
		if (vlBest == -Constant.MATE_VALUE) {
			// �����ɱ�壬�͸���ɱ�岽����������
			return nDistance - Constant.MATE_VALUE;
		}
		if (mvBest != 0) {
			// �������Alpha�߷����ͽ�����߷����浽��ʷ��
			nHistoryTable[mvBest] += nDepth * nDepth;
			if (nDistance == 0) {
				// �������ڵ�ʱ��������һ������߷�(��Ϊȫ�����������ᳬ���߽�)��������߷���������
				mvResult = mvBest;
			}
		}
		Log.i("SearchFull( ) over", "vlBest = " + vlBest);
		return vlBest;
	}

	final static String LOG_Evaluate = "��������";

	/**
	 * �������ۺ���
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
	 * ���������߷����÷������ݸþ������������и÷������岽��
	 * 
	 * @param mvs
	 * @return
	 */
	public static int GenerateMoves(Integer[] mvs) {
		int i, j, nGenMoves, nDelta, sqSrc, sqDst;
		int pcSelfSide, pcOppSide, pcSrc, pcDst;

		// ���������߷�����Ҫ��������2�����裺

		nGenMoves = 0;
		pcSelfSide = Chess_LoadUtil.SIDE_TAG(sdPlayer);
		pcOppSide = Chess_LoadUtil.OPP_SIDE_TAG(sdPlayer);

		for (sqSrc = 0; sqSrc < 256; sqSrc++) {
			// 1. �ҵ�һ���������ӣ����������жϣ�
			pcSrc = ucpcSquares[sqSrc];
			if ((pcSrc & pcSelfSide) == 0) {
				continue;
			}

			// 2. ��������ȷ���߷�
			switch (pcSrc - pcSelfSide) {
			// ��˧
			case Constant.PIECE_KING:
				for (i = 0; i < 4; i++) {
					sqDst = sqSrc + Constant.ccKingDelta[i];
					if (!Chess_LoadUtil.IN_FORT(sqDst)) {
						continue;
					}
					pcDst = ucpcSquares[sqDst];
					if ((pcDst & pcSelfSide) == 0) {
						mvs[nGenMoves] = Chess_LoadUtil.MOVE(sqSrc, sqDst);// ���������յ����߷�
						Log.i("GenerateMoves", "mvs[" + nGenMoves + "] = "
								+ mvs[nGenMoves]);
						nGenMoves++;
					}
				}
				break;
			// ʿ
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
				sqDst = SQUARE_FORWARD(sqSrc, sdPlayer);// ����ˮƽ����
				if (IN_BOARD(sqDst)) {// �ж������Ƿ���������
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
	 * ������һ����
	 * 
	 * @param mv
	 * @param pcCaptured
	 */
	public static void UndoMakeMove(int mv, int pcCaptured) {
		nDistance--;
		ChangeSide();// ��������
		UndoMovePiece(mv, pcCaptured);// ��������
	}

	/**
	 * ��һ���壬�÷�������һ���壬�������󱻽����ˣ����������岽�裬������false����ʾ�������˲������ߣ������߷�
	 * 
	 * @param mv
	 *            ���岽��
	 * @param pcCaptured
	 *            ԭ��Ŀ������ϵ�����
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
	 * ��һ���������
	 * 
	 * @param mv
	 *            ���岽��
	 * @return ����ԭ��Ŀ������ϵ�����
	 */
	public static int MovePiece(int mv) {
		int sqSrc, sqDst, pc, pcCaptured;

		sqSrc = SRC(mv); // �õ���ʼλ�õ������±�
		sqDst = DST(mv); // �õ�Ŀ��λ�õ������±�

		pcCaptured = ucpcSquares[sqDst]; // �õ�Ŀ�ĸ��ӵ�����

		if (pcCaptured != 0) {// Ŀ�ĵز�Ϊ��
			DelPiece(sqDst, pcCaptured);// ɾ��Ŀ���������
		}
		pc = ucpcSquares[sqSrc];// �õ���ʼ�����ϵ�����
		DelPiece(sqSrc, pc);// ɾ����ʼ�����ϵ�����
		AddPiece(sqDst, pc);// ��Ŀ������Ϸ�������
		return pcCaptured;// ����ԭ��Ŀ������ϵ�����
	}

	/**
	 * �ж��Ƿ񱻽���
	 * 
	 * @return
	 */
	public static boolean Checked() {
		int i, j, sqSrc, sqDst;
		int pcSelfSide, pcOppSide, pcDst, nDelta;

		pcSelfSide = Chess_LoadUtil.SIDE_TAG(sdPlayer);// ��ú�ڱ��(������8��������16)
		pcOppSide = Chess_LoadUtil.OPP_SIDE_TAG(sdPlayer);// ��ú�ڱ�ǣ��Է���

		// �ҵ������ϵ�˧(��)�����������жϣ�

		for (sqSrc = 0; sqSrc < 256; sqSrc++) {
			if (ucpcSquares[sqSrc] != pcSelfSide + PIECE_KING) {
				continue;
			}

			// 1. �ж��Ƿ񱻶Է��ı�(��)����
			if (ucpcSquares[SQUARE_FORWARD(sqSrc, sdPlayer)] == pcOppSide
					+ PIECE_PAWN) {
				return true;
			}
			for (nDelta = -1; nDelta <= 1; nDelta += 2) {
				if (ucpcSquares[sqSrc + nDelta] == pcOppSide + PIECE_PAWN) {
					return true;
				}
			}

			// 2. �ж��Ƿ񱻶Է�������(����(ʿ)�Ĳ�����������)
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

			// 3. �ж��Ƿ񱻶Է��ĳ����ڽ���(������˧����)
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
	 * �������ӷ�
	 */
	public static void ChangeSide() {
		sdPlayer = 1 - sdPlayer;
	}

	/**
	 * �ж��Ƿ�ɱ���÷����ڸþ������������е��߷���ÿһ���߷�������ж��Ƿ񱻽�����
	 * ������е��߷��¶���������˵���÷��Ѿ�������������ɱ��ֻҪ��һ���߷���û�б������� ��˵����û�б���������û�б�ɱ����
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
