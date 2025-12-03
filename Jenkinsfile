pipeline {
    agent { label 'jenkins_slave1' }

    environment {
        SONAR_TOKEN = credentials('sonar-token')     // нужно создать в Jenkins → Credentials - squ_d5aa7279b8650b2a808fa614dac04fcfa9a9b967
        NEXUS_CREDS = credentials('nexus-creds')
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
                    allure(
                        includeProperties: false,
                        results: [[path: "build/allure-results"]]
                    )
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
                    -PnexusUser=${NEXUS_CREDS_USR} \
                    -PnexusPass=${NEXUS_CREDS_PSW}
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
