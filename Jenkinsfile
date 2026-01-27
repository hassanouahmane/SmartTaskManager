pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                git(
                    branch: 'main',
                    url: 'https://github.com/hassanouahmane/SmartTaskManager.git',
                    credentialsId: 'github-token'
                )
            }
        }

        stage('Build & Test') {
            steps {
                echo '🔨 Compilation du projet...'
                dir('taskmanager') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
                dir('taskmanager') {
                    sh 'docker build -t smarttaskmanager:latest .'
                }
            }
        }

        stage('Run with Docker Compose') {
            steps {
                echo '🚀 Démarrage avec Docker Compose...'
                dir('taskmanager') {
                    sh 'docker-compose down || true'
                    sh 'docker-compose up -d --build'
                }
            }
        }

        stage('Health Check') {
            steps {
                echo '🏥 Vérification de l\'état de l\'application...'
                script {
                    sleep 15 // Attendre que l'app démarre
                    sh 'docker-compose ps'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline exécuté avec succès!'
        }
        failure {
            echo '❌ Le pipeline a échoué!'
            dir('taskmanager') {
                sh 'docker-compose logs --tail=50'
            }
        }
        always {
            echo '🧹 Nettoyage...'
        }
    }
}