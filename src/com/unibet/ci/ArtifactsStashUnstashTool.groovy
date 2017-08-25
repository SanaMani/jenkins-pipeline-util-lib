#!/usr/bin/groovy
package com.unibet.ci

class ArtifactsStashUnstashTool implements Serializable {

    List stashList

    ArtifactsStashUnstashTool() {
        this.stashList = []
    }

    def getStashedArtifactsList() {
        return stashList
    }

    def stash(script, name, dir, include) {
        script.dir(dir) {

            script.echo "Print out build artifacts:"
            script.bat "dir"

            // save test artifact from the build
            script.stash name: name, includes: include
        }

        stashList.add(name)
    }

    def unstash(script, dir, artifacts) {
        script.dir(dir) {
            for (int i = 0; i < artifacts.size(); i++) {
                script.unstash(artifacts[i])
            }
        }
    }

    def unstashAll(script, dir) {
        unstash(script, dir, stashList)
    }

}

