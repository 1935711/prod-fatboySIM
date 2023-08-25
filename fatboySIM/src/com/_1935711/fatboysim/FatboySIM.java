package com._1935711.fatboysim;

import javacard.framework.*;
import sim.toolkit.*;

public class FatboySIM extends Applet
		implements ToolkitInterface, ToolkitConstants {
	/*
	 * Sysmocom-SJA2 flash memory has 500,000 write cycles, so keep these
	 * variables to a bare minimum.
	 */

	/* Plus 1 for error item. Total is 12. */
	static private final byte menuItemCount = 11;
	static private final byte menuItemLength = 15;

	static private final byte[] eventList = new byte[] {
			/*
			 * These return ToolkitException.EVENT_NOT_ALLOWED:
			 * - EVENT_MENU_SELECTION,
			 * - EVENT_MENU_SELECTION_HELP_REQUEST,
			 * - EVENT_TIMER_EXPIRATION,
			 * - EVENT_STATUS_COMMAND,
			 */

			/*
			 * These return ToolkitException.TAR_NOT_DEFINED:
			 * - EVENT_FORMATTED_SMS_PP_ENV,
			 * - EVENT_FORMATTED_SMS_PP_UPD,
			 * - EVENT_FORMATTED_SMS_CB,
			 */

			EVENT_PROFILE_DOWNLOAD,
			EVENT_UNFORMATTED_SMS_PP_ENV,
			EVENT_UNFORMATTED_SMS_PP_UPD,
			EVENT_UNFORMATTED_SMS_CB,
			EVENT_CALL_CONTROL_BY_SIM,
			EVENT_MO_SHORT_MESSAGE_CONTROL_BY_SIM,
			EVENT_EVENT_DOWNLOAD_MT_CALL,
			EVENT_EVENT_DOWNLOAD_CALL_CONNECTED,
			EVENT_EVENT_DOWNLOAD_CALL_DISCONNECTED,
			EVENT_EVENT_DOWNLOAD_LOCATION_STATUS,
			EVENT_EVENT_DOWNLOAD_USER_ACTIVITY,
			EVENT_EVENT_DOWNLOAD_IDLE_SCREEN_AVAILABLE,
			EVENT_EVENT_DOWNLOAD_CARD_READER_STATUS,
			EVENT_EVENT_DOWNLOAD_LANGUAGE_SELECTION,
			EVENT_EVENT_DOWNLOAD_BROWSER_TERMINATION,
			EVENT_EVENT_DOWNLOAD_DATA_AVAILABLE,
			EVENT_EVENT_DOWNLOAD_CHANNEL_STATUS,
			EVENT_FIRST_COMMAND_AFTER_SELECT,
	};

	/*
	 * Critical failures are those that prevent the applet from displaying an
	 * error on the menu.
	 */
	private boolean failureCritical = false;
	private boolean failure = false;

	private byte menuItemIdError;
	private byte[] menuItemId = new byte[menuItemCount];
	private byte[] textRender;
	private byte[] textError = new byte[] {
			// ------------------4----5----6----7----8----9----10---11---12
			'E', 'R', 'R', ' ', '0', '0', '0', '0', '0', '0', '0', '0', '0'
	};

	private boolean timerInitialized = false;
	private byte timerId;
	static private byte[] timerHourMinuteSecond = new byte[] {
			(byte) 0, (byte) 0, (byte) 1
	};

	private short[] frame;
	private boolean[] demoStartRunning;

	private FatboySIM() {
		/*
		 * This is the interface to the STK applet registry (which is separate
		 * from the JavaCard applet registry).
		 */
		ToolkitRegistry toolkitRegistry = ToolkitRegistry.getEntry();

		try {
			textRender = JCSystem.makeTransientByteArray(
					(short) (menuItemCount
							* menuItemLength),
					(byte) 1);
		} catch (Exception exception) {
			textError[5] = '1';
			failure = true;
		}

		if (!failure && !failureCritical) {
			try {
				for (byte item_index = 0; item_index < menuItemCount
						- 1; ++item_index) {
					/* Define the applet menu entries. */
					menuItemId[item_index] = toolkitRegistry.initMenuEntry(
							textRender,
							(short) (item_index * menuItemLength),
							(short) menuItemLength,
							PRO_CMD_SELECT_ITEM, false, (byte) 0, (short) 0);
				}
				draw((short) 0);
				menuRefresh();
			} catch (Exception exception) {
				textError[6] = '1';
				failure = true;
			}
		}

		if (!failure && !failureCritical) {
			try {
				/*
				 * If this works, it will register to the EVENT_STATUS_COMMAND
				 * event.
				 */
				toolkitRegistry.requestPollInterval((short) 1);
			} catch (Exception exception) {
				textError[7] = '1';
				// Non-essential so not setting 'failure=true'.
			}
		}

		if (!failure && !failureCritical) {
			try {
				frame = JCSystem.makeTransientShortArray((short) 1, (byte) 1);
				demoStartRunning = JCSystem.makeTransientBooleanArray((short) 1,
						(byte) 1);
			} catch (Exception exception) {
				textError[8] = '1';
				failure = true;
			}
		}

		short eventRegisterIndex = 0;
		if (!failure && !failureCritical) {
			for (; eventRegisterIndex < eventList.length; ++eventRegisterIndex) {
				try {
					toolkitRegistry.setEvent(eventList[eventRegisterIndex]);
				} catch (Exception exception) {
					textError[12] = (byte) ('1' + eventRegisterIndex);
				}
			}
		}

		// Should still be fine when there was a failure.
		if (!failureCritical) {
			try {
				menuItemIdError = toolkitRegistry.initMenuEntry(
						textError,
						(short) 0,
						(short) textError.length,
						PRO_CMD_SELECT_ITEM, false, (byte) 0, (short) 0);
			} catch (Exception e) {
				textError[4] = '1';
				failureCritical = true;
			}
		}
	}

	/*
	 * JCRE calls `install` to create ann instance of the Java Card applet and
	 * give it control. This is called when the applet is being installed.
	 */
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		FatboySIM applet = new FatboySIM();
		applet.register();
	}

	/*
	 * This processes APDUs sent directly to the applet. For STK applets, this
	 * interface isn't really used.
	 */
	public void process(APDU arg0) throws ISOException {
		/* Ignore the applet select command dispached to the process. */
		if (selectingApplet())
			return;
	}

	/* This processes STK events. */
	public void processToolkit(byte event) throws ToolkitException {
		ProactiveHandler proactiveHandler = ProactiveHandler
				.getTheHandler();

		if (!timerInitialized && !failure && !failureCritical) {
			ToolkitRegistry toolkitRegistry = ToolkitRegistry.getEntry();
			try {
				/*
				 * If it succeeds,it will implicitly register to the
				 * EVENT_TIMER_EXPIRATION event.
				 */
				timerId = toolkitRegistry.allocateTimer();
				timerInitialized = true;
			} catch (Exception exception) {
				textError[9] = '1';
				// Non-essential so not setting 'failure=true'.
			}

			if (timerInitialized) {
				try {
					timerStart();
				} catch (Exception exception1) {
					textError[10] = '1';
					ToolkitRegistry.getEntry().releaseTimer(timerId);
				}
			}
		}

		if (failureCritical) {
			proactiveHandler.initDisplayText((byte) 0, DCS_8_BIT_DATA,
					textError,
					(short) 0,
					(short) textError.length);
			proactiveHandler.send();
		} else {
			if (event == EVENT_MENU_SELECTION) {
				EnvelopeHandler eventHandler = EnvelopeHandler.getTheHandler();

				final byte selectedItemId = eventHandler
						.getItemIdentifier();

				if (selectedItemId == menuItemIdError) {
					proactiveHandler.initDisplayText((byte) 0,
							DCS_8_BIT_DATA,
							textError,
							(short) 0,
							(short) textError.length);
					proactiveHandler.send();
				}
			}

			if (!failure) {
				if (event == EVENT_MENU_SELECTION) {
					demoStartRunning[0] = true;
				}
				try {
					/*
					 * 0x01 One dial tone, longest.
					 * 0x02 Two dial tones, medium long.
					 * 0x03 Six dial tones, short.
					 * 0x04 One dial tone, short.
					 * 0x05 Three dial tones, short.
					 * 0x06 Two emergency tones, short.
					 * 0x07 Two dial tones, short with longer pause between.
					 * 0x08 One dial tone, long.
					 */
					if ( /* When full intro title is shown. */ frame[0] == effectIntroStart
							+ ((effectIntroEnd - effectIntroStart) / 2)
							|| /* When "IS RUNNING JAVA". */ frame[0] == effectQuestionStart
									+ (5 * 2)
							|| /* When outro text is shown. */ frame[0] == effectOutroStart) {
						tonePlay((byte) 0x06);
					} else if (/* At start of each effect. */ frame[0] == effectSpiralStart
							|| frame[0] == effectPongStart
							|| frame[0] == effectQuestionStart
							|| frame[0] == effectSlimakStart
							|| frame[0] == effectChessStart) {
						tonePlay((byte) 0x04);
					} else if (/* First pong bounce. */ frame[0] == effectPongStart
							+ 12
							|| /* Second pong bounce. */ frame[0] == effectPongStart
									+ 12 + 12) {
						tonePlay((byte) 0x10);
					} else if (/* When spiral reverses. */ frame[0] == effectSpiralStart
							+ ((effectSpiralEnd
									- effectSpiralStart) / 2)) {
						tonePlay((byte) 0x05);
					} else if (/* When "3bn devices... sure" is shown. */ frame[0] == effectQuestionStart
							+ (5 * 4) + 1) {
						tonePlay((byte) 0x03);
					} else if (/* When slimak completes fill-in. */ frame[0] == effectSlimakStart
							+ ((effectSlimakEnd
									- effectSlimakStart) / 2)) {
						tonePlay((byte) 0x07);
					} else if (/* When chess is half-way done. */ frame[0] == effectChessStart
							+ ((effectChessEnd
									- effectChessStart) / 2)) {
						tonePlay((byte) 0x02);
					}
				} catch (Exception exception) {
					textError[11] = '1';
					/* Non-critical so continuing. */
				}
				if (demoStartRunning[0]) {
					frame[0] = (short) (frame[0] + 1);
				}
				draw(frame[0]);
				menuRefresh();
			}
		}
	}

	private void menuRefresh() throws ToolkitException {
		if (!failure && !failureCritical) {

			ToolkitRegistry toolkitRegistry = ToolkitRegistry.getEntry();

			for (byte item_index = 0; item_index < menuItemCount
					- 1; ++item_index) {
				toolkitRegistry.changeMenuEntry(
						menuItemId[item_index],
						textRender,
						(short) (item_index * menuItemLength),
						(short) menuItemLength,
						PRO_CMD_SELECT_ITEM, false, (byte) 0, (short) 0);
			}
		}
	}

	private void timerStart() throws NullPointerException,
			ArrayIndexOutOfBoundsException, ToolkitException {
		ProactiveHandler command = ProactiveHandler.getTheHandler();

		command.init(PRO_CMD_TIMER_MANAGEMENT, (byte) 0, DEV_ID_ME);
		command.appendTLV((byte) (TAG_TIMER_IDENTIFIER | TAG_SET_CR),
				timerId);
		command.appendTLV((byte) (TAG_TIMER_VALUE | TAG_SET_CR),
				timerHourMinuteSecond, (short) 0,
				(short) timerHourMinuteSecond.length);
		command.send();
	}

	private void tonePlay(byte tone)
			throws ToolkitException {
		ProactiveHandler command = ProactiveHandler.getTheHandler();

		command.init(PRO_CMD_PLAY_TONE, (byte) 0, DEV_ID_ME);
		command.appendTLV((byte) (TAG_TONE | TAG_SET_CR), tone);
		command.send();
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

	private void draw(short frame) {
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

	private void drawIntro(short frameRelative) {
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

	private void drawSpiral(short frameRelative) {
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

	private void drawPong(short frameRelative) {
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

	private void drawQuestion(short frameRelative) {
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

	private void drawSlimak(short frameRelative) {
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

	private void drawChess(short frameRelative) {
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

	private void drawOutro(short frameRelative) {
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
