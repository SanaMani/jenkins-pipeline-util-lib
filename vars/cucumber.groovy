#!/usr/bin/groovy

def publishReport(include, exclude) {
    step([$class: 'CucumberReportPublisher', fileExcludePattern: exclude, fileIncludePattern: include, ignoreFailedTests: false, jenkinsBasePath: '', jsonReportDirectory: '', missingFails: false, parallelTesting: true, pendingFails: false, skippedFails: false, undefinedFails: false])
}

def publishReport(include) {
    publishReport(include, '')
}



