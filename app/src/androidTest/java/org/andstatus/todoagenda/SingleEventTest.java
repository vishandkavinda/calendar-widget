package org.andstatus.todoagenda;

import org.andstatus.todoagenda.calendar.CalendarEvent;
import org.andstatus.todoagenda.util.DateUtil;
import org.andstatus.todoagenda.widget.CalendarEntry;
import org.andstatus.todoagenda.widget.WidgetEntry;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author yvolk@yurivolkov.com
 */
public class SingleEventTest extends BaseWidgetTest {

    private int eventId = 0;

    public void testEventAttributes() {
        DateTime today = DateUtil.now(provider.getSettings().getTimeZone()).withTimeAtStartOfDay();
        DateUtil.setNow(today.plusHours(10));
        CalendarEvent event = new CalendarEvent(provider.getContext(), provider.getWidgetId(),
                provider.getSettings().getTimeZone(), false);
        event.setEventId(++eventId);
        event.setTitle("Single Event today with all known attributes");
        event.setStartDate(today.plusHours(12));
        event.setEndDate(today.plusHours(13));
        event.setColor(0xFF92E1C0);
        event.setLocation("somewhere");
        event.setAlarmActive(true);
        event.setRecurring(true);

        assertOneEvent(event, true);
        event.setAlarmActive(false);
        assertOneEvent(event, true);
        event.setRecurring(false);
        assertOneEvent(event, true);
    }

    public void testAlldayEventAttributes() {
        DateTime today = DateUtil.now(provider.getSettings().getTimeZone()).withTimeAtStartOfDay();
        DateUtil.setNow(today.plusHours(10));
        CalendarEvent event = new CalendarEvent(provider.getContext(), provider.getWidgetId(),
                provider.getSettings().getTimeZone(), true);
        event.setEventId(++eventId);
        event.setTitle("Single AllDay event today with all known attributes");
        event.setStartDate(today.minusDays(1));
        event.setEndDate(today.plusDays(1));
        event.setColor(0xFF92E1C0);
        event.setLocation("somewhere");
        assertOneEvent(event, false);
        event.setStartDate(today);
        event.setEndDate(today.plusDays(1));
        assertOneEvent(event, true);
    }


    public void testAlldayEventMillis() {
        DateTime today = DateUtil.now(DateTimeZone.UTC).withTimeAtStartOfDay();
        CalendarEvent event = new CalendarEvent(provider.getContext(), provider.getWidgetId(),
                provider.getSettings().getTimeZone(), true);
        event.setEventId(++eventId);
        event.setTitle("Single All day event from millis");
        event.setStartMillis(today.getMillis());
        assertEquals(event.getStartDate().toString(), today.getMillis(), event.getStartMillis());
        assertEquals(event.getEndDate().toString(), today.plusDays(1).getMillis(), event.getEndMillis());
    }

    private void assertOneEvent(CalendarEvent event, boolean equal) {
        provider.clear();
        provider.addRow(event);
        factory.onDataSetChanged();
        factory.logWidgetEntries(TAG);
        assertEquals(1, provider.getQueriesCount());
        assertEquals(factory.getWidgetEntries().toString(), 2, factory.getWidgetEntries().size());
        WidgetEntry entry = factory.getWidgetEntries().get(1);
        assertTrue(entry instanceof CalendarEntry);
        CalendarEvent eventOut = ((CalendarEntry) entry).getEvent();
        if (equal) {
            assertEquals(event.toString(), eventOut.toString());
        } else {
            assertNotSame(event.toString(), eventOut.toString());
        }
    }
}
