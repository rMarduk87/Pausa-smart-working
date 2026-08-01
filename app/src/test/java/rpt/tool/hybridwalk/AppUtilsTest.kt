package rpt.tool.hybridwalk

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import rpt.tool.hybridwalk.utils.AppUtils

class AppUtilsTest {

    @Test
    fun getCurrentDate_returns_formattedDate() {
        val date = AppUtils.getCurrentDate()
        assertNotNull(date)
        // Check format YYYY-MM-DD
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        assertTrue("Date format should be YYYY-MM-DD but was $date", regex.matches(date))
    }
}
