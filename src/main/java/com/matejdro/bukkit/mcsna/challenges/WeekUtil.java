package com.matejdro.bukkit.mcsna.challenges;

public class WeekUtil {
	public static final int SECONDS_PER_WEEK = 604800;
	//private static final int FIRST_2013_WEEK = 1356980400;
	public static final int FIRST_WEEK = 1356303600;
	
	public static int customTimeOffset = 0;

	public static int getCurrentWeek()
	{
		//Prvi teden v januarju - 1356980400
		//1 teden: 604800

		long ticksDiff = getCurrentTime() - FIRST_WEEK - Settings.getInt(Setting.SWITCH_TIME_OFFSET);
		
		return (int) Math.floor((double) ticksDiff / SECONDS_PER_WEEK);
	}
	
	public static long getWeekStart(int id)
	{
		long date = FIRST_WEEK + Settings.getInt(Setting.SWITCH_TIME_OFFSET);
		date += id * SECONDS_PER_WEEK;
		return date;
	}
	
	public static long getCurrentTime()
	{
		return System.currentTimeMillis() / 1000 - customTimeOffset;
	}

}
