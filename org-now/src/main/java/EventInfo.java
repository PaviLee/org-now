import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and stores Event data from Gmail for Google Calendar.
 *
 * @author Pavi Lee
 * @version September 5, 2020
 */
public class EventInfo {

    private static String[] dateRegexes;

    private String eventName;
    private String eventStartDateTime;
    private String eventEndDateTime;

    static {
        dateRegexes = new String[3];
        dateRegexes[0] = "(\\d{1,2})[-/](\\d{1,2})([-/](\\d{2,4}))?( from | " +
                "at | - |-| )(\\d{1,2}:\\d{2}) ?(am|AM|pm|PM)?( to | - |-)" +
                "(\\d{1,2}:\\d{2}) ?(am|AM|pm|PM)?";
        dateRegexes[1] = "(Jan|Feb|Mar|Apr|May|June|July|Aug|Sept|Oct|Nov" +
                "|Dec).? (\\d{1,2})(, | )?(\\d{1,4})?( from | at | - |-| )" +
                "(\\d{1,2}:\\d{2}) ?(am|AM|pm|PM)?( to | - |-)(\\d{1," +
                "2}:\\d{2}) ?(am|AM|pm|PM)?";
        dateRegexes[2] = "(January|February|March|April|May|June|July|August" +
                "|September|October|November|December) (\\d{1,2})(, | )?" +
                "(\\d{1,4})?( from | at | - |-| )(\\d{1,2}:\\d{2}) ?" +
                "(am|AM|pm|PM)?( to | - |-)(\\d{1,2}:\\d{2}) ?(am|AM|pm|PM)?";
    }

    public static List<EventInfo> createEventInfos(String eventName,
                                                   String snippet) {
        List<EventInfo> eventInfoList = new LinkedList<EventInfo>();
        String eventStartDateTime = "";
        String eventEndDateTime = "";

        for (String dateRegex : dateRegexes) {

            Pattern pattern = Pattern.compile(dateRegex);
            Matcher matcher = pattern.matcher(snippet);

            int index = 0;
            while (matcher.find(index)) {
                index = matcher.end() + 1;

                String month = matcher.group(1);
                String day = matcher.group(2);
                String year = matcher.group(4);
                String startTime = matcher.group(6);
                String startAbb = matcher.group(7);
                String endTime = matcher.group(9);
                String endAbb = matcher.group(10);

                if (Character.isAlphabetic(month.charAt(0))) {
                    month = monthToInt(month) + "";
                }
                month = format(month, 2);
                day = format(day, 2);

                if (year == null) {
                    year = "2020";
                } else if (year.length() == 2) {
                    year = "20" + year;
                }

                if (startAbb == null) {
                    startAbb = endAbb;
                } else if (endAbb == null) {
                    endAbb = startAbb;
                }
                if (startAbb.equalsIgnoreCase("pm")) {
                    StringTokenizer st = new StringTokenizer(startTime, ":");
                    int hour = Integer.valueOf(st.nextToken());
                    hour += 12;
                    startTime = hour + ":" + st.nextToken();
                }
                if (endAbb.equalsIgnoreCase("pm")) {
                    StringTokenizer st = new StringTokenizer(endTime, ":");
                    int hour = Integer.valueOf(st.nextToken());
                    hour += 12;
                    endTime = hour + ":" + st.nextToken();
                }
                startTime = format(startTime, 5) + ":00";
                endTime = format(endTime, 5) + ":00";

                StringBuilder sb = new StringBuilder();
                sb.append(year);
                sb.append("-");
                sb.append(month);
                sb.append("-");
                sb.append(day);
                sb.append("T");

                String date = sb.toString();
                String offset = "07:00";
                eventStartDateTime = date + startTime + "-" + offset;
                eventEndDateTime = date + endTime + "-" + offset;

                EventInfo eventInfo = new EventInfo(eventName,
                        eventStartDateTime,
                        eventEndDateTime);
                if (!eventInfoList.contains(eventInfo)) {
                    eventInfoList.add(eventInfo);
                }
            }
        }

        if (eventInfoList.isEmpty()) {
            return null;
        } else {
            return eventInfoList;
        }
    }

    private static int monthToInt(String month) {
        switch (month) {
            case "January":
            case "Jan":
                return 1;
            case "February":
            case "Feb":
                return 2;
            case "March":
            case "Mar":
                return 3;
            case "April":
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
            case "Aug":
                return 8;
            case "September":
            case "Sept":
                return 9;
            case "October":
            case "Oct":
                return 10;
            case "November":
            case "Nov":
                return 11;
            case "December":
            case "Dec":
                return 12;
            default:
                return -1;
        }
    }

    private static String format(String data, int finalLength) {
        for (int i = data.length(); i < finalLength; i++) {
            data = "0" + data;
        }

        return data;
    }


    public EventInfo(String eventName, String eventStartDateTime,
                     String eventEndDateTime) {
        this.eventName = eventName;
        this.eventStartDateTime = eventStartDateTime;
        this.eventEndDateTime = eventEndDateTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventStartDateTime() {
        return eventStartDateTime;
    }

    public String getEventEndDateTime() {
        return eventEndDateTime;
    }

    @Override
    public String toString() {
        StringTokenizer st = new StringTokenizer(eventStartDateTime, "T");
        String simplifiedStartDateTime =
                st.nextToken() + " at " + st.nextToken().substring(0, 8);

        st = new StringTokenizer(eventEndDateTime, "T");
        String simplifiedEndDateTime =
                st.nextToken() + " at " + st.nextToken().substring(0, 8);

        return eventName + ": " + simplifiedStartDateTime + " to " + simplifiedEndDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EventInfo) {
            EventInfo otherEventInfo = (EventInfo) o;
            return eventName.equals(otherEventInfo.eventName) &&
                    eventStartDateTime.equals(otherEventInfo.eventStartDateTime)
                    && eventEndDateTime.equals(otherEventInfo.eventEndDateTime);
        }
        return false;
    }

}
