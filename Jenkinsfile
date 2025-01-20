pipeline {
    agent any
    environment {
        GITHUB_REPO = "https://github.com/hieu15402/sell-course.git"
        DOCKER_COMPOSE_FILE = "docker-compose.yml"
        PROJECT_NAME = "sell-course"
    }
    stages {
        stage('Clone Repository') {
            steps {
                echo 'Cloning repository...'
                git branch: 'main', credentialsId: 'jenkins_github', url: "${env.GITHUB_REPO}"
            }
        }
        stage('Build and Start with Docker Compose') {
            steps {
                script {
                    echo 'Stopping any existing services...'
                    sh """
                    docker-compose -f ${DOCKER_COMPOSE_FILE} down || true
                    """
                    echo 'Building and starting services...'
                    sh """
                    docker-compose -f ${DOCKER_COMPOSE_FILE} up --build -d
                    """
                }
            }
        }
    }
    post {
        always {
            echo 'Pipeline execution completed.'
        }
        success {
            echo 'Services started successfully using Docker Compose!'
        }
        failure {
            echo 'Pipeline failed. Check logs for more details.'
        }
    }
}