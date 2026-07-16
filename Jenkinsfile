pipeline {
    agent any

    tools {
        jdk 'jdk25'
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '15'))
    }

    // Push auf main: Multibranch-Pipeline mit Git-Webhook einrichten (z. B. GitHub Branch Source).
    // Nur der main-Branch führt den Build aus; andere Branches werden übersprungen.
    when {
        branch 'main'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew build --warning-mode all --stacktrace'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
        }
        success {
            archiveArtifacts artifacts: 'build/libs/Basicx-*.jar', fingerprint: true
        }
    }
}
