package com.example.testfx.chart;

import org.jfree.chart.axis.NumberAxis;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitaires partagés par tous les graphiques.
 * - format français (1 234 567 au lieu de 1.0E6)
 * - axe forcé à 0 (pas de valeurs négatives)
 */
public final class ChartUtils {

    private static final DecimalFormat FORMAT_FR;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        FORMAT_FR = new DecimalFormat("#,##0.###", symbols);
        FORMAT_FR.setGroupingUsed(true);
    }

    private ChartUtils() {}

    /** Format FR + borne inférieure à 0. Pour les graphes où les données sont toujours positives. */
    public static void formaterAxeNumerique(NumberAxis axis) {
        if (axis == null) return;
        axis.setNumberFormatOverride(FORMAT_FR);
        axis.setAutoRangeIncludesZero(true);
        axis.setLowerMargin(0.0);
    }

    /** Format FR seulement (pas de min 0). Pour le scatter où on veut zoomer. */
    public static void formaterAxeNumeriqueSansForcerZero(NumberAxis axis) {
        if (axis == null) return;
        axis.setNumberFormatOverride(FORMAT_FR);
    }

    /** Pour formater une valeur isolée (ex: label de la moyenne). */
    public static String formater(double valeur) {
        return FORMAT_FR.format(valeur);
    }

    public static NumberFormat getFormatFr() {
        return FORMAT_FR;
    }
}