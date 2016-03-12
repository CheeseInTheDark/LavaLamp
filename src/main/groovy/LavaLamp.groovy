class LavaLamp
{
    def static describe(description) {
        println "SUCCESS"
    }

    def static describe(description, closure) {
        closure.delegate = new LavaLamp(description: description)
        closure()
        closure.delegate.runTests()
    }

    def private description
    def private setup = []
    def private tests = []
    def private teardown = []

    def beforeEach(closure) {
        setup << closure
    }

    def afterEach(closure) {
        teardown << closure
    }

    def testIt(name) {
        tests << {
            outputTestSuccess(name)
            return true
        }
    }

    def testIt(name, testClosure) {
        tests << {
            try {
                runTest({setup.each { it() }}, testClosure, {teardown.each { it() }})
                outputTestSuccess(name)
                return true
            } catch (AssertionError failure) {
                outputTestFailure(name)
                return false
            }
        }
    }

    def private runTests () {
        def success = true

        tests.each {
            success &= it()
        }

        println()
        if (success) {
            println "SUCCESS"
        } else {
            println "FAILURE"
        }
    }

    def private runTest(setup, test, teardown) {
        setup()
        test()
        teardown()
    }

    def private outputTestSuccess(name) {
        println description + " " + name + ": PASS"
    }

    def private outputTestFailure(name) {
        println description + " " + name + ": FAIL"
    }
}
