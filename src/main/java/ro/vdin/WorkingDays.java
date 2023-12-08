package ro.vdin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private record Params(int year, Month month, FormatMode formatMode, WorkingDayMode workingDayMode) {
    }

    public static void main(String[] args) {
        try {
            Params params = readCommandLine(args);
            var wd = new WorkingDays();

            wd.printDays(params.year, params.month, params.formatMode, params.workingDayMode);
        } catch (RuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof ParseException) {
                // do nothing
            } else {
                throw e;
            }
        }

    }

    private static Params readCommandLine(String[] args) {
        Options options = new Options();

        options.addOption(new Option("y", "year", true, "year"));
        options.addOption(new Option("m", "month", true, "month - ex: DECEMBER"));
        options.addOption(new Option("f",
                                     "format-mode",
                                     true,
                                     "format mode - " + Stream.of(FormatMode.values())
                                             .map(Enum::name)
                                             .collect(Collectors.joining("|"))));
        options.addOption(new Option("w",
                                     "working-day-mode",
                                     true,
                                     Stream.of(WorkingDayMode.values())
                                             .map(Enum::name)
                                             .collect(Collectors.joining("|"))));

        CommandLine cmd;

        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);
            throw new RuntimeException(e);
        }

        String yearStr = cmd.getOptionValue("y");
        int year;
        if (yearStr != null) {
            year = Integer.parseInt(yearStr);
            if (year < 2000) {
                throw new IllegalArgumentException("Year must be at least 2000");
            }
        } else {
            year = LocalDate.now().getYear();
        }

        Month month;
        if (cmd.getOptionValue("m") != null) {
            month = getMonth(cmd.getOptionValue("m"));
        } else {
            month = LocalDate.now().getMonth();
        }

        FormatMode formatMode =
                cmd.getOptionValue("f") != null ? FormatMode.valueOf(cmd.getOptionValue("f")) : FormatMode.YYYY;
        WorkingDayMode workingDayMode =
                cmd.getOptionValue("w") != null ? WorkingDayMode.valueOf(cmd.getOptionValue("w")) :
                        WorkingDayMode.ALL_DAYS;

        return new Params(year, month, formatMode, workingDayMode);
    }

    private static Month getMonth(String arg) {
        if (arg.equalsIgnoreCase("CURRENT")) {
            return LocalDate.now().getMonth();
        }

        return Month.valueOf(arg);
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
        return switch (month) {
            case JANUARY -> "Ian";
            case FEBRUARY -> "Feb";
            case MARCH -> "Mar";
            case APRIL -> "Apr";
            case MAY -> "Mai";
            case JUNE -> "Iun";
            case JULY -> "Iul";
            case AUGUST -> "Aug";
            case SEPTEMBER -> "Sep";
            case OCTOBER -> "Oct";
            case NOVEMBER -> "Noi";
            case DECEMBER -> "Dec";
        };

    }

//    private String poormanCamelCase(String str) {
//        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
//    }

    private String dayOfWeekToRomanian(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Luni";
            case TUESDAY -> "Marți";
            case WEDNESDAY -> "Miercuri";
            case THURSDAY -> "Joi";
            case FRIDAY -> "Vineri";
            case SATURDAY -> "Sâmbătă";
            case SUNDAY -> "Duminică";
        };
    }

    private int daysInMonth(int year, Month month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    private boolean isWeekend(LocalDate ld) {
        return switch (ld.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
    }
}
