public class App {
	private static final byte menuItemCount = 11;
	private static final byte menuItemLength = 15;
	private static boolean failureCritical = false;
	private static boolean failure = false;
	private static byte[] textRender = new byte[menuItemCount * menuItemLength];

	public static char byteToAlphabetGsm(byte character) {
		final String alphabetGsm = "@\u00A3$\u00A5\u00E8\u00E9\u00F9\u00EC\u00F2\u00C7\n\u00D8\u00F8\r\u00C5\u00E5\u0394_\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039E \u00C6\u00E6\u00DF\u00C9 !\"#\u00A4%&'()*+,-./0123456789:;<=>?\u00A1ABCDEFGHIJKLMNOPQRSTUVWXYZ\u00C4\u00D6\u00D1\u00DC\u00A7\u00BFabcdefghijklmnopqrstuvwxyz\u00E4\u00F6\u00F1\u00FC\u00E0";
		return alphabetGsm.charAt(character);
	}

	public static void main(String[] args) {
		short frame = 0;
		while (true) {
			System.out.print("\nFrame: " + frame + '\n');
			draw(frame++);
			for (byte row = 0; row < menuItemCount; ++row) {
				for (byte column = 0; column < menuItemLength; ++column) {
					System.out.print(
							byteToAlphabetGsm(textRender[(row * menuItemLength)
									+ column]));
				}
				System.out.print('\n');
			}
			try {
				Thread.sleep(100);
			} catch (Exception exception) {
			}
		}
	}

	/*************************************************************************/

	private static short minShort(short a, short b) {
		return a <= b ? a : b;
	}

	private static short textXYToIndex(short row, short column) {
		return (short) ((short) (row * menuItemLength) + column);
	}

	static final byte[] textTitle = new byte[] {
			'f', 'a', 't', 'b', 'o', 'y', 'S', 'I', 'M',
	};
	static final byte[] textOutroLine0 = new byte[] {
			'X', 'E', 'N', 'I', 'U', 'M', 0x11, '2', '0', '2', '3',
	};
	static final byte[] textOutroLine1 = new byte[] {
			'W', 'I', 'L', 'D', 0x11, 0x11, 0x11,
			'S', 'I', 'M', 0x11, 'C', 'A', 'R', 'D',
	};
	static final byte[] textOutroLine2 = new byte[] {
			'1', '9', '3', '5', '7', '1', '1',
	};
	static final byte[] textQuestion0 = new byte[] {
			'W', 'a', 'i', 't', ',', 'h', 'o', 'l', 'd', ' ', 'o', 'n', '.',
			'.', '.',
	};
	static final byte[] textQuestion1 = new byte[] {
			'T', 'h', 'i', 's', ' ', 'd', 'a', 'r', 'n', ' ', 't', 'h', 'i',
			'n', 'g',
	};
	static final byte[] textQuestion2 = new byte[] {
			'I', 'S', ' ', 'R', 'U', 'N', 'N', 'I', 'N', 'G', ' ', 'J', 'A',
			'V', 'A',
	};
	static final byte[] textQuestion3 = new byte[] {
			'3', 'b', 'n', ' ', 'd', 'e', 'v', 's', '.', '.', '.', 's', 'u',
			'r', 'e',
	};

	static final short effectIntroStart = 0;
	static final short effectIntroEnd = 20;

	static final short effectSpiralStart = effectIntroEnd + 1;
	static final short effectSpiralEnd = effectSpiralStart + 24;

	static final short effectPongStart = effectSpiralEnd + 1;
	static final short effectPongEnd = effectPongStart + 29;

	static final short effectQuestionStart = effectPongEnd + 1;
	static final short effectQuestionEnd = effectQuestionStart + 30;

	static final short effectSlimakStart = effectQuestionEnd + 1;
	static final short effectSlimakEnd = effectSlimakStart + 20 + 20;

	static final short effectChessStart = effectSlimakEnd + 1;
	static final short effectChessEnd = effectChessStart + 5;

	static final short effectOutroStart = effectChessEnd + 1;

	static final byte spiralFrameCount = 13;
	static final byte[] spiralLineCoordinates = new byte[] {
			0x70, 0x71, 0x72, 0x73, 0x74, 0x75, /* Frame 1 */
			0x60, 0x61, 0x62, 0x63, 0x74, 0x75, /* Frame 2 */
			0x50, 0x51, 0x52, 0x63, 0x74, 0x75, /* Frame 3 */
			0x40, 0x41, 0x52, 0x53, 0x64, 0x75, /* Frame 4 */
			0x30, 0x31, 0x42, 0x53, 0x64, 0x75, /* Frame 5 */
			0x20, 0x31, 0x42, 0x53, 0x64, 0x75, /* Frame 6 */
			0x10, 0x21, 0x32, 0x43, 0x53, 0x64, 0x75, /* Frame 7 */
			0x00, 0x11, 0x22, 0x33, 0x43, 0x54, 0x64, 0x75, /* Frame 8 */
			0x01, 0x12, 0x22, 0x33, 0x43, 0x54, 0x64, 0x75, /* Frame 9 */
			0x02, 0x13, 0x23, 0x33, 0x44, 0x54, 0x64, 0x75, /* Frame 10 */
			0x03, 0x13, 0x23, 0x34, 0x44, 0x54, 0x65, 0x75, /* Frame 11 */
			0x04, 0x14, 0x24, 0x34, 0x45, 0x55, 0x65, 0x75, /* Frame 12 */
			0x05, 0x15, 0x25, 0x35, 0x45, 0x55, 0x65, /* Frame 13 */
	};

	static final byte[] slimakCurve = new byte[] {
			(byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x01, (byte) 0x40,
			(byte) 0x80, (byte) 0x00, (byte) 0x10, (byte) 0x10, (byte) 0x00,
			(byte) 0x81, (byte) 0x00, (byte) 0x04, (byte) 0x01, (byte) 0x84,
			(byte) 0x00, (byte) 0x11, (byte) 0x90, (byte) 0x40, (byte) 0xC1,
			(byte) 0x00,
	};

	private static byte spiralLineCoordinatesRow(byte coordinate) {
		return (byte) (coordinate & (byte) 0x0F);
	}

	private static byte spiralLineCoordinatesColumn(byte coordinate) {
		return (byte) ((coordinate & (byte) 0xF0) >> 4);
	}

	private static void draw(short frame) {
		if (!failure && !failureCritical) {
			if (frame >= effectIntroStart && frame <= effectIntroEnd) {
				drawIntro((short) (frame - effectIntroStart));
			} else if (frame >= effectSpiralStart && frame <= effectSpiralEnd) {
				drawSpiral((short) (frame - effectSpiralStart));
			} else if (frame >= effectPongStart && frame <= effectPongEnd) {
				drawPong((short) (frame - effectPongStart));
			} else if (frame >= effectQuestionStart
					&& frame <= effectQuestionEnd) {
				drawQuestion((short) (frame - effectQuestionStart));
			} else if (frame >= effectSlimakStart && frame <= effectSlimakEnd) {
				drawSlimak((short) (frame - effectSlimakStart));
			} else if (frame >= effectChessStart && frame <= effectChessEnd) {
				drawChess((short) (frame - effectChessStart));
			} else {
				drawOutro((short) (frame - effectOutroStart));
			}
		}
	}

	private static void drawIntro(short frameRelative) {
		for (byte row = 0; row < menuItemCount; ++row) {
			for (byte column = 0; column < menuItemLength; ++column) {
				textRender[textXYToIndex(row, column)] = 0x11;
			}
		}
		for (byte row = 0; row < menuItemCount; ++row) {
			textRender[textXYToIndex(row,
					(short) ((short) (row + frameRelative)
							% menuItemLength))] = 0x10;
		}

		/* When we start showing the text. */
		final byte textShowFrameStart = 0;
		final byte textShowFrameEnd = (byte) (textShowFrameStart
				+ textTitle.length);
		/* When we start hiding the text again. */
		final byte textHideFrameStart = textShowFrameStart + 12;
		final byte textHideFrameEnd = (byte) (textHideFrameStart
				+ textTitle.length);

		if (frameRelative >= textShowFrameStart) {
			byte textStartColumn = 3;
			byte textShowLength = 0;
			byte textHideLength = 0;
			if (frameRelative >= textShowFrameStart) {
				textShowLength = (byte) (minShort(frameRelative,
						textShowFrameEnd)
						- textShowFrameStart);
			}
			if (frameRelative >= textHideFrameStart) {
				textHideLength = (byte) (minShort(frameRelative,
						textHideFrameEnd)
						- textHideFrameStart);
			}

			final byte textLength = (byte) (textShowLength
					- textHideLength);
			final byte textOffset = textHideLength;
			for (byte textTitleIndex = textOffset; textTitleIndex < textLength; ++textTitleIndex) {
				textRender[textXYToIndex((short) 4,
						(short) ((short) textStartColumn
								+ textTitleIndex))] = textTitle[textTitleIndex
										+ textOffset];
			}
		}
	}

	private static void drawSpiral(short frameRelative) {
		final byte characterBackground = 0x11;
		final byte characterForeground = 0x2B;

		final boolean effectSpiralForward = (short) (frameRelative
				/ 12)
				% 2 == 0;
		final boolean effectSpiralBackward = !effectSpiralForward;

		final byte animationFrameIndex = (byte) (frameRelative
				% spiralFrameCount);

		for (byte row = 0; row < menuItemCount; ++row) {
			for (byte column = 0; column < menuItemLength; ++column) {
				textRender[textXYToIndex(row, column)] = characterBackground;
			}
		}

		byte animationFrameCount = 0;
		byte animationOppositeFrameCount = 0;
		boolean animationDraw = false;
		boolean animationOppositeDraw = false;
		for (byte lookupTableIndexForward = 0; lookupTableIndexForward < spiralLineCoordinates.length; ++lookupTableIndexForward) {
			final byte lookupTableIndexBackward = (byte) ((spiralLineCoordinates.length
					- 1) - lookupTableIndexForward);
			final byte lookupTableIndex = effectSpiralForward
					? lookupTableIndexForward
					: lookupTableIndexBackward;
			final byte lookupTableIndexOpposite = effectSpiralBackward
					? lookupTableIndexForward
					: lookupTableIndexBackward;

			final byte coordinate = spiralLineCoordinates[lookupTableIndex];
			final byte coordinateOpposite = spiralLineCoordinates[lookupTableIndexOpposite];

			final byte row = spiralLineCoordinatesRow(coordinate);
			final byte column = spiralLineCoordinatesColumn(coordinate);

			final byte rowMirrored = (byte) ((menuItemCount - 1)
					- row);
			final byte columnMirrored = (byte) ((menuItemLength - 1)
					- column);

			final byte rowOpposite = spiralLineCoordinatesRow(
					coordinateOpposite);
			final byte columnOpposite = spiralLineCoordinatesColumn(
					coordinateOpposite);

			final byte rowOppositeMirrored = (byte) ((menuItemCount - 1)
					- rowOpposite);
			final byte columnOppositeMirrored = (byte) ((menuItemLength
					- 1) - columnOpposite);

			if (animationFrameCount == animationFrameIndex) {
				animationDraw = true;
			} else {
				animationDraw = false;
			}

			if (animationOppositeFrameCount == animationFrameIndex) {
				animationOppositeDraw = true;
			} else {
				animationOppositeDraw = false;
			}

			if (animationDraw) {
				textRender[textXYToIndex(row, column)] = characterForeground;
				textRender[textXYToIndex(rowMirrored,
						columnMirrored)] = characterForeground;
			}

			if (animationOppositeDraw) {
				textRender[textXYToIndex(rowOpposite,
						columnOppositeMirrored)] = characterForeground;
				textRender[textXYToIndex(rowOppositeMirrored,
						columnOpposite)] = characterForeground;
			}

			if (coordinate == 0x75) {
				animationFrameCount++;
			}

			if (coordinateOpposite == 0x75) {
				animationOppositeFrameCount++;
			}
		}
	}

	private static void drawPong(short frameRelative) {
		final short frameRelativeQuarter = (short) (frameRelative / 1);
		final short frameRelativeHalf = (short) (frameRelative / 1);

		final byte characterBackground = 0x11;

		for (byte row = 0; row < menuItemCount; ++row) {
			for (byte column = 0; column < menuItemLength; ++column) {
				textRender[textXYToIndex(row, column)] = characterBackground;
			}
		}

		final byte paddleLength = (byte) 4;

		final byte paddleOffsetRow = 1;
		final byte paddleOffsetRange = (menuItemCount - paddleLength)
				- paddleOffsetRow;
		final byte paddleOffsetAbsolute = (byte) (frameRelativeQuarter
				% paddleOffsetRange);
		final boolean paddleOffsetIncreasing = (byte) (frameRelativeQuarter
				/ paddleOffsetRange) % 2 == 0;
		final byte paddleOffset = (byte) (paddleOffsetIncreasing
				? paddleOffsetAbsolute
				: paddleOffsetRange - paddleOffsetAbsolute);
		final byte paddleLeftPosition = paddleOffset;
		final byte paddleRightPosition = (byte) ((menuItemCount - paddleLength)
				- paddleOffset);

		final byte ballOffsetRange = menuItemLength - 3;
		final byte ballOffsetAbsolute = (byte) (frameRelativeHalf
				% ballOffsetRange);
		final boolean ballOffsetIncreasing = (byte) (frameRelativeHalf
				/ ballOffsetRange) % 2 == 0;
		final byte ballOffset = (byte) (ballOffsetIncreasing
				? ballOffsetAbsolute
				: ballOffsetRange - ballOffsetAbsolute);
		final byte ballOffsetColumn = ballOffset;

		for (byte row = 0; row < menuItemCount; ++row) {
			if (row >= paddleLeftPosition + paddleOffsetRow
					&& row < paddleLeftPosition + paddleLength) {
				textRender[textXYToIndex(row,
						(short) 0)] = 0x18;
			}
			if (row >= paddleRightPosition + paddleOffsetRow
					&& row < paddleRightPosition + paddleLength) {
				textRender[textXYToIndex(
						row,
						(short) (menuItemLength - 1))] = 0x18;
			}
			if (row == (byte) (ballOffsetColumn / 2) + 2) {
				textRender[textXYToIndex(
						(short) row,
						(short) (ballOffsetColumn + 1))] = 0x19;
			}
		}
	}

	private static void drawQuestion(short frameRelative) {
		for (byte row = 0; row < menuItemCount; ++row) {
			for (byte column = 0; column < menuItemLength; ++column) {
				textRender[textXYToIndex(row, column)] = 0x20;
			}
		}

		for (byte column = 0; column < menuItemLength; ++column) {
			if (frameRelative < 5) {
				textRender[textXYToIndex((short) 4,
						column)] = textQuestion0[column];
			} else if (frameRelative < 10) {
				textRender[textXYToIndex((short) 4,
						column)] = textQuestion1[column];
			} else if (frameRelative < 15) {
				textRender[textXYToIndex((short) 4,
						column)] = textQuestion2[column];
			} else if (frameRelative < 25 && frameRelative > 20) {
				textRender[textXYToIndex((short) 4,
						column)] = textQuestion3[column];
			}
		}
	}

	private static void drawSlimak(short frameRelative) {
		final byte characterBackground = (byte) (frameRelative > 20 ? 0x03
				: 0x11);
		final byte characterForeground = (byte) (frameRelative > 20 ? 0x11
				: 0x03);

		for (byte row = 0; row < menuItemCount; ++row) {
			for (byte column = 0; column < menuItemLength; ++column) {
				textRender[textXYToIndex(row, column)] = characterBackground;
			}
		}

		final short fillerLength = (short) ((short) (frameRelative * 8)
				% (short) (menuItemCount * menuItemLength));
		short fillerLengthCompleted = 0;
		byte row = 0;
		byte column = 0;
		byte direction = 0;
		for (byte slimakIndex = 0; slimakIndex < (short) ((fillerLength / 8)
				+ (fillerLength % 8 != 0 ? 1 : 0)); ++slimakIndex) {
			short slimakValue = slimakCurve[slimakIndex];
			for (byte bitIndex = 0; bitIndex < 8
					&& fillerLengthCompleted <= fillerLength; ++bitIndex, ++fillerLengthCompleted) {
				textRender[textXYToIndex(row, column)] = characterForeground;
				if ((slimakValue & 0x01) > 0) {
					direction++;
					direction %= 4;
				}
				slimakValue >>= 1;
				switch (direction) {
					case 0:
						column++;
						break;
					case 1:
						row++;
						break;
					case 2:
						column--;
						break;
					case 3:
						row--;
						break;
				}
				if (row < 0 || row > menuItemCount - 1 || column < 0
						|| column > menuItemLength - 1) {
					break;
				}
			}
			if (row < 0 || row > menuItemCount - 1 || column < 0
					|| column > menuItemLength - 1) {
				break;
			}
		}
	}

	private static void drawChess(short frameRelative) {
		for (byte row = 0; row < menuItemCount; ++row) {
			for (byte column = 0; column < menuItemLength; ++column) {
				textRender[textXYToIndex(row,
						column)] = (byte) ((byte) ((frameRelative % 2) + row
								+ column)
								% 2 == 0
										? 0x11
										: 0x16);
			}
		}
	}

	private static void drawOutro(short frameRelative) {
		for (byte row = 0; row < menuItemCount; ++row) {
			for (byte column = 0; column < menuItemLength; ++column) {
				textRender[textXYToIndex(row, column)] = 0x11;
			}
		}
		for (byte row = 0; row < menuItemCount; ++row) {
			textRender[textXYToIndex(row,
					(short) ((short) (row + frameRelative)
							% menuItemLength))] = 0x15;
		}

		final byte offsetLineTitle = (byte) ((byte) (menuItemLength
				- textTitle.length)
				/ 2);
		final byte offsetLine0 = (byte) ((byte) (menuItemLength
				- textOutroLine0.length)
				/ 2);
		final byte offsetLine1 = (byte) ((byte) (menuItemLength
				- textOutroLine1.length)
				/ 2);
		final byte offsetLine2 = (byte) ((byte) (menuItemLength
				- textOutroLine2.length)
				/ 2);
		for (byte column = 0; column < menuItemLength; ++column) {
			if (column >= offsetLineTitle
					&& column < (byte) (textTitle.length + offsetLineTitle)) {
				textRender[textXYToIndex((short) 2,
						(short) (column))] = textTitle[column
								- offsetLineTitle];
			}
			if (column >= offsetLine0
					&& column < (byte) (textOutroLine0.length + offsetLine0)) {
				textRender[textXYToIndex((short) 4,
						(short) (column))] = textOutroLine0[column
								- offsetLine0];
			}
			if (column >= offsetLine1
					&& column < (byte) (textOutroLine1.length + offsetLine1)) {
				textRender[textXYToIndex((short) 5,
						(short) (column))] = textOutroLine1[column
								- offsetLine1];
			}
			if (column >= offsetLine2
					&& column < (byte) (textOutroLine2.length + offsetLine2)) {
				textRender[textXYToIndex((short) 6,
						(short) (column))] = textOutroLine2[column
								- offsetLine2];
			}
		}
	}
}
