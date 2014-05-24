package us.corenetwork.challenges;

import org.bukkit.ChatColor;

import java.util.HashMap;

public enum ChallengeState
{
	SUBMITTED(0, Setting.COLOR_SUBMITTED),
	DONE(1, Setting.COLOR_ACCEPTED),
	REJECTED(2, Setting.COLOR_REJECTED),
	REJECTED_MESSAGE_SENT(3, Setting.COLOR_REJECTED),
	NOT_SUBMITTED(-1, Setting.COLOR_NOT_SUBMITTED),
	UNDONE(4, Setting.COLOR_UNDONE),
	UNDONE_MESSAGE_SENT(5, Setting.COLOR_UNDONE);

	private final int code;
	private final Setting color;
	private static HashMap<Integer, ChallengeState> byCode = new HashMap<Integer, ChallengeState>();

	static {
		for (ChallengeState state : values()) {
			byCode.put(state.code(), state);
		}
	}

	ChallengeState(int code, Setting color)
	{
		this.code = code;
		this.color = color;
	}

	public int code()
	{
		return code;
	}

	public String getPrint() {
		return color() + name();
	}

	public ChatColor color()
	{
		return ChatColor.getByChar(Settings.getString(color).charAt(1));
	}

	public static ChallengeState getByCode(int code)
	{
		return byCode.get(code);
	}
}
