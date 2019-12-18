package vn.com.call.db.cache;

import io.paperdb.Paper;
import io.paperdb.PaperDbException;

/**
 * Created by ngson on 06/07/2017.
 */

public class ColorCache {
    public static final String BOOK_COLOR_CACHE = "color_contacts";

    public static int getColorFromNumber(String number, int defaultColor) {
        try {
            int color = Paper.book(BOOK_COLOR_CACHE).read(number, 0);

            if (color == 0) Paper.book(BOOK_COLOR_CACHE).write(number, defaultColor);

            return color == 0 ? defaultColor : color;
        } catch (PaperDbException e) {
            e.printStackTrace();

            return defaultColor;
        }
    }
}
