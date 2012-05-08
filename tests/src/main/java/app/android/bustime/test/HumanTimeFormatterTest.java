package app.android.bustime.test;


import android.test.AndroidTestCase;
import app.android.bustime.local.Time;
import app.android.bustime.ui.HumanTimeFormatter;

public class HumanTimeFormatterTest extends AndroidTestCase
{
	private HumanTimeFormatter timeFormatter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		timeFormatter = new HumanTimeFormatter(getContext().getApplicationContext());
	}

	public void testHourSingularForm() {
		assertEquals("1 hour", timeFormatter.toHumanFormat(new Time("01:00")));
	}

	public void testHourPluralForm() {
		assertEquals("8 hours", timeFormatter.toHumanFormat(new Time("08:00")));
		assertEquals("11 hours", timeFormatter.toHumanFormat(new Time("11:00")));
		assertEquals("21 hours", timeFormatter.toHumanFormat(new Time("21:00")));
	}

	public void testMinuteSingularForm() {
		assertEquals("1 minute", timeFormatter.toHumanFormat(new Time("00:01")));
	}

	public void testMinutesPluralForm() {
		assertEquals("8 minutes", timeFormatter.toHumanFormat(new Time("00:08")));
		assertEquals("11 minutes", timeFormatter.toHumanFormat(new Time("00:11")));
		assertEquals("21 minutes", timeFormatter.toHumanFormat(new Time("00:21")));
	}

	public void testHoursAndMinutes() {
		assertEquals("8 hours 8 minutes", timeFormatter.toHumanFormat(new Time("08:08")));
		assertEquals("11 hours 11 minutes", timeFormatter.toHumanFormat(new Time("11:11")));
		assertEquals("21 hours 21 minutes", timeFormatter.toHumanFormat(new Time("21:21")));
	}
}
