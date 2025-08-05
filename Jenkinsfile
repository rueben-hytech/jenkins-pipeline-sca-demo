pipeline {
    agent any

    tools {
        jdk 'OpenJDK'
        maven 'mvn' 
    }

    stages {
        stage('SCM') {
            steps {
                echo '🛠️ [DEBUG] Starting SCM stage: Cloning repository...'
                git credentialsId: 'Private-Repo-SCA-Demo',
                    branch: 'main', 
                    changelog: false, 
                    poll: false, 
                    url: 'https://github.com/rueben-hytech/jenkins-pipeline-sca-demo.git'
                echo '✅ [DEBUG] SCM stage completed.'
            }
        }

        stage('Build') {
            steps {
                echo '🏗️ [DEBUG] Starting Build stage: Running Maven to build the project...'
                sh 'mvn clean package -DskipTests'
                sh 'mvn dependency:copy-dependencies'
                echo '✅ [DEBUG] Build stage completed.'
            }
        }

        stage('ODC') {
            steps {
                echo '🔍 [DEBUG] Starting ODC stage: Running OWASP Dependency-Check...'
                dependencyCheck additionalArguments: '--format XML --format HTML', 
                                 nvdCredentialsId: 'nvd-api-key', 
                                 odcInstallation: 'ODC'
                echo '✅ [DEBUG] ODC stage completed.'
            }
        }

        stage('Snyk SCA') {
            steps {
                echo '🧪 [DEBUG] Starting Snyk scan on pom.xml...'
                snykSecurity(
                    snykInstallation: 'snyk@latest',
                    snykTokenId: 'Synk-API',
                    projectName: 'vulnerable-app',
                    targetFile: 'pom.xml',
                    monitorProjectOnBuild: true,
                    severity: 'medium',
                    failOnIssues: false
                )
                echo '✅ [DEBUG] Snyk scan completed.'
            }
        }

        stage('Debug Tool Path') {
            steps {
                echo "✅ [DEBUG] Snyk tool location: ${tool 'snyk@latest'}"
                sh 'ls -la $(which snyk) || echo "Snyk not found in PATH"'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔎 [DEBUG] Starting SonarQube analysis...'
                withSonarQubeEnv('SonarQube-Server') { // Must match your Jenkins SonarQube config name
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=rueben-hytech_jenkins-pipeline-sca-demo_c6f473bb-2edf-4a50-bc2e-15448921d358 \
                          -Dsonar.projectName='jenkins-pipeline-sca-demo'
                    """
                }
                echo '✅ [DEBUG] SonarQube analysis submitted.'
            }
        }

        stage('Wait for Quality Gate') {
            steps {
                echo '⏳ [DEBUG] Waiting for SonarQube Quality Gate result...'
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
                echo '✅ [DEBUG] Quality Gate passed.'
            }
        }     
   
        stage('Publish SCA Report') {
            steps {
                echo '📄 [DEBUG] Publishing SCA report using dependencyCheckPublisher...'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
                echo '✅ [DEBUG] SCA report published.'
            }
        }

        stage('Publish HTML Report') {
            steps {
                echo '🌐 [DEBUG] Publishing HTML report to Jenkins UI...'
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: '.',
                    reportFiles: 'dependency-check-report.html',
                    reportName: 'Dependency-Check Report'
                ])
                echo '✅ [DEBUG] HTML report published.'
            }
        }
    }

    post {
        always {
            echo '📦 [DEBUG] Archiving dependency-check report artifacts...'
            archiveArtifacts artifacts: '**/dependency-check-report.*', fingerprint: true
            echo '✅ [DEBUG] Artifact archiving completed.'
        }
    }
}
