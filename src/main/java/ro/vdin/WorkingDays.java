package ro.vdin;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class WorkingDays {
    private enum FormatMode {
        RO_AGENDA,
        EN_AGENDA,
        YYYY,
    }

    private enum WorkingDayMode {
        WORKING_DAYS,
        ALL_DAYS
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int year = LocalDate.now().getYear();
//        int year = Integer.parseInt(args[0]);
//        if (year < 2000) {
//            throw new IllegalArgumentException("Year must be at least 2000");
//        }

        Month month = getMonth(args[0]);

        FormatMode formatMode = FormatMode.valueOf(args[1]);
        WorkingDayMode workingDayMode = WorkingDayMode.valueOf(args[2]);

        var wd = new WorkingDays();

        wd.printDays(year, month, formatMode, workingDayMode);
    }

    private static Month getMonth(String arg) {
        if (arg.equalsIgnoreCase("CURRENT")) {
            return LocalDate.now().getMonth();
        }

        return Month.valueOf(arg);
    }

    private static void usage() {
        System.out.printf("Usage example: working-days <MONTH> <%s> <%s>\n",
                          List.of(FormatMode.values()).stream().map(x -> x.name()).collect(Collectors.joining("|")),
                          List.of(WorkingDayMode.values()).stream().map(x -> x.name()).collect(Collectors.joining("|"))
        );
        System.exit(1);
    }

    private void printDays(int year, Month month, FormatMode mode, WorkingDayMode workingDayMode) {
        for (int day = 1; day <= daysInMonth(year, month); day++) {
            LocalDate ld = LocalDate.of(year, month, day);

            if (workingDayMode == WorkingDayMode.WORKING_DAYS && isWeekend(ld)) {
                continue;
            }

            switch (mode) {
                case RO_AGENDA:
                    System.out.printf("%s, %d %s %s\n",
                                      dayOfWeekToRomanian(ld.getDayOfWeek()),
                                      ld.getDayOfMonth(),
                                      monthToRomanian(ld.getMonth()),
                                      ld.getYear());
                    System.out.println();
                    break;
                case EN_AGENDA:
                    System.out.printf("%s, %d.%s.%s\n",
                                      ld.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                                      ld.getDayOfMonth(),
                                      ld.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                                      ld.getYear());
                    System.out.println();
                    break;
                case YYYY:
                    System.out.printf("%d-%02d-%02d\n", ld.getYear(), ld.getMonth().getValue(), ld.getDayOfMonth());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported mode " + mode);
            }
        }
    }

    private String monthToRomanian(Month month) {
        switch (month) {
            case JANUARY:
                return "Ian";
            case FEBRUARY:
                return "Feb";
            case MARCH:
                return "Mar";
            case APRIL:
                return "Apr";
            case MAY:
                return "Mai";
            case JUNE:
                return "Iun";
            case JULY:
                return "Iul";
            case AUGUST:
                return "Aug";
            case SEPTEMBER:
                return "Sep";
            case OCTOBER:
                return "Oct";
            case NOVEMBER:
                return "Noi";
            case DECEMBER:
                return "Dec";
        }

        throw new IllegalArgumentException();
    }

    private String poormanCamelCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String dayOfWeekToRomanian(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "Luni";
            case TUESDAY:
                return "Marți";
            case WEDNESDAY:
                return "Miercuri";
            case THURSDAY:
                return "Joi";
            case FRIDAY:
                return "Vineri";
            case SATURDAY:
                return "Sâmbătă";
            case SUNDAY:
                return "Duminică";
            default:
                throw new IllegalArgumentException("Unsupported value " + dayOfWeek);
        }
    }

    private int daysInMonth(int year, Month month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    private boolean isWeekend(LocalDate ld) {
        switch (ld.getDayOfWeek()) {
            case SATURDAY:
            case SUNDAY:
                return true;
            default:
                return false;
        }
    }
}
