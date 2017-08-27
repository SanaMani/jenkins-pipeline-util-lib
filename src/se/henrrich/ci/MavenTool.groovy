#!/usr/bin/groovy
package se.henrrich.ci

class MavenTool implements Serializable {

    String mvnToolName

    MavenTool(mvnToolName) {
        this.mvnToolName = mvnToolName
    }

    static def getMvnDependencyVersion(pom, dependencyTag) {
        return pom.properties.get(dependencyTag)
    }

    static def isSnapshot(version) {
        return version.contains('SNAPSHOT')
    }

    def mvn(script, args) {
        script.bat "${script.tool mvnToolName}/bin/mvn ${args}"
    }
}

