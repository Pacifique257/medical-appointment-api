pipeline {
    agent any
    stages {
        stage('Construction') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Déploiement') {
            steps {
                sh 'docker build -t medical-appointment .'
                sh 'docker push medical-appointment'
            }
        }
    }
}
