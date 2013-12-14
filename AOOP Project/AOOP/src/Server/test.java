package Server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Mon on 12/14/13.
 */
public class test {

	private static int year;
	private static int month_no;
	private static int day_no;
	private static int date;
	private static int hour;
	private static int minute;
	private static final String[] day = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	private static final String[] month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	private static String currentDate;

	public static void main (String[] args) {
		year = Calendar.getInstance().get(Calendar.YEAR);
		month_no = Calendar.getInstance().get(Calendar.MONTH);
		date = Calendar.getInstance().get(Calendar.DATE);
		day_no = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		currentDate = day[day_no] + ", " + month[month_no] + ' ' + date + ", " + year;

		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println( sdf.format(cal.getTime()) );
		System.out.println(currentDate);
	}
}
