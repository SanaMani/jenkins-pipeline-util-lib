#!/usr/bin/groovy
package com.unibet.ci

class MavenProjectBuilder implements Serializable {

    String projectName
    String repo
    String branch
    String tag
    List dependencies
    MavenTool mvnTool

    MavenProjectBuilder(projectName, repo, branch, mvnTool) {
        this(projectName, null, repo, branch, mvnTool)
    }

    MavenProjectBuilder(projectName, tag, repo, branch, mvnTool) {
        this.projectName = projectName
        this.repo = repo
        this.branch = branch
        this.tag = tag
        this.mvnTool = mvnTool
        this.dependencies = []
    }

    def addDependency(dependency) {
        this.dependencies.add(dependency)
    }

    private def buildAndInstallDependency(script, dependency, alreadyBuilt) {
        script.directory.create dependency.projectName

        script.dir(dependency.projectName) {

            script.echo "Checkout git repo ${dependency.repo} with branch ${dependency.branch}"
            script.git url: dependency.repo, branch: dependency.branch

            def pom = script.readMavenPom file: 'pom.xml'

            for (MavenProjectBuilder dep : dependency.dependencies) {

                script.echo "Dependency: ${dep.projectName} ${dep.repo} ${dep.branch} ${dep.tag}"

                def version = MavenTool.getMvnDependencyVersion pom, dep.tag

                script.echo "Dependency ${dep.projectName} version is " + version

                if (MavenTool.isSnapshot(version)) {
                    if (!alreadyBuilt.contains(dep.projectName))
                        buildAndInstallDependency script, dep, alreadyBuilt
                }
            }

            if (dependency.tag != null) {
                alreadyBuilt.add dependency.projectName
                script.echo "Build and install project ${dependency.projectName}..."
                mvnTool.mvn script, "clean install"
            }
        }
    }

    def buildAndPackage(script) {
        buildAndInstallDependency script, this, []

        script.dir(projectName) {
            script.echo "Build and package project ${projectName}..."
            mvnTool.mvn script, "clean package"
        }
    }
}


