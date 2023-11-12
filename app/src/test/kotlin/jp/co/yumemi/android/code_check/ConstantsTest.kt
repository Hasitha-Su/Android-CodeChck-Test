package jp.co.yumemi.android.code_check

import jp.co.yumemi.android.code_check.constants.Constants
import org.junit.Assert.*
import org.junit.Test

/**
 * A test class for validating the constants used in the application.
 *
 * This class contains unit tests to ensure that the values of constants
 * defined in the Constants object are correct and as expected.
 */
class ConstantsTest {

    /**
     * Tests if the base URL constant is correctly defined.
     *
     * This test checks whether the BASE_URL constant in the Constants object
     * matches the expected URL string for the API.
     */
    @Test
    fun `base URL is correct`() {
        assertEquals("https://api.github.com/", Constants.BASE_URL)
    }

    /**
     * Tests if the search path constant is correctly defined.
     *
     * This test ensures that the PATH_SEARCH constant in the Constants object
     * correctly represents the path used for repository search in the API.
     */
    @Test
    fun `search path is correct`() {
        assertEquals("search/repositories", Constants.PATH_SEARCH)
    }
}