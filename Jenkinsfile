pipeline {
    agent {
        docker {
            image 'maven:3.9.3-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    environment {
        DOCKER_IMAGE = 'pacifiquedev/medical-appointment'
        DOCKER_CREDENTIALS = 'dockerhub-credentials'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Maven') {
            steps {
                sh 'mvn clean package -DskipTests=false'
            }
        }
        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Tag avec latest et commit SHA
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

