pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                    sh '''
                        git config --global user.email "jenkins@example.com"
                        git config --global user.name "Jenkins"
                        git clone https://x-access-token:${GITHUB_TOKEN}@github.com/hassanmouhamane/SmartTaskManager.git .
                        git checkout main

                        # Verify pom.xml exists
                        if [ -f "pom.xml" ]; then
                            echo "✅ pom.xml found in root directory"
                        else
                            echo "❌ pom.xml not found in root directory"
                            find . -name "pom.xml" -type f
                            exit 1
                        fi
                    '''
                }
            }
        }

        stage('Build & Package') {
            steps {
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                failure {
                    echo '❌ Tests failed! Check test reports.'
                    // You can archive test results here
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }

        stage('Package JAR') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Check if Dockerfile exists
                    if (fileExists('Dockerfile')) {
                        sh 'docker build -t smarttaskmanager:latest .'
                    } else {
                        echo '⚠️  No Dockerfile found. Skipping Docker build.'
                    }
                }
            }
        }

        stage('Run with Docker Compose') {
            steps {
                script {
                    if (fileExists('docker-compose.yml')) {
                        sh 'docker-compose up -d --build'
                    } else {
                        echo '⚠️  No docker-compose.yml found. Skipping Docker Compose.'
                    }
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline executed successfully!'

            // Archive the built JAR
            archiveArtifacts 'target/*.jar'
        }
        failure {
            echo '❌ Pipeline failed!'

            // Debug info
            sh '''
                echo "=== Build directory contents ==="
                ls -la target/ 2>/dev/null || echo "No target directory"
                echo "=== Maven dependency tree ==="
                mvn dependency:tree -DskipTests 2>/dev/null | head -50 || true
            '''
        }
        always {
            // Clean up Docker resources
            sh 'docker-compose down 2>/dev/null || true'

            // Clean workspace (optional)
            // cleanWs()
        }
    }
}