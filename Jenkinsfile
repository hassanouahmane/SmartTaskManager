pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }
     environment {

            GITHUB_TOKEN = credentials('github-token')

            DOCKER_IMAGE = "taskmanager_app:latest"
        }


    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/hassanmouhamane/SmartTaskManager.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t smarttaskmanager:latest .'
            }
        }

        stage('Run with Docker Compose') {
            steps {
                sh 'docker-compose up -d --build'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline executed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
