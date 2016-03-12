package net.jmlproductions.lavalamp

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
    def private success = true

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
        tests << new LavaLampTestCase(name, setup, testClosure, teardown, outputTestSuccess, outputTestFailure)
    }

    def private runTests () {
        tests.each { it() }

        println()
        if (success) {
            println "SUCCESS"
        } else {
            println "FAILURE"
        }
    }

    def private outputTestSuccess = { name ->
        println description + " " + name + ": PASS"
    }

    def private outputTestFailure = { name ->
        success = false
        println description + " " + name + ": FAIL"
    }
}
