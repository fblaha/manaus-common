package cz.fb.manaus.rest;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class IntervalParser {

    public static final String INTERVAL = "{interval:\\d+[hmd](?:-\\d+)?}";


    public static ImmutableMap<Character, ChronoUnit> UNITS = ImmutableMap.of(
            'h', ChronoUnit.HOURS,
            'm', ChronoUnit.MINUTES,
            'd', ChronoUnit.DAYS);

    Range<Instant> parse(Instant date, String interval) {
        List<String> split = Splitter.on('-').splitToList(interval);
        interval = split.get(0);

        int count = Integer.parseInt(CharMatcher.DIGIT.retainFrom(interval));
        char unitChar = CharMatcher.DIGIT.removeFrom(interval).charAt(0);
        ChronoUnit unit = UNITS.get(unitChar);

        if (split.size() == 2) {
            int offsetDays = Integer.parseInt(split.get(1));
            date = date.minus(offsetDays, unit);
        }

        return Range.closed(date.minus(count, unit), date);
    }


}
