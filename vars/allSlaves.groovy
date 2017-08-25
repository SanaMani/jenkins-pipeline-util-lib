#!/usr/bin/groovy

def call(body) {

    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = this

    def slaves = nodes.names(nodes.slaves())
    def slaveTasks = [:]
    for (int i = 0; i < slaves.size(); i++) {
        def index = i //this dummy assignment is needed so that index i will not be always the last one in the list.
        def label = slaves[i];
        slaveTasks["${label}"] = {
            node("${label}") {
                body(label)
            }
        }
    }
    parallel slaveTasks
}

