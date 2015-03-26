package us.corenetwork.challenges;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeekUtil {
	private static final List<String> weekDays = new ArrayList<String>() {{
		add("Mon");
		add("Tue");
		add("Wed");
		add("Thu");
		add("Fri");
		add("Sat");
		add("Sun");
	}};

	public static long customTimeOffset = 0;
	public static int getCurrentWeek()
	{
		return Settings.getInt(Setting.CURRENT_WEEK);
	}
	
	public static DateTime getWeekStart(int id)
	{
		DateTime currentWeek = new DateTime().withMillis(Settings.getLong(Setting.CURRENT_WEEK_START) * 1000);
        int weeksToAdd = id - getCurrentWeek();
        return currentWeek.plus(Period.weeks(weeksToAdd));
	}
	
	public static DateTime getCurrentTime()
	{
		return DateTime.now();
	}

	public static long getCurrentWeekCalculatedStart()
	{
		String weekStart = Settings.getString(Setting.FIRST_WEEK_START);
		String splt[] = weekStart.split(",");

		int weekDay = weekDays.indexOf(splt[0]) + 1;
		String timeSplt[] = splt[1].trim().split(":");
		int hour = Integer.valueOf(timeSplt[0]);
		int minute = Integer.valueOf(timeSplt[1]);
		DateTime oneWeekBefore = DateTime.now().withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(0).withMillisOfSecond(0).minusWeeks(1);
		while (oneWeekBefore.getDayOfWeek() != weekDay)
		{
			oneWeekBefore = oneWeekBefore.plusDays(1);
		}
		return oneWeekBefore.getMillis() / 1000;
	}
}
