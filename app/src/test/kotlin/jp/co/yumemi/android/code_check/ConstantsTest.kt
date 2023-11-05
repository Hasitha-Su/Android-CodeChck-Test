package jp.co.yumemi.android.code_check

import jp.co.yumemi.android.code_check.constants.Constants
import org.junit.Assert.*
import org.junit.Test

class ConstantsTest {

    @Test
    fun `base URL is correct`() {
        assertEquals("https://api.github.com/", Constants.BASE_URL)
    }

    @Test
    fun `search path is correct`() {
        assertEquals("search/repositories", Constants.PATH_SEARCH)
    }
}
