package com.matejdro.bukkit.mcsna.challenges;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePrint {
	public static String formatSekunde(long cas)
	{
		if (cas > 90) return formatMinute(Math.round(cas / 60.0));
		
		String enota = "seconds";
		if (cas % 60 == 1) enota = "second";

		if (cas > 60)
			return String.valueOf(formatDan(Math.round(cas / 60.0)) + ", " + cas % 60 + " " + enota); 
		return String.valueOf(cas + " " + enota);
	}
	
	private static String formatMinute(long cas)
	{
		if (cas > 90) return formatUre(Math.round(cas / 60.0));
	
		String enota = "minutes";
		if (cas % 60 == 1) enota = "minute";
		
		if (cas > 60)
			return String.valueOf(formatDan(Math.round(cas / 60.0)) + ", " + cas % 60 + " " + enota); 
		return String.valueOf(cas + " " + enota);
	}
	
	private static String formatUre(long cas)
	{
		if (cas > 36) return formatDan(Math.round(cas / 24.0));
		
		
		String enota = "hours";
		if (cas % 24 == 1) enota = "hour";

		if (cas > 24)
			return String.valueOf(formatDan(Math.round(cas / 24.0)) + ", " + cas % 24 + " " + enota); 
		return String.valueOf(cas + " " + enota);
	}
	
	private static String formatDan(long cas)
	{
		String enota = "days";
		if (cas == 1) enota = "day";

		return String.valueOf(cas + " " + enota);
	}
	
	
	public static String formatDate(long cas)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(cas * 1000);
		int dan = c.get(Calendar.DAY_OF_MONTH);
		
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
		
		return dan + getDayOfMonthSuffix(dan) + " " + monthFormat.format(new Date(cas * 1000));
		
	}
	
	private static String getDayOfMonthSuffix(final int n) {
	    if (n >= 11 && n <= 13) {
	        return "th";
	    }
	    switch (n % 10) {
	        case 1:  return "st";
	        case 2:  return "nd";
	        case 3:  return "rd";
	        default: return "th";
	    }
	}
}
