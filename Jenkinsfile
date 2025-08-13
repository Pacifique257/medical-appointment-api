pipeline {
    agent any // Utilise le conteneur Jenkins comme agent principal
    environment {
        DOCKER_IMAGE = 'pacifiquedev/medical-appointment'
        DOCKER_CREDENTIALS = 'dockerhub-credentials'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: '*/main']],
                          userRemoteConfigs: [[url: 'https://github.com/Pacifique257/medical-appointment-api', credentialsId: 'a8ca7ec4-7429-4b26-bcd8-14dacf0a8552']]])
            }
        }
        stage('Build Maven') {
            steps {
                sh 'docker run --rm -v $(pwd):/usr/src/mymaven -w /usr/src/mymaven maven:3.9.3-eclipse-temurin-17 mvn clean package -DskipTests=false'
            }
        }
        stage('Run Tests') {
            steps {
                sh 'docker run --rm -v $(pwd):/usr/src/mymaven -w /usr/src/mymaven maven:3.9.3-eclipse-temurin-17 mvn test'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    commit = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    app = docker.build("${DOCKER_IMAGE}:latest")
                    appVersion = docker.build("${DOCKER_IMAGE}:${commit}")
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS) {
                        app.push()
                        appVersion.push()
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Build, tests et push Docker terminés avec succès !'
        }
        failure {
            echo 'Le pipeline a échoué. Vérifie les logs !'
        }
    }
}
