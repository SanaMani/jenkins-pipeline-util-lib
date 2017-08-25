# User Guide

The jenkins-pipeline-util-lib is a Jenkins pipeline shared library that provides reusable groovy utility classes and global variables for defining Jenkins scripted pipelines. 
Please refer to the [Jenkins pipeline shared library guide](https://jenkins.io/doc/book/pipeline/shared-libraries/) for detailed information on how to install and use the library.

## Load library
To load this library, add the following lines to the beginning of your project's Jenkins file:
```
@Library('jenkins-pipeline-util-lib')
import com.unibet.ci.*
```

## API examples
### Here are examples of utility classes from this shared library:


#### __TestManager__
TestManager class is a utility to help manage Jenkins test jobs as well as result collectors that will aggregate test report files generated from those test jobs to specified place.

* __To define a TestManager:__
```
def testManager = new TestManager()
```
    
* __To add a test job to be managed by TestManager instance:__
```
testManager.addTestJob this, new TestJob("job alias", 'job name', [job parameters as a list])
```
e.g.
```
testManager.addTestJob this, new TestJob("LoginLogout", 'Maria - Web - Login Logout - Pipeline', [string(name: 'test.env', value: "${env.testenv}"), [$class: 'MatrixCombinationsParameterValue', combinations: ['BROWSER=chrome', 'BROWSER=chrome incognito'], description: '', name: 'configurations'], string(name: 'tags', value: "${env.tags}"), string(name: 'testartifactsdir', value: "..\\..\\..\\${JOB_NAME}")])
```

* __To add a test report collector for a job:__
```
testManager.addTestResultCollector new TestResultCollector("job alias", 'filter', 'job name', 'target folder')
```
Multiple test report collectors can be added to the same job if more types of reports need to be collected. One example can be in multi-configuration job, test reports are generated within each configuration, so reports from different configurations can be collected by defining multiple test result collectors, e.g.:
```
testManager.addTestResultCollector new TestResultCollector("LoginLogout", 'reports/*chrome.json', 'Maria - Web - Login Logout - Pipeline/BROWSER=chrome', 'aggregate/login/')
testManager.addTestResultCollector new TestResultCollector("LoginLogout", 'reports/*chrome_incognito.json', 'Maria - Web - Login Logout - Pipeline/BROWSER=chrome incognito', 'aggregate/login/')
```

* __To select which tests will be executed:__
```
testManager.selectTests [test_alias_1, test_alias_2, test_alias_3, ...]
```

* __To execute selected test jobs in parallel on slave nodes:__
```
testManager.runSelectedTestJobs this
```
__Note that corresponding test Jenkins jobs have to be predefined. The API will not create job, but instead only trigger them.__

* __To aggregate test reports from test jobs:__
```
testManager.collectTestResults this
```

#### __MavenTool__
MavenTool class is a utility to help loading predefined Maven from Jenkins configuration, parsing POM.xml file and build the Maven project.

* __To define a MavenTool:__
```
def mvnTool = new MavenTool('Maven 3.3.9')
```
The Maven tool name used here must refer to a pre-configured Maven tool in Jenkins "Global Tool Configuration".

* __To get version of a Maven dependency from properties tag in the POM.xml file:__
```
def pom = script.readMavenPom file: 'pom.xml'
def version = MavenTool.getMvnDependencyVersion pom, 'unitard.core.version'
```

* __To check if artifact version is a snapshot:__
```
boolean isSnapshot = MavenTool.isSnapshot version
```

* __To run maven build command:__
```
def mvnTool = new MavenTool('Maven 3.3.9')
mvnTool.mvn this, "clean install"
mvnTool.mvn this, "clean package"
```


#### __MavenProjectBuilder__
MavenProjectBuilder is a utility to help build Maven project. If a project has dependencies on other Maven projects, it will check out other projects from their git repo and run build and install before building and packaging the parent project.

* __To specify project to build as well as dependency projects, and build all of them:__
```
def mvnTool = new MavenTool('Maven 3.3.9')
def mavenProjectBuilder = new MavenProjectBuilder('unitard-maria', 'https://autobuild@bitbucket.unibet.com/bitbucket/scm/taut/unitard-maria.git', "${env.branch}", mvnTool)
mavenProjectBuilder.addDependency new Dependency('unitard-core', 'unitard.core.version', 'https://autobuild@bitbucket.unibet.com/bitbucket/scm/taut/unitard-core.git', "${env.branch}", mvnTool)
mavenProjectBuilder.buildAndPackage this
```

### Here are examples of global variables defined in this shared library:

#### __allSlaves__
This global variable allows to define steps in a groovy closure that are going to be executed in parallel on all Jenkins slave nodes at the pipeline job's workspace.
```
allSlaves 'Deploy', { label ->
    stage("Deploy on Slave ${label}") {
        directory.delete()
        artifactsStashUnstashTool.unstashAll this, '.'
        echo "Print out deployed artifacts on slave ${label} workspace:"
        directory.list()
    }
}
```

#### __directory__
This is a utility class to help with directory operations.
```
directory.list() // list all files under current folder
directory.delete() // clean up the current folder
directory.create(name) // create a folder with specified name
```

#### __nodes__
This is a utility class to help with Jenkins node information.
```
nodes.slaves() // return all hudson.model.Slave instances as a list
nodes.names(nodes) // return names of specified hudson.model.Slave nodes as strings in a list
```

#### __cucumber__
This is utility class to help with Cucumber reports.
```
cucumber.publishReport include, exclude // publish cucumber report as selected by include and exclude filters
cucumber.publishReport include // pushlish cucumber report as selected by include filter
```
e.g.
```
cucumber.publishReport 'aggregate/**/*.json'
```