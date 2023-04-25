import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

class MainTest {
    private static InputStream stdin;

    @BeforeEach
    void setUp() {
        stdin = System.in;
    }

    @AfterAll
    static void resetSystemIn() {
        System.setIn(stdin);
    }

    @Test
    void mainTest_validInput() {
        String inputData = "https://www.neromylos.com;1;english\n";
        System.setIn(new ByteArrayInputStream(inputData.getBytes()));
        Main.main(new String[0]);
    }

}