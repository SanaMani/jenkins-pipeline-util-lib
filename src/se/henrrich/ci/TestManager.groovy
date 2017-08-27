#!/usr/bin/groovy
package se.henrrich.ci

class TestManager implements Serializable {

    Map testJobs
    Map testResultsCollectors
    List selectedTestJobs

    TestManager() {
        this.testJobs = [:]
        this.testResultsCollectors = [:]
        this.selectedTestJobs = []
    }

    def addTestJob(script, job) {

        def testjob = {
            script.stage("Test ${job.alias}") {
                script.echo "Trigger ${job.alias} jobs..."
                script.build job: job.name, parameters: job.parameters, propagate: false
            }
        }

        testJobs[job.alias] = testjob
    }

    def addTestResultCollector(collector) {

        def c = [$class: 'CopyArtifact', filter: collector.filter, fingerprintArtifacts: true, optional: true, projectName: collector.project, selector: [$class: 'WorkspaceSelector'], target: collector.target]

        if (!testResultsCollectors.containsKey(collector.alias)) {
            testResultsCollectors[collector.alias] = [c]
        } else {
            testResultsCollectors[collector.alias].add(c)
        }
    }

    def selectTest(alias) {
        selectedTestJobs.add(alias)
    }

    def selectTests(aliasList) {
        selectedTestJobs.addAll(aliasList)
    }

    def runSelectedTestJobs(script) {
        def jobs = [:]
        for (String alias : selectedTestJobs) {
            jobs["Test ${alias}"] = testJobs[alias]
        }

        script.parallel jobs
    }

    def runSelectedTestJobsInSequence(script) {
        for (String alias : selectedTestJobs) {
            script.catchError {
                testJobs[alias]()
            }
        }
    }

    def collectTestResults(script) {
        for (String alias : selectedTestJobs) {
            def collectors = testResultsCollectors[alias]
            for (int i = 0; i < collectors.size(); i++) {
                script.step(collectors[i])
            }
        }
    }

}

class TestJob implements Serializable {

    String alias
    String name
    List parameters

    TestJob(String alias, String name, List parameters) {
        this.alias = alias
        this.name = name
        this.parameters = parameters
    }

}

class TestResultCollector implements Serializable {

    String alias
    String filter
    String project
    String target

    TestResultCollector(String alias, String filter, String project, String target) {
        this.alias = alias
        this.filter = filter
        this.project = project
        this.target = target
    }
}

