#!/usr/bin/groovy

@NonCPS
def slaves() {
    return Jenkins.getInstance().getNodes()
}

def names(nodes) {
    def names = []
    for (int i = 0; i < nodes.size(); i++) {
        names.add(nodes[i].name)
    }
    return names
}


