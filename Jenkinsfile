pipeline {
    agent { label 'jenkins_slave1' }

    environment {
        SONAR_TOKEN = credentials('sonar-token')
        NEXUS = credentials('nexus-creds')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh "./gradlew clean build"
            }
        }

        stage('Unit Tests') {
            steps {
                sh "./gradlew test"
            }
            post {
                always {
                    allure([
                        includeProperties: false,
                        results: [[path: "build/allure-results"]]
                    ])
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarServer') {
                    sh """
                        ./gradlew sonarqube \
                        -Dsonar.projectKey=myapp \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Publish Artifact to Nexus') {
            steps {
                sh """
                    ./gradlew publish \
                    -PnexusUser=${NEXUS_USR} \
                    -PnexusPass=${NEXUS_PSW}
                """
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        }
    }
}
