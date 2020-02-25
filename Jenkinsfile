pipeline {
    agent any
    tools {
        maven 'Maven 3.6.3'
        jdk 'jdk8'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true install' 
            }
            post {
                always {
                    echo 'Archiving coverage results...'
                    jacoco(execPattern: '**/**.exec', classPattern: '**/classes', sourcePattern: '**/src/main/java')
                    echo 'Archiving test results...'
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    echo 'Archiving artifacts...'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
    }
}
