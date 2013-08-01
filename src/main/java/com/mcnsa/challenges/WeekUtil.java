package com.mcnsa.challenges;

public class WeekUtil {
	public static final int SECONDS_PER_WEEK = 604800;
	public static final int FIRST_WEEK = 1356303600;
	
	public static int customTimeOffset = 0;

	public static int getCurrentWeek()
	{
		return Settings.getInt(Setting.CURRENT_WEEK);
	}
	
	public static long getWeekStart(int id)
	{
		return Settings.getLong(Setting.CURRENT_WEEK_START) + (id - getCurrentWeek()) * SECONDS_PER_WEEK;
	}
	
	public static long getCurrentTime()
	{
		return System.currentTimeMillis() / 1000 - customTimeOffset;
	}

	public static long getCurrentWeekCalculatedStart()
	{
		return getCurrentTime() - (Math.abs(getCurrentTime() - FIRST_WEEK) % SECONDS_PER_WEEK) - SECONDS_PER_WEEK + Settings.getInt(Setting.SWITCH_TIME_OFFSET);
	}
}
