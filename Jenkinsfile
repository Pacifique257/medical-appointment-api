pipeline {
    agent any // Utilise l'agent Jenkins sur l'hôte
    environment {
        DOCKER_IMAGE = 'pacifiquedev/medical-appointment'
        DOCKER_CREDENTIALS = 'dockerhub-credentials'
        WORKSPACE = "${env.WORKSPACE}"
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
                sh 'docker run --rm -v "$WORKSPACE":/usr/src/mymaven -w /usr/src/mymaven maven:3.9.3-eclipse-temurin-17 mvn clean package -DskipTests=false'
            }
        }
        stage('Run Tests') {
            steps {
                sh 'docker run --rm -v "$WORKSPACE":/usr/src/mymaven -w /usr/src/mymaven maven:3.9.3-eclipse-temurin-17 mvn test'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t "$DOCKER_IMAGE":latest "$WORKSPACE"'
                script {
                    commit = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    sh "docker tag $DOCKER_IMAGE:latest $DOCKER_IMAGE:${commit}"
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                    sh 'docker push $DOCKER_IMAGE:latest'
                    script {
                        commit = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                        sh "docker push $DOCKER_IMAGE:${commit}"
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
