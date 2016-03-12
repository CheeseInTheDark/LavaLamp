import org.junit.Before
import org.junit.Test

import static LavaLamp.*

class LavaLampTest
{
    def testOutputBuffer = new ByteArrayOutputStream()
    def testOutputStream = new PrintStream(testOutputBuffer)
    def previousOutputStream = System.out

    @Before
    void setup() {
        testOutputBuffer.reset()
    }

    def captureOutput(closure) {
        System.out = testOutputStream
        closure()
        System.out = previousOutputStream
    }

    def consoleOutput() {
        testOutputBuffer.toString()
    }

    @Test
    void describingSomethingWithNoTestsOutputsSuccess() {
        captureOutput {
            describe("something")
        }

        assert consoleOutput() == "SUCCESS\n"
    }

    @Test
    void aTestPrintsOutItsName() {
        captureOutput {
            describe("the thing") {
                testIt("works")
            }
        }

        assert consoleOutput().startsWith("the thing works")
    }

    @Test
    void anEmptyTestIndicatesThatItPassed() {
        captureOutput {
            describe("the test") {
                testIt("passes")
            }
        }

        assert consoleOutput().contains("the test passes: PASS\n")
    }

    @Test
    void aFailingTestIndicatesThatItFailed() {
        captureOutput {
            describe("the test") {
                testIt("fails") {
                    assert false
                }
            }
        }

        assert consoleOutput().contains("the test fails: FAIL\n")
    }

    @Test
    void aPassingTestIndicatesThatItPassed() {
        captureOutput {
            describe("the test") {
                testIt("passes when it does something") {
                    assert true
                }
            }
        }

        assert consoleOutput().contains("the test passes when it does something: PASS\n")
    }

    @Test
    void perTestSetupMethodsRunBeforeTests() {
        captureOutput {
            describe("every test") {
                def setupHasRun = false

                testIt("runs after the setup") {
                    assert setupHasRun
                }

                beforeEach {
                    setupHasRun = true
                }
            }
        }

        assert consoleOutput().contains("every test runs after the setup: PASS")
    }

    @Test
    void successIsPrintedWhenTestsSucceed() {
        captureOutput {
            describe("test") {
                testIt("passes")
            }
        }

        assert consoleOutput() == "test passes: PASS\n\nSUCCESS\n"
    }

    @Test
    void failureIsPrintedWhenTestsFail() {
        captureOutput {
            describe("test") {
                testIt("fails") {
                    assert false
                }
            }
        }

        assert consoleOutput() == "test fails: FAIL\n\nFAILURE\n"
    }

    @Test
    void failureIsPrintedWhenAnyTestsFail() {
        captureOutput {
            describe("test") {
                testIt("passes")
                testIt("indicates failure") {
                    assert false
                }
            }
        }

        assert consoleOutput().endsWith("FAILURE\n")
    }

    @Test
    void eachTestPrintsItsParentDescription() {
        captureOutput {
            describe("test") {
                testIt("prints its parent description")
                testIt("prints its parent description")
            }
        }

        assert consoleOutput().startsWith("test prints its parent description: PASS\ntest prints its parent description: PASS")
    }

    @Test
    void multipleTestsOutputTheirResults() {
        captureOutput {
            describe("something") {
                testIt("does this") {
                    assert true
                }

                testIt("does not do this") {
                    assert false
                }
            }
        }

        assert consoleOutput().startsWith("something does this: PASS\nsomething does not do this: FAIL")
    }

    @Test
    void multipleTestSetupMethodsRun() {
        captureOutput {
            describe("context with multiple setup methods") {
                def firstSetupRun = false
                def secondSetupRun = false

                beforeEach {
                    firstSetupRun = true
                }

                beforeEach {
                    secondSetupRun = true
                }

                testIt("runs all of them") {
                    assert firstSetupRun
                    assert secondSetupRun
                }
            }
        }

        assert consoleOutput().contains("context with multiple setup methods runs all of them: PASS")
    }

    @Test
    void testTeardownMethodsAlwaysRunAfterTests() {
        def teardownHasRun = false
        def testRanBeforeTeardown = false

        describe("teardown method") {
            afterEach {
                teardownHasRun = true
            }

            testIt("has not run yet") {
                testRanBeforeTeardown = !teardownHasRun
            }
        }

        assert testRanBeforeTeardown
        assert teardownHasRun
    }

    @Test
    void testMultipleTeardownMethodsRun() {
        def firstTeardownHasRun = false
        def secondTeardownHasRun = false

        describe("context with teardown methods") {
            afterEach {
                firstTeardownHasRun = true
            }

            afterEach {
                secondTeardownHasRun = true
            }

            testIt("runs each one") {
                assert true
            }
        }

        assert firstTeardownHasRun
        assert secondTeardownHasRun
    }
}
