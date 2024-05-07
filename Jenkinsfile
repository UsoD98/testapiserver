pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t testapiserver .'
            }
        }
        stage('Deploy') {
            steps {
                sh 'docker stop testapiserver || true'
                sh 'docker rm testapiserver || true'
                sh 'docker run -d -p 8080:8080 --name testapiserver testapiserver'
            }
        }
    }
}
