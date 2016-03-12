package net.jmlproductions.lavalamp

class LavaLampTestCase extends Closure<Void>
{
    def private test
    def private name
    def private onSuccess
    def private onFailure

    LavaLampTestCase(name, setup, test, teardown, onSuccess, onFailure) {
        super(null)
        this.name = name
        this.test = buildTest(setup, test, teardown)
        this.onSuccess = onSuccess
        this.onFailure = onFailure
    }

    def private buildTest(setup, test, teardown) {
        return {
            setup.each { it() }
            test()
            teardown.each { it() }
        }
    }

    def doCall() {
        try {
            test()
            onSuccess(name)
        } catch (AssertionError failure) {
            onFailure(name)
        }
    }
}
