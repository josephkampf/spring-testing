node {
    def server = Artifactory.server 'artifactory'
    def rtMaven = Artifactory.newMavenBuild()

	withEnv(["JAVA_HOME=${ tool 'java-8-openjdk' }", "PATH+MAVEN=${tool 'maven'}/bin:${env.JAVA_HOME}/bin"]) {
    	stage 'SCM'
        	git url: 'https://github.com/josephkampf/spring-testing.git'

    	stage 'Artifactory configuration'
        	rtMaven.tool = 'maven' // Tool name from Jenkins configuration
        	rtMaven.deployer releaseRepo:'libs-release-local', snapshotRepo:'libs-snapshot-local', server: server
        	rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server
        	def buildInfo = Artifactory.newBuildInfo()

    	stage 'Build'
        	rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo

        stage('SonarQube analysis') { 
            withSonarQubeEnv('SonarQube') { 
                sh 'mvn cobertura:cobertura sonar:sonar ' + 
                ' -Dcobertura.report.format=xml'
            }
        }

    	stage 'Publish build info'
        	server.publishBuildInfo buildInfo
    }
}