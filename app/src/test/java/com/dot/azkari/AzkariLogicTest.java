package com.dot.azkari;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.buzzbox.mob.android.scheduler.cron.Predictor;
import com.dot.arabic.ArabicUtilities;
import com.dot.constants.ApplicationConstants;
import org.junit.Test;

public class AzkariLogicTest {

    @Test
    public void testWrapTwoDigits() {
        assertEquals("00", ApplicationConstants.WrapTwoDigits(0));
        assertEquals("05", ApplicationConstants.WrapTwoDigits(5));
        assertEquals("12", ApplicationConstants.WrapTwoDigits(12));
    }

    @Test
    public void testArabicUtilitiesReshape() {
        String input = "سبحان الله";
        String reshaped = ArabicUtilities.reshape(input);
        assertNotNull(reshaped);
        assertTrue(reshaped.length() > 0);
    }

    @Test
    public void testCronPredictor() throws Exception {
        String cron = "*/5 * * * *";
        Predictor predictor = new Predictor(cron, 1000000000000L);
        long next = predictor.nextMatchingTime();
        assertTrue(next > 1000000000000L);
        assertEquals(0, next % 60000L);
    }

    @Test
    public void testDailyAzkarCronPrediction() throws Exception {
        // Morning reminder at 07:00
        String morningCron = "0 7 * * *";
        Predictor morningPredictor = new Predictor(morningCron, 1700000000000L);
        long nextMorning = morningPredictor.nextMatchingTime();
        assertTrue(nextMorning > 1700000000000L);
        assertEquals(0, nextMorning % 60000L);

        // Evening reminder at 18:30
        String eveningCron = "30 18 * * *";
        Predictor eveningPredictor = new Predictor(eveningCron, 1700000000000L);
        long nextEvening = eveningPredictor.nextMatchingTime();
        assertTrue(nextEvening > 1700000000000L);
        assertEquals(0, nextEvening % 60000L);
    }

    @Test
    public void testTimeFormatConversions() {
        assertEquals("9,30", Utils.convertTo24Hour("09:30 AM"));
        assertEquals("21,30", Utils.convertTo24Hour("09:30 PM"));
        assertEquals("0,0", Utils.convertTo24Hour("12:00 AM"));
        assertEquals("12,0", Utils.convertTo24Hour("12:00 PM"));

        assertEquals("02:05 PM", Utils.convertTo12Hour(14, 5));
        assertEquals("12:00 AM", Utils.convertTo12Hour(0, 0));
        assertEquals("12:30 PM", Utils.convertTo12Hour(12, 30));
    }

    @Test
    public void testAlarmTimeToCronConversion() {
        // Test parsing "12:00 am" -> 00:00 -> cron "0 0 * * *"
        String[] dateAm = Utils.convertTo24Hour("12:00 am").split(",");
        String cronAm = Integer.parseInt(dateAm[1].trim()) + " " + Integer.parseInt(dateAm[0].trim()) + " * * *";
        assertEquals("0 0 * * *", cronAm);

        // Test parsing "05:15 pm" -> 17:15 -> cron "15 17 * * *"
        String[] datePm = Utils.convertTo24Hour("05:15 pm").split(",");
        String cronPm = Integer.parseInt(datePm[1].trim()) + " " + Integer.parseInt(datePm[0].trim()) + " * * *";
        assertEquals("15 17 * * *", cronPm);
    }

    @Test
    public void testFontSizeValueArrayParsing() {
        String[] fontSizeValues = new String[]{"17", "20", "22"};
        int foundIndex = -1;
        int targetValue = 20;
        for (int i = 0; i < fontSizeValues.length; i++) {
            if (fontSizeValues[i] != null && Integer.parseInt(fontSizeValues[i].trim()) == targetValue) {
                foundIndex = i;
                break;
            }
        }
        assertEquals(1, foundIndex);

        // Test safe fallback for non-matching or null values
        String[] brokenValues = new String[]{"17", null, "invalid"};
        int safeIndex = 0;
        int searchVal = 99;
        for (int i = 0; i < brokenValues.length; i++) {
            if (brokenValues[i] != null) {
                try {
                    if (Integer.parseInt(brokenValues[i].trim()) == searchVal) {
                        safeIndex = i;
                        break;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        assertEquals(0, safeIndex);
    }
}
