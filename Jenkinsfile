pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    environment {
        PROJECT_DIR = 'taskmanager'
        DOCKER_IMAGE = 'smarttaskmanager'
    }

    stages {
        stage('Setup & Clean') {
            steps {
                echo '🧹 Préparation de l\'environnement...'
                script {
                    // Nettoyer complètement
                    sh 'rm -rf taskmanager/target/ || true'

                    // S'assurer des bonnes permissions
                    sh 'chmod -R 755 . || true'
                }
            }
        }

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

        stage('Build') {
            steps {
                echo '🔨 Compilation du projet...'
                dir("${PROJECT_DIR}") {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Package') {
            steps {
                echo '📦 Création du package JAR (sans tests)...'
                dir("${PROJECT_DIR}") {
                    sh 'mvn package -DskipTests'
                }
            }
            post {
                success {
                    dir("${PROJECT_DIR}") {
                        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
                dir("${PROJECT_DIR}") {
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Stop Previous Deployment') {
            steps {
                echo '🛑 Arrêt du déploiement précédent...'
                dir("${PROJECT_DIR}") {
                    sh '''
                        # Arrêter docker-compose
                        docker-compose down || true

                        # Forcer l'arrêt et la suppression des conteneurs PostgreSQL
                        docker stop postgres_db 2>/dev/null || true
                        docker rm postgres_db 2>/dev/null || true

                        # Arrêter l'application
                        docker stop spring-app 2>/dev/null || true
                        docker rm spring-app 2>/dev/null || true

                        # Attendre que les ports se libèrent
                        echo "Attente de la libération des ports..."
                        sleep 10

                        # Vérifier que le port 5432 est libre
                        if lsof -Pi :5432 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
                            echo "⚠️ Port 5432 encore occupé, nettoyage forcé..."
                            PID=$(lsof -ti :5432)
                            if [ ! -z "$PID" ]; then
                                kill -9 $PID || true
                                sleep 3
                            fi
                        else
                            echo "✅ Port 5432 est libre"
                        fi
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 Déploiement avec Docker Compose...'
                dir("${PROJECT_DIR}") {
                    sh 'docker-compose up -d --build'
                }
            }
        }

        stage('Health Check') {
            steps {
                echo '🏥 Vérification de l\'application...'
                script {
                    dir("${PROJECT_DIR}") {
                        sleep 25
                        sh 'docker-compose ps'
                        sh '''
                            for i in {1..15}; do
                                if curl -f http://localhost:8080/actuator/health 2>/dev/null || curl -f http://localhost:8080 2>/dev/null; then
                                    echo "✅ Application accessible!"
                                    exit 0
                                fi
                                echo "⏳ Tentative $i/15..."
                                sleep 4
                            done
                            echo "⚠️ Application déployée mais vérification timeout"
                        '''
                    }
                }
            }
        }

        stage('Display Logs') {
            steps {
                echo '📋 Affichage des logs...'
                dir("${PROJECT_DIR}") {
                    sh 'docker-compose logs --tail=30 || true'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline exécuté avec succès!'
            echo '🎉 Application disponible sur http://localhost:8080'
        }
        failure {
            echo '❌ Le pipeline a échoué!'
            dir("${PROJECT_DIR}") {
                sh 'docker-compose logs --tail=100 || true'
                sh 'docker ps -a || true'
            }
        }
        always {
            echo '🧹 Nettoyage des images inutilisées...'
            sh 'docker image prune -f || true'
        }
    }
}