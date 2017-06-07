node {
    def server = Artifactory.newServer url: 'http://artifactory:8081/artifactory', username: 'deployer', password: 'password'
    
    def rtMaven = Artifactory.newMavenBuild()

	withEnv(["JAVA_HOME=/docker-java-home", "PATH+MAVEN=${tool 'Maven'}/bin:${env.JAVA_HOME}/bin"]) {
    	stage 'SCM'
        	git url: 'https://github.com/josephkampf/spring-testing.git'

    	stage 'Artifactory configuration'
        	rtMaven.tool = 'Maven' // Tool name from Jenkins configuration
        	rtMaven.deployer releaseRepo:'libs-release-local', snapshotRepo:'libs-snapshot-local', server: server
        	rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server
        	def buildInfo = Artifactory.newBuildInfo()

    	stage 'Build'
        	rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo

        stage('SonarQube analysis') { 
            sh 'mvn cobertura:cobertura sonar:sonar ' + 
            ' -Dcobertura.report.format=xml -Dsonar.host.url=http://sonarqube:9000'
        }

    	stage 'Publish build info'
        	server.publishBuildInfo buildInfo
    }
}