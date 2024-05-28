package josebailon.ensayos.cliente.model.database.converter;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Tipos de conversiones de fecha para Room
 *
 * @author Jose Javier Bailon Ortiz
 */
public class Converters {

    /**
     * Formato ISO
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Conversion desde un timestamp
     * @param value El timestamp como long
     * @return La fecha como Date
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * Convierte un date a timestamp
     * @param date El objeto Date
     * @return El timestamp como Long
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    /**
     * Convierte un Date a string usando el formato ISO
     * @param date La fecha
     * @return La fecha como string
     */
    @TypeConverter
    public static String dateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return date != null ? dateFormat.format(date) : null;
    }
}
