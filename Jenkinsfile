node {
    def server = Artifactory.newServer url: 'http://artifactory:8081/artifactory', username: 'deployer', password: 'password'
    
    def rtMaven = Artifactory.newMavenBuild()
    
    def DOCKER_ROOT = '${WORKSPACE}/target/docker'

    def buildInfo = Artifactory.newBuildInfo()
    
	withEnv(["JAVA_HOME=/docker-java-home", "PATH+MAVEN=${tool 'Maven'}/bin:${env.JAVA_HOME}/bin"]) {
    	stage ('SCM') {
        	git url: 'https://github.com/josephkampf/spring-testing.git'
        }

    	stage ('Artifactory configuration') {
        	rtMaven.tool = 'Maven' // Tool name from Jenkins configuration
        	rtMaven.deployer releaseRepo:'libs-release-local', snapshotRepo:'libs-snapshot-local', server: server
        	rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server
        }

    	stage ('Build') {
        	rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
		}

        stage('SonarQube analysis') { 
            sh 'mvn cobertura:cobertura sonar:sonar ' + 
            ' -Dcobertura.report.format=xml -Dsonar.host.url=http://sonarqube:9000'
        }

    	stage ('Publish build info'){
        	server.publishBuildInfo buildInfo
        }
        	
        stage ('Prep Docker Container') {
            sh 'mkdir ' + DOCKER_ROOT
            sh 'cp ${WORKSPACE}/src/docker/* ' + DOCKER_ROOT
            sh 'cp ${WORKSPACE}/src/main/resources/db-schema.sql ' + DOCKER_ROOT
            sh 'mvn -q -Dexec.executable=\'echo\' -Dexec.args=\'${project.artifactId}-${project.version}.war\' --non-recursive exec:exec > warfilename.txt' 
            def WAR_FILE = readFile('warfilename.txt').trim()
            sh 'cp target/' + WAR_FILE + ' ' + DOCKER_ROOT +'/spring-test.war'
        }
            
        stage ('Generate MySQL Container') {
            dir('target/docker') {
               sh 'docker build -t capt_mysql:1 -f Dockerfile.mysql  .'
            }
        }
        
        stage ('Generate Application Container') {
            dir('target/docker') {
               sh 'docker build -t spring-testing:1 -f Dockerfile.tomcat  .'
            }
        }

    }
}