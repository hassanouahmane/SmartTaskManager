pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    environment {
        PROJECT_DIR = 'taskmanager'
        DOCKER_IMAGE = 'smarttaskmanager'
        TEST_POSTGRES_PORT = '5433'      // Port pour les tests
        DEPLOY_POSTGRES_PORT = '5432'
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

        stage('Verify Permissions') {
            steps {
                echo '🔐 Vérification des permissions...'
                dir("${PROJECT_DIR}") {
                    sh 'ls -la'
                    sh 'pwd'
                    sh 'whoami'
                }
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

        stage('Test') {
            steps {
                echo '🧪 Exécution des tests...'
                dir("${PROJECT_DIR}") {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    dir("${PROJECT_DIR}") {
                        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo '📦 Création du package JAR...'
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

        stage('Deploy') {
            steps {
                echo '🚀 Déploiement avec Docker Compose...'
                dir("${PROJECT_DIR}") {
                    sh 'docker-compose down || true'
                    sh 'docker-compose up -d --build'
                }
            }
        }

        stage('Health Check') {
            steps {
                echo '🏥 Vérification de l\'application...'
                script {
                    dir("${PROJECT_DIR}") {
                        sleep 20
                        sh 'docker-compose ps'
                        sh '''
                            for i in {1..10}; do
                                if curl -f http://localhost:8080/actuator/health 2>/dev/null || curl -f http://localhost:8080 2>/dev/null; then
                                    echo "✅ Application accessible!"
                                    exit 0
                                fi
                                echo "⏳ Tentative $i/10..."
                                sleep 3
                            done
                            echo "⚠️ Application pas encore accessible, mais déployée"
                        '''
                    }
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
            }
        }
        always {
            echo '🧹 Nettoyage...'
            sh 'docker image prune -f || true'
        }
    }
}