pipeline {
    agent any

    tools {
        maven "MAVEN"
    }

    stages {
        stage('Clean workspace') {
            steps {
               cleanWs()
            }
        }

        stage("Checkout from SCM"){
            steps {
              git branch: 'master', credentialsId: 'GitHub', url: 'https://github.com/RossonGabriel/whatsapp-clone'
            }
        }

        stage("Test Backend Application"){
            steps {
                dir('whatsappclone') {
                    bat "mvn -v"
                    bat "mvn test"
                }
            }
        }

        stage("Build Backend Application"){
            steps {
                dir('whatsappclone') {
                    bat "mvn clean package -DskipTests"
                }
            }
        }

        stage("Deploy Application Backend"){
            steps {
                script {
                    bat 'docker compose up -d --build backend'
                }
            }
        }

        stage("Deploy Application Frontend"){
            steps {
                script {
                    bat 'docker compose up -d --build frontend'
                }
            }
        }
    }
}